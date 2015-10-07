package com.nbb.spider.news.baidu

import org.jsoup._
import java.text.SimpleDateFormat
import java.util.Date

case class News(
   created:Date, title: String, author: String, s_targetTime: String, d_targetTime:Option[Date], summary: String,
   follow_num: Option[Int], follow_href:Option[String]) {
}

object News {

  def targetStrToDate(s_targetTime:String, now:Date) = {
    def sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm")
    try {
      Some(sdf.parse(s_targetTime))
    } catch {
      case e: Exception => {
        implicit class Regex(sc: StringContext) {
          def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
        }
        import org.scala_tools.time.Imports._
        s_targetTime match {
          case r"([0-9]+)${i}小时前" => {
            Some(new DateTime(now).minusHours(i.toInt).withMinuteOfHour(0).withSecondOfMinute(0).toDate);
          }
          case r"([0-9]+)${i}分钟前" => {
            Some(new DateTime(now).minusMinutes(i.toInt).withSecondOfMinute(0).toDate);
          }
          case _ => None
        }
      }
    }
  }

  def run() = {
    import java.net.URLEncoder
    import dispatch._
    import Defaults._

    val gb2312encoded = URLEncoder.encode("阿里", "GB2312")
    val pattern = "http://news.baidu.com/ns?word=%s&bs=%s&sr=0&cl=2&rn=20&tn=news&ct=1&clk=sortbyrel"
    val hq_baidu = url(pattern.format(gb2312encoded, gb2312encoded))
    val http_baidu = Http(hq_baidu OK as.String)
    val html_baidu = http_baidu()
    val created = new Date;
    val contents_div = Jsoup.parse(html_baidu).select("#content_left > div:nth-child(3)").first.children
    import scala.collection.JavaConversions._
    for (c <- contents_div.iterator().toList) yield {
      val title = c.select(".c-title").first.text
      val c_summary_div = c.select(".c-summary").first
      val c_author_text = c_summary_div.select(".c-author").first.text
      val c_author_text_split = c_author_text.split("\u00a0\u00a0")
      val author = c_author_text_split(0)
      val time = c_author_text_split(1);
      val d_target = targetStrToDate(time,created)
      val summary = c_summary_div.text
      val follow_num_div = c_summary_div.select("span.c-info > a.c-more_link")
      val follow_num_a = if (follow_num_div != null && !follow_num_div.toString.equals("")) {
        val x = follow_num_div
        val href = x.attr("href")
        (Some(x.first.text.replace("条相同新闻", "").toInt),Some(href))
      } else {
        (None,None)
      }
      val news = News(created, title, author, time, d_target, summary, follow_num_a._1,follow_num_a._2)
      println(news)
      news
    }
  }
}
