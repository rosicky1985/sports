package com.nbb.spider.dao

import java.sql.SQLException

import com.bestv.cps.service.rest.{FailureType, Failure}
import com.nbb.spider.config.Configuration

import scala.slick.session.Database

trait DaoUtils extends Configuration{
  val db = Database.forURL(url = "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8".format(dbHost, dbPort, dbName),
    user = dbUser, password = dbPassword, driver = "com.mysql.jdbc.Driver")
  /**
   * Produce database error description.
   *
   * @param e SQL Exception
   * @return database error description
   */
  protected def databaseError(e: SQLException) =
    Failure("%d: %s".format(e.getErrorCode, e.getMessage), FailureType.DatabaseFailure)

  /**
   * Produce customer not found error description.
   *
   * @param id id of the customer
   * @return not found error description
   */
  protected def notFoundError(id: Long) =
    Failure("Entity id=%d does not exist".format(id), FailureType.NotFound)
}