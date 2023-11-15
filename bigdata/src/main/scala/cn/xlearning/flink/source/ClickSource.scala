package cn.xlearning.flink.source

import cn.xlearning.flink.entity.Event
import org.apache.flink.streaming.api.functions.source.SourceFunction

import scala.util.Random

class ClickSource extends SourceFunction[Event]{
  var running = true
  override def run(sourceContext: SourceFunction.SourceContext[Event]): Unit = {
    import java.util.Calendar
    val random = new Random() // 在指定的数据集中随机选取数据
    77
    val users = Array("Mary", "Alice", "Bob", "Cary")
    val urls = Array("./home", "./cart", "./fav", "./prod?id=1", "./prod?id=2")
    while (running) {
      sourceContext.collect(new Event(users(random.nextInt(users.length)), urls(random.nextInt(urls.length)), Calendar.getInstance.getTimeInMillis))
      // 隔 1 秒生成一个点击事件，方便观测
      Thread.sleep(1000)
    }
  }

  override def cancel(): Unit = {
    running = false
  }
}
