package cn.tongdun.learning.flink.function

import cn.tongdun.learning.flink.entity.Event
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.scala.typeutils.Types
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.triggers.{Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

class LateTrigger extends Trigger[Event, TimeWindow]{
  private var lateTime: Long = _

  def this(lateTime: Int) {
    this()
    this.lateTime = lateTime*60*1000
  }

  override def onElement(t: Event, l: Long, window: TimeWindow, ctx: Trigger.TriggerContext): TriggerResult = {
    var firstSeen: ValueState[Boolean] = ctx.getPartitionedState(new ValueStateDescriptor[Boolean]("firstSeen", Types.of[Boolean]))
    var lateSeen: ValueState[Boolean] = ctx.getPartitionedState(new ValueStateDescriptor[Boolean]("lateSeen", Types.of[Boolean]))
    if(!firstSeen.value()){
      ctx.registerEventTimeTimer(window.getEnd)
      firstSeen.update(true)
    }

    if(!lateSeen.value() && ctx.getCurrentWatermark >= window.getEnd){
      ctx.registerEventTimeTimer(window.getEnd + lateTime)
      lateSeen.update(true)
    }
    TriggerResult.CONTINUE
  }

  override def onProcessingTime(l: Long, w: TimeWindow, triggerContext: Trigger.TriggerContext): TriggerResult = {
    TriggerResult.CONTINUE;
  }

  override def onEventTime(l: Long, w: TimeWindow, triggerContext: Trigger.TriggerContext): TriggerResult = {
    if(l == w.getEnd){
      TriggerResult.FIRE
    }else{
      TriggerResult.FIRE_AND_PURGE
    }

  }

  override def clear(w: TimeWindow, triggerContext: Trigger.TriggerContext): Unit = {
    var firstSeen: ValueState[Boolean] = triggerContext.getPartitionedState(new ValueStateDescriptor[Boolean]("firstSeen", Types.of[Boolean]))
    var lateSeen: ValueState[Boolean] = triggerContext.getPartitionedState(new ValueStateDescriptor[Boolean]("lateSeen", Types.of[Boolean]))
    firstSeen.clear()
    lateSeen.clear()
    triggerContext.deleteEventTimeTimer(w.getEnd)
    triggerContext.deleteEventTimeTimer(w.getEnd + lateTime)
  }

  override def toString: String = super.toString

  override def canMerge: Boolean = true
}
