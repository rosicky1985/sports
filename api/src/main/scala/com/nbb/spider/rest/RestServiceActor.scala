package com.nbb.spider.rest

import akka.actor.Actor
import com.nbb.spider.finance.sina.StockRest

/**
 * REST Service actor.
 */
class RestServiceActor extends Actor
with StockRest {

  implicit def actorRefFactory = context

  def receive = runRoute(
    stockRest)

}

