package com.sgbus.spark.business

import java.util.Date

import com.google.gson.Gson

case class Service(timestamp: Date,
                   busStationId: String,
                   busId: String,
                   isOperating: String,
                   waitingTimeBus1: Double,
                   availableSeatsBus1: String,
                   waitingTimeBus2: Double,
                   availableSeatsBus2: String,
                   waitingTimeBus3: Double,
                   availableSeatsBus3: String
                  )

case class Stat(
                 timestamp: String,
                 stat_type: String,
                 key: String,
                 value: Double
               ){
  def toJson() = new Gson().toJson(this)
}
