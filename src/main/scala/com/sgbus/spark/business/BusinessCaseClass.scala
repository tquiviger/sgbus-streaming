package com.sgbus.spark.business

import java.util.Date

case class Service(busStationId: String,
                   busId: String,
                   isOperating: String,
                   nextArrivalTime: Date,
                   seats: String
                  )
