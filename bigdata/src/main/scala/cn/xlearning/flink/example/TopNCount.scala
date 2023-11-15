package cn.xlearning.flink.example


import cn.xlearning.flink.entity.{Event, UrlViewCount}
import cn.xlearning.flink.function.TopN
import cn.xlearning.flink.source.ClickSource
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object TopNCount {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val source = env.addSource(new ClickSource)
    val urlCountStream = source.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps()
    .withTimestampAssigner(new SerializableTimestampAssigner[Event] {
      override def extractTimestamp(t: Event, l: Long): Long = {
        t.timestamp
      }
    })).keyBy(_.url).window(SlidingEventTimeWindows.of(Time.seconds(60), Time.seconds(5)))
      .aggregate(new AggregateFunction[Event,Long,Long] {
        override def createAccumulator(): Long = 0L

        override def add(in: Event, acc: Long): Long = {
          acc + 1
        }

        override def getResult(acc: Long): Long = {
          acc
        }

        override def merge(acc: Long, acc1: Long): Long = 0L
      }, new ProcessWindowFunction[Long, UrlViewCount, String, TimeWindow] {
        override def process(key: String, context: Context, elements: Iterable[Long], out: Collector[UrlViewCount]): Unit = {
          val start = context.window.getStart
          val end = context.window.getEnd
          out.collect(new UrlViewCount(key, elements.iterator.next(), start, end))
        }
      })

    urlCountStream.keyBy(r => r.windowEnd).process(new TopN(2)).print("result")

    env.execute()
  }
}
