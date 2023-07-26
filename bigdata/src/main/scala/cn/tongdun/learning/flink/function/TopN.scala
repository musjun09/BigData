package cn.tongdun.learning.flink.function

import cn.tongdun.learning.flink.entity.UrlViewCount
import com.google.common.collect.Lists
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector

import java.sql.Timestamp
import java.util.Comparator

class TopN extends KeyedProcessFunction[Long, UrlViewCount, String]{
  private var N:Int =_
  private var urlViewCountListState:ListState[UrlViewCount] =_

  def this(n:Int){
    this()
    this.N = n
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    urlViewCountListState = getRuntimeContext.getListState(new ListStateDescriptor[UrlViewCount]("url-view-count-list", classOf[UrlViewCount]))
  }
  override def processElement(i: UrlViewCount, context: KeyedProcessFunction[Long, UrlViewCount, String]#Context, collector: Collector[String]): Unit = {
    urlViewCountListState.add(i)
    context.timerService().registerEventTimeTimer(context.getCurrentKey + 1)
  }

  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[Long, UrlViewCount, String]#OnTimerContext, out: Collector[String]): Unit = {
    val list = Lists.newArrayList(urlViewCountListState.get())
    urlViewCountListState.clear()
    list.sort(new Comparator[UrlViewCount] {
      override def compare(o1: UrlViewCount, o2: UrlViewCount): Int = {
        o1.count.toInt - o2.count.toInt
      }
    })

    val result = new StringBuilder
    result.append("窗口结束时间:" + new Timestamp(timestamp - 1) + "\n")
    for(i <- 0 to Math.min(this.N - 1, list.size() - 1)){
      val urlViewCount = list.get(i)
      val info = "No." + (i + 1) + " " + "url:" + urlViewCount.url + " " + "浏览量:" + urlViewCount.count + "\n";
      result.append(info)
    }
    result.append("========================================\n")
    out.collect(result.toString())
  }

  override def close(): Unit = {
    super.close()
    urlViewCountListState.clear()
  }
}
