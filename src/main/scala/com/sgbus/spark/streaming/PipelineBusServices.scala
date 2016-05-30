package com.sgbus.spark.streaming

import java.text.SimpleDateFormat

import com.sgbus.spark.business.{Service, Stat}
import com.sgbus.utils.AppConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream
import org.elasticsearch.spark._
import org.joda.time.{DateTime, LocalDateTime}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.util.Try

object PipelineBusServices extends AppConf {

  val timestampFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss")
  val fmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss")

  // Main Pipeline
  def pipeline(rawStream: DStream[String]) = saveToElastic(computeMeanWaitingTime(processService(filterServices(parseLines(rawStream)))))

  // Parsing Log Lines
  def parseLines(logLineStream: DStream[String]): DStream[Option[Service]] = {
    logLineStream.map(logLine => parseLineToCaseClass(logLine))
  }

  def filterServices(dstream: DStream[Option[Service]]): DStream[Service] = {
    dstream.filter({
      case None => false
      case _ => true
    })
      .map(_.get)
  }

  def processService(dstream: DStream[Service]): DStream[(String, (Double, Int))] = {
    dstream.flatMap(toto => List(
      (toto.busId, (toto.waitingTimeBus1, 1)),
      (toto.busId, (toto.waitingTimeBus2, 1)),
      (toto.busId, (toto.waitingTimeBus3, 1))
    ))
  }

  def computeMeanWaitingTime(dstream: DStream[(String, (Double, Int))]): DStream[Stat] = {
    dstream.reduceByKeyAndWindow((a, b) => (a._1 + b._1, a._2 + b._2), windowDuration = Seconds(WindowDuration))
      .map(stat =>
        Stat(
          new LocalDateTime().withSecondOfMinute(0).withMillisOfSecond(0).toString(),
          "meanWaitingTimeByBus",
          stat._1,
          (math floor (stat._2._1 / stat._2._2) * 100) / 100)

      )
  }

  def saveToElastic(dstream: DStream[Stat]) = {
    dstream.foreachRDD(_.saveToEs(ESIndex))
  }


  def parseLineToCaseClass(logLine: String): Option[Service] = {
    val lineSplitted = logLine.split('"').apply(1).split('|')
    if (lineSplitted.length >= 10) {
      val timestamp = Try(timestampFormat.parse(lineSplitted.apply(0)))
      val busStationId = lineSplitted.apply(1)
      val busId = lineSplitted.apply(2)
      val isOperating = lineSplitted.apply(3)
      val waitingTimeBus1 = Try(lineSplitted.apply(4).toDouble)
      val availableSeatsBus1 = lineSplitted.apply(5)
      val waitingTimeBus2 = Try(lineSplitted.apply(6).toDouble)
      val availableSeatsBus2 = lineSplitted.apply(7)
      val waitingTimeBus3 = Try(lineSplitted.apply(8).toDouble)
      val availableSeatsBus3 = lineSplitted.apply(9)
      timestamp.isSuccess && waitingTimeBus1.isSuccess && waitingTimeBus2.isSuccess && waitingTimeBus3.isSuccess match {
        case false =>
          None
        case true =>
          Some(
            Service(
              timestamp.get,
              busStationId,
              busId,
              isOperating,
              waitingTimeBus1.get,
              availableSeatsBus1,
              waitingTimeBus2.get,
              availableSeatsBus2,
              waitingTimeBus3.get,
              availableSeatsBus3
            ))
      }
    }
    else {
      None
    }
  }

}
