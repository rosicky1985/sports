package com.nbb.spider.dao

import java.sql.SQLException

import com.bestv.cps.service.rest.{FailureType, Failure}

trait DaoUtils {
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