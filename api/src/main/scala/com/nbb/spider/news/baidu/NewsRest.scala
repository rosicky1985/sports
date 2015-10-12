package com.nbb.spider.news.baidu

import akka.event.slf4j.SLF4JLogging
import com.bestv.cps.service.rest.RestUtils
import spray.http.HttpHeaders.RawHeader
import spray.http._
import spray.routing.RequestContext

trait NewsRest extends SLF4JLogging with RestUtils {

  import java.util.Date

  val newsDao = new NewsDao

  val newsRest = respondWithMediaType(MediaTypes.`application/json`) {
    respondWithHeaders(RawHeader("Access-Control-Allow-Origin", "*"), RawHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")) {
      path("news") {
        post {
          ctx: RequestContext =>
            handleRequest(ctx) {
              log.debug("执行百度新闻抓取...");
              val news = News.run()
              newsDao.create(news)
              Right(news)
            }
        } ~
          get {
            parameters('category.as[String] ?, 'targetDay.as[Date] ?, 'createdDay.as[Date] ?).as(NewsQuery) {
              q: NewsQuery => {
                ctx: RequestContext =>
                  handleRequest(ctx) {
                    log.debug("查询News的条件: %s".format(q))
                    newsDao.query(q)
                  }
              }
            }
          }
      } ~
        path("news" / "target") {
          get {
            ctx: RequestContext =>
              handleRequest(ctx) {
                newsDao.distinct_target
              }
          }
        } ~
        path("news" / "created") {
          get {
            ctx: RequestContext =>
              handleRequest(ctx) {
                newsDao.distinct_created
              }
          }
        } ~
        path("news" / "category") {
          get {
            ctx: RequestContext =>
              handleRequest(ctx) {
                newsDao.distinct_category
              }
          }
        }
    }
  }
}