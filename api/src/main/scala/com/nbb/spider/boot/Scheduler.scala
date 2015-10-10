package com.nbb.spider.boot

import com.nbb.spider.finance.sina.Stock
import com.nbb.spider.finance.sina.StockDao

/**
 * Created by rosicky on 15-9-21.
 */
trait Scheduler {
  import cronish._
  import cronish.dsl._
  val stockDao = new StockDao
  //val cron = Cron("0","20","10","*","*","*","*")
  val cron = Cron("0","0","17","*","*","*","*")
  task {
    val stocks = Stock.execute
    if (stocks.isRight)
      stocks.right.get.foreach(s => stockDao.create(s))
  } executes cron
}
