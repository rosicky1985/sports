package com.nbb.spider.rest

import akka.actor.Actor
import com.nbb.spider.finance.sina.StockRest
import com.nbb.spider.news.baidu.NewsRest

/**
 * REST Service actor.
 */
class RestServiceActor extends Actor
  with StockRest with NewsRest{

  implicit def actorRefFactory = context

  def receive = runRoute(
    stockRest ~ newsRest)

}

