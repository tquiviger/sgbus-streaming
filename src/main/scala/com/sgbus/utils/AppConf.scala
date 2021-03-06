package com.sgbus.utils

import com.typesafe.config.ConfigFactory
import org.apache.spark.streaming.Seconds

trait AppConf {

  val conf = ConfigFactory.load()

  val AppName = conf.getString("AppName")
  val SparkStreamName = conf.getString("SparkStreamName")
  val SparkBatchWindow = Seconds(conf.getInt("SparkBatchWindow"))
  val SparkMaster = conf.getString("SparkMaster")
  val WindowDuration = conf.getInt("WindowDuration")

  val KafkaBroker = conf.getString("KafkaBroker")
  val KafkaTopicSgBus = conf.getString("KafkaTopicSgBus")
  val NbThreadsPerKafkaTopic = conf.getString("NbThreadsPerKafkaTopic")

  val ESBroker = conf.getString("ESBroker")
  val ESPort = conf.getString("ESPort")
  val ESIndex = conf.getString("ESIndex")

}
