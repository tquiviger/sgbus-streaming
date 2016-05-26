package com.sgbus.spark.streaming

import java.text.SimpleDateFormat


import com.sgbus.utils.AppConf
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkContext, SparkConf}

object Main extends App with AppConf {

  val timestampFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss")
  val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMM")

  /*  Spark Configuration */
  val sparkConf =
    new SparkConf()
      .setAppName(AppName)
      .set("es.index.auto.create", "true")
      .setMaster(SparkMaster)


  val sc = new SparkContext(sparkConf)
  val ssc = new StreamingContext(sc, SparkBatchWindow)

  /* Kafka Configuration */
  val kafkaParams = Map[String, String]("metadata.broker.list" -> KafkaBroker)

  /* Pipeline */
  val streamCis = KafkaUtils
    .createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set(KafkaTopicSgBus))
    .map(_._2)

  PipelineBusServices.pipeline(streamCis)

  ssc.start()
  ssc.awaitTermination()


}
