package cn.xlearning.flink.function

import cn.xlearning.flink.entity.Event
import org.apache.flink.api.common.eventtime._

class CustomWatermarkStrategy extends WatermarkStrategy[Event]{

  override def createTimestampAssigner(context: TimestampAssignerSupplier.Context):TimestampAssigner[Event] = {
    new SerializableTimestampAssigner[Event] {
      override def extractTimestamp(t: Event, l: Long): Long = {
        t.timestamp
      }
    }
  }

  override def createWatermarkGenerator(context: WatermarkGeneratorSupplier.Context): WatermarkGenerator[Event] = {
    new CustomPeriodicGenerator()
  }
}

class CustomPeriodicGenerator extends WatermarkGenerator[Event] {
  private val delayTime = 5000L // 延迟时间

  private var maxTs = Long.MinValue + delayTime + 1L
  override def onEvent(t: Event, l: Long, watermarkOutput: WatermarkOutput): Unit = {
    maxTs = Math.max(t.timestamp, maxTs)
  }

  override def onPeriodicEmit(watermarkOutput: WatermarkOutput): Unit = {
    watermarkOutput.emitWatermark(new Watermark(maxTs - delayTime - 1L))
  }
}
