package com.nbb.spider.finance.sina

import akka.event.slf4j.SLF4JLogging
import com.bestv.cps.service.rest.RestUtils
import spray.http._
import spray.routing.RequestContext
import spray.http.HttpHeaders.RawHeader
import java.util.Date

/**
 * REST Service
 */
trait StockRest extends SLF4JLogging with RestUtils {

  val stockDao = new StockDao

  val stockRest = respondWithMediaType(MediaTypes.`application/json`) {
    respondWithHeaders(RawHeader("Access-Control-Allow-Origin","*"),RawHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS")) {
      path("stock") {
        post {
          ctx: RequestContext =>
            handleRequest(ctx) {
              log.debug("立即执行Stock下载...")
              val stocks = Stock.execute
              if (stocks.isRight)
                stocks.right.get.foreach(s => stockDao.create(s))
              stocks
            }
        } ~
          get {
            parameters('after.as[Date] ?).as(StockQuery) {
              q:StockQuery => {
                ctx: RequestContext =>
                  handleRequest(ctx) {
                    log.debug("查询Stock条件: %s".format(q))
                    stockDao.query(q)
                  }
              }
            }
          }
      }
    }
  }
}