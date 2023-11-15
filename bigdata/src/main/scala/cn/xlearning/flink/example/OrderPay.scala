package cn.xlearning.flink.example

import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.functions.co.{CoMapFunction, CoProcessFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

object OrderPay {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val appStream = env.fromElements(
      ("order-1", "app", 1000L),
      ("order-2", "app", 2000L)
    ).assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps[(String, String, Long)]()
    .withTimestampAssigner(new SerializableTimestampAssigner[(String, String, Long)] {
      override def extractTimestamp(t: (String, String, Long), l: Long): Long = {
        t._3
      }
    }))

    val thirdpartStream = env.fromElements(
      ("order-1", "third-party", "success", 3000L),
      ("order-3", "third-party", "success", 4000L)
    ).assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps[(String, String,String, Long)]()
    .withTimestampAssigner(new SerializableTimestampAssigner[(String, String, String, Long)] {
      override def extractTimestamp(t: (String, String, String, Long), l: Long): Long = t._4
    }))

    appStream.connect(thirdpartStream).keyBy(data => data._1, data => data._1)
      .process(new CoProcessFunction[(String, String, Long), (String, String, String, Long), String] {
        var appEventState:ValueState[(String, String, Long)]=_
        var thirdPartyEventState:ValueState[(String, String, String, Long)]=_

        override def open(parameters: Configuration): Unit = {
          super.open(parameters)
          appEventState = getRuntimeContext.getState(new ValueStateDescriptor[(String, String, Long)]("app", classOf[(String, String, Long)]))
          thirdPartyEventState = getRuntimeContext.getState(new ValueStateDescriptor[(String, String, String, Long)]("thirdpatry", classOf[(String, String, String, Long)]))
        }

        override def processElement1(in1: (String, String, Long), context: CoProcessFunction[(String, String, Long), (String, String, String, Long), String]#Context, collector: Collector[String]): Unit = {
          if(thirdPartyEventState.value() != null){
            collector.collect("对账成功")
            thirdPartyEventState.clear()
          }else{
            appEventState.update(in1)
            context.timerService().registerEventTimeTimer(in1._3 + 5000L)
          }
        }

        override def processElement2(in2: (String, String, String, Long), context: CoProcessFunction[(String, String, Long), (String, String, String, Long), String]#Context, collector: Collector[String]): Unit = {
          if(appEventState.value() != null){
            collector.collect("对账成功")
            appEventState.clear()
          }else{
            thirdPartyEventState.update(in2)
            context.timerService().registerEventTimeTimer(in2._4 + 5000L)
          }
        }

        override def onTimer(timestamp: Long, ctx: CoProcessFunction[(String, String, Long), (String, String, String, Long), String]#OnTimerContext, out: Collector[String]): Unit = {
          if(appEventState.value() != null){
            out.collect("对账失败")
          }

          if(thirdPartyEventState.value() != null){
            out.collect("对账失败")
          }
          appEventState.clear()
          thirdPartyEventState.clear()
        }

      }).print()


    env.execute()
  }
}
