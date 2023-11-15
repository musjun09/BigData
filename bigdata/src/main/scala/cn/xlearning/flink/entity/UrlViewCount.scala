package cn.xlearning.flink.entity

import java.sql.Timestamp

class UrlViewCount {
  var url:String =_
  var count:Long =_
  var windowStart:Long=_
  var windowEnd:Long=_

  def this(url:String, count:Long, windowStart:Long, windowEnd:Long) {
    this()
    this.url = url;
    this.count = count;
    this.windowStart = windowStart;
    this.windowEnd = windowEnd;
  }
  @Override
  override def toString():String = {
     "UrlViewCount{" +
      "url='" + url + '\'' +
      ", count=" + count +
      ", windowStart=" + new Timestamp(windowStart) +
      ", windowEnd=" + new Timestamp(windowEnd) +
      '}';
  }
}
