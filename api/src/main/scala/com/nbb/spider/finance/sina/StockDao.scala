package com.nbb.spider.finance.sina

import java.sql.SQLException

import com.bestv.cps.service.rest.Failure
import com.nbb.spider.dao.DaoUtils

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database.threadLocalSession


class StockDao extends DaoUtils {
  def query(q: StockQuery): Either[Failure, _] = {
    try {
      implicit val typeMapper = Stocks.dateTypeMapper
      val query = for {s <- Stocks if {
        Seq(
          q.after.map(s.time < _)
        ).flatten match {
          case Nil => ConstColumn.TRUE
          case seq => seq.reduce(_ && _)
        }
      }} yield s;
      Right(db.withSession(query.list))
    }catch {
      case e:SQLException => Left(databaseError(e))
    }
  }



  def create(stock: Stock): Either[Failure, Stock] = {
    try {
      db.withSession {
        Stocks.insert(stock)
      }
      Right(stock)
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }
}