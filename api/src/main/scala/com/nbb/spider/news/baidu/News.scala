package com.nbb.spider.news.baidu

import akka.event.slf4j.SLF4JLogging
import org.joda.time.DateTime
import org.jsoup._
import java.text.SimpleDateFormat
import java.util.Date



case class News(
                 created: DateTime,
                 key: String, category: String, id: Int,
                 title: String, author: Option[String], summary: String,
                 s_targetTime: String, d_targetTime: Option[DateTime],
                 follow_num: Option[Int], follow_href: Option[String]) {
}

case class NewsQuery(category:Option[String], targetDay:Option[Date], createdDay:Option[Date])

import scala.slick.driver.MySQLDriver.simple._
object NewsORM extends Table[News]("news_baidu"){
  def created = column[DateTime]("created")
  def key = column[String]("key")
  def category = column[String]("category")
  def title = column[String]("title")
  def id = column[Int]("id")
  def author = column[String]("author",O.Nullable)
  def summary = column[String]("summary")
  def s_targetTime = column[String]("s_targetTime")
  def d_targetTime = column[DateTime]("d_targetTime",O.Nullable)
  def follow_num = column[Int]("follow_num",O.Nullable)
  def follow_href = column[String]("follow_href",O.Nullable)

  def * = created ~ key ~ category ~ id ~ title ~ author.? ~ summary ~ s_targetTime ~ d_targetTime.? ~ follow_num.? ~ follow_href.? <> (News.apply _, News.unapply _)

  implicit val dateTypeMapper = MappedTypeMapper.base[org.joda.time.DateTime, java.sql.Timestamp](
  {
    ud => new java.sql.Timestamp(ud.toDate.getTime)
  }, {
    sd => new DateTime(sd.getTime)
  })
}

object News extends SLF4JLogging {

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def targetStrToDate(s_targetTime: String, now: Date):Option[DateTime] = {
    def sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm")
    try {
      Some(new DateTime(sdf.parse(s_targetTime)))
    } catch {
      case e: Exception =>

        import org.scala_tools.time.Imports._
        s_targetTime match {
          case r"([0-9]+)${i}小时前" =>
            Some(new DateTime(now).minusHours(i.toInt).withMinuteOfHour(0).withSecondOfMinute(0))
          case r"([0-9]+)${i}分钟前" =>
            Some(new DateTime(now).minusMinutes(i.toInt).withSecondOfMinute(0))
          case _ => None
        }
    }
  }
  
  def run() = {
    val target = Map(
      "一、阿里巴巴与阿里体育" -> List("阿里","阿里巴巴","阿里体育","张大钟","马云"),
      "二、互联网体育" -> List("新浪体育","乐视体育","腾讯体育","pptv 体育","网易体育","搜狐体育","懒熊体育","36氪体育","懂球帝","爱奇艺体育","互联网体育","体奥动力","微鲸"),
      "三、体育赛事/版权" -> List("赛事","版权","中超","英超","NBA","网球","NFL","电子竞技","欧冠","欧锦","亚冠","亚锦","亚运","奥运","健身","运动","体育 IP"),
      "四、体育产业/融资/经营" -> List("体育 融资","体育 投资","体育 创业","体育 政策","体育 场馆"),
      "五、特辑：无人机/大数据/机器人" -> List("体育 无人机","体育 机器人","体育 大数据")
    )
    val l = for{
      ck <- target
      key <- ck._2
    } yield {
      val category = ck._1
      fetch(category,key)
    }
    l.toList.flatten
  }

  def fetch(category: String, key: String):List[News] = {
    import java.net.URLEncoder
    import dispatch._
    import Defaults._

    val gb2312encoded = URLEncoder.encode(key, "GB2312")
    val pattern = "http://news.baidu.com/ns?word=%s&bs=%s&sr=0&cl=2&rn=20&tn=news&ct=1&clk=sortbyrel"
    val hq_baidu = url(pattern.format(gb2312encoded, gb2312encoded))
    log.debug("百度抓取中url:" + hq_baidu.url)
    val http_baidu = Http(hq_baidu OK as.String)
    val html_baidu = http_baidu()
    val created = new Date
    val contents_div = Jsoup.parse(html_baidu).select("#content_left > div:nth-child(3) > div[id]")
    import scala.collection.JavaConversions._
    for (c <- contents_div.iterator().toList) yield {
      val id = c.attr("id").toInt
      val title = c.select(".c-title").first.text
      val c_summary_div = c.select(".c-summary").first
      val c_author_text = c_summary_div.select(".c-author").first.text

      log.debug("c-author:" + c_author_text)
      val c_author_text_split = c_author_text.split("\u00a0\u00a0")
      val author = if(c_author_text_split.length==2)Some(c_author_text_split(0)) else None // 通常都在第一个位置，有时候没有
      val s_targetTime = if(c_author_text_split.length==2)c_author_text_split(1) else c_author_text_split(0) //通常在第二个位置，有时候在第一个位置

      val d_target = targetStrToDate(s_targetTime, created)
      val summary_raw = c_summary_div.text
      val follow_num_div = c_summary_div.select("span.c-info > a.c-more_link")
      val follow_num_a = if (follow_num_div != null && !follow_num_div.toString.equals("")) {
        val x = follow_num_div
        val href = x.attr("href")
        (Some(x.first.text.replace("条相同新闻", "").toInt), Some(href))
      } else {
        (None, None)
      }
      val summary_step1 = (if(author.isDefined)summary_raw.replace(author.get, "") else summary_raw)
        .replace(s_targetTime, "").replace("-  百度快照", "")
      val summary_step2 = follow_num_a._1 match {
        case Some(e) => summary_step1.replace(e + "条相同新闻", "")
        case None => summary_step1
      }
      val summary = summary_step2.trim

      News(new DateTime(created),
        key,category,id,
        title, author, summary,
        s_targetTime, d_target,
        follow_num_a._1, follow_num_a._2)
    }
  }
}
