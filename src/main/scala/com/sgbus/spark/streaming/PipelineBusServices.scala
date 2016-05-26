package com.sgbus.spark.streaming

import java.text.SimpleDateFormat
import java.util.Date

import com.sgbus.spark.business.Service
import com.sgbus.utils.AppConf
import org.apache.spark.streaming.dstream.DStream

import scala.util.Try

object PipelineBusServices extends AppConf {

  val timestampFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss")
  val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMM")

  // Main Pipeline
  def pipeline(rawStream: DStream[String]) = parseLines(rawStream)

  // Parsing Log Lines
  def parseLines(logLineStream: DStream[String]) = {
    //logLineStream.map(logLine => parseLineToCaseClass(logLine))
    logLineStream.foreachRDD(rdd => rdd.foreach(message => println(message)))
  }




  def parseLineToCaseClass(logLine: String): Option[Service] = {
    val lineSplitted = logLine.split('!')
    if (lineSplitted.length >= 5) {

      val busStationId = lineSplitted.apply(1)
      val busId = lineSplitted.apply(2)
      val isOperating = lineSplitted.apply(3)
      val nextArrivalTime = lineSplitted.apply(4)
      val seats = lineSplitted.apply(5)
      val timestampArrival = Try(timestampFormat.parse(nextArrivalTime))
      timestampArrival.isSuccess match {
        case false =>
          None
        case true =>
          Some(
            Service(
              busStationId,
              busId,
              isOperating,
              timestampArrival.get,
              seats
          ))
      }
    }
    else {
      None
    }
  }

}
