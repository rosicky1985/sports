package com.nbb.spider.news.baidu

import java.sql.SQLException

import com.bestv.cps.service.rest.Failure
import com.nbb.spider.dao.DaoUtils

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database.threadLocalSession
class NewsDao extends DaoUtils {

  def create(newsList:List[News]) : Either[Failure,List[News]] = {
    try{
      db.withSession {
        NewsORM.insertAll(newsList: _*)
      }
      Right(newsList)
    }catch {
      case e:SQLException => Left(databaseError(e))
    }
  }

  import org.scala_tools.time.Imports._
  def dayrange(targetDay: DateTime) = {
    val today = targetDay.toLocalDate
    val tomorrow = today.plusDays(1)
    val startOfToday = today.toDateTimeAtStartOfDay(targetDay.getZone)
    val startOfTomorrow = tomorrow.toDateTimeAtStartOfDay(targetDay.getZone)
    (startOfToday,startOfTomorrow)
  }
  def query(q: NewsQuery):Either[Failure, List[News]] = {
    try{
      implicit val typeMapperDelegate = NewsORM.dateTypeMapper
      val today = SimpleFunction.unary[DateTime,DateTime]("date")
      val query = for{ n <- NewsORM if {
        Seq(
          q.category.map(n.category like "%%%s%%".format(_)),
          q.createdDay.map(today(n.created) === new DateTime(_).toLocalDate.toDateTimeAtStartOfDay),
          q.targetDay.map(today(n.d_targetTime) === new DateTime(_).toLocalDate.toDateTimeAtStartOfDay)
        ).flatten match {
          case Nil => ConstColumn.TRUE
          case seq => seq.reduce(_ && _)
        }
      }} yield n
      Right(db.withSession(query.list))
    }catch {
      case e:SQLException => Left(databaseError(e))
    }
  }
}