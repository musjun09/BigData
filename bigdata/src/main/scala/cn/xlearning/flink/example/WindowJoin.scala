package cn.xlearning.flink.example

import cn.xlearning.flink.entity.Event
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.functions.JoinFunction
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.util.Collector
import org.apache.kafka.common.serialization.Serdes.String
object WindowJoin {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val stream1 = env
      .fromElements(
        ("Mary", "order-1", 5000L),
        ("Alice", "order-2", 5000L),
        ("Bob", "order-3", 20000L),
        ("Alice", "order-4", 20000L),
        ("Cary", "order-5", 51000L)
      )
      .assignTimestampsAndWatermarks(
        WatermarkStrategy
          .forMonotonousTimestamps[(String,String, Long)]()
          .withTimestampAssigner(new SerializableTimestampAssigner[(String,String, Long)] {
            override def extractTimestamp(t: (String,String, Long), l: Long): Long = {
              t._3
            }
          }))

    val stream2 = env
      .fromElements(
        new Event("Bob", "./cart", 2000L),
        new Event("Alice", "./prod?id=100", 3000L),
        new Event("Alice", "./prod?id=200", 3500L),
        new Event("Bob", "./prod?id=2", 2500L),
        new Event("Alice", "./prod?id=300", 36000L),
        new Event("Bob", "./home", 30000L),
        new Event("Bob", "./prod?id=1", 23000L),
        new Event("Bob", "./prod?id=3", 33000L)
      ).assignTimestampsAndWatermarks(
        WatermarkStrategy
          .forMonotonousTimestamps[Event]()
          .withTimestampAssigner(new SerializableTimestampAssigner[Event] {
            override def extractTimestamp(t: Event, l: Long): Long = {
              t.timestamp
            }
          }))
      stream1.keyBy(_._1).intervalJoin(stream2.keyBy(_.user)).between(Time.seconds(-5), Time.seconds(10))
        .process(new ProcessJoinFunction[(String, String, Long), Event, String] {
          override def processElement(in1: (String, String, Long), in2: Event, context: ProcessJoinFunction[(String, String, Long), Event, String]#Context, collector: Collector[String]): Unit = {
            collector.collect(in1 + "=>" + in2)
          }
        }).print()

    env.execute()
  }

}
