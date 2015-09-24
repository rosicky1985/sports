package com.nbb.spider.finance.sina


/**
 * Created by rosicky on 15-9-19.
 */
import java.util.Date

import akka.event.slf4j.SLF4JLogging
import com.bestv.cps.service.rest.{Failure, FailureType}

import scala.slick.driver.MySQLDriver.simple._

case class Stock(name:String,code:String,price:String,float:String,time:Date)
case class StockQuery(after: Option[Date])

object Stocks extends Table[Stock]("stock_sina"){
  def name = column[String]("name")
  def code = column[String]("code")
  def price = column[String]("price")
  def float = column[String]("float")
  def time = column[Date]("time")

  def * = name ~ code ~ price ~ float ~ time <> (Stock.apply _, Stock.unapply _)

  implicit val dateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Timestamp](
  {
    ud => new java.sql.Timestamp(ud.getTime)
  }, {
    sd => new java.util.Date(sd.getTime)
  })
}

object Stock extends SLF4JLogging {
  def execute = {
    try{
      Right(run)
    }catch {
      case ex: Exception => Left(Failure(ex.toString,FailureType.InternalError))
    }
  }
  private def run:Array[Stock] = {
    import dispatch._
    import Defaults._
    val run_time = new Date()
    val codes = Map(
      "s_sh000001" -> ("上证指数","D"),
      "s_sz399001" -> ("深证成指","D"),
      "s_sz399006" -> ("创业板指","D"),
      "sz300104" -> ("乐视网","B"),
      "rt_hkHSI" -> ("恒生指数","C"),
      "gb_$dji" -> ("道琼斯","A"),
      "gb_baba" -> ("阿里巴巴","A"),
      "hk00700" -> ("腾讯控股","C")
    )
    val hq_sina = url("http://hq.sinajs.cn/rn="+new java.util.Date().getTime +"&list=" + codes.keys.mkString(","))
    val stock = Http(hq_sina OK as.String)
    val body = stock().split("\n")
    log.debug("原始响应.......")
    log.debug(stock())
    val pattern = """var\s(.*)="(.*)"""".r
    val result:Array[Stock] = body.map( l => {
      val m = pattern.findAllIn(l).matchData
      if(m.hasNext){
        val m1 = m.next()
        val key = m1.group(1)
        val rawValue = m1.group(2)
        val define = codes.get(key.replace("hq_str_",""))
        if(!define.isEmpty){
          val name = define.get._1
          val method = define.get._2
          val s = rawValue.split(",")
          val parsed = method match {
            case "A" => {
              (s(1),s(2))
            }
            case "D" => {
              (s(1),s(3))
            }
            case "B" => {
              val today = s(3)
              val yesterday = s(2)
              val diff = "%.2f".format((today.toDouble - yesterday.toDouble) / yesterday.toDouble * 100)
              (today,diff)
            }
            case "C" => {
              val today = s(6)
              val yesterday = s(3)
              val diff = "%.2f".format((today.toDouble - yesterday.toDouble) / yesterday.toDouble * 100)
              (today,diff)
            }
          }
          Stock(name,key.replace("hq_str_",""),parsed._1,parsed._2,run_time)
        }else{
          Stock("some","some","some","some",run_time)
        }
      }else{
        Stock("some","some","some","some",run_time)
      }
    })
    log.debug("计算结果 ...")
    result.foreach(l => log.debug(l.toString))
    result
  }
}


