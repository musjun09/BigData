package cn.xlearning.flink.example

import cn.xlearning.flink.entity.Event
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor}
import org.apache.flink.runtime.state.{FunctionInitializationContext, FunctionSnapshotContext}
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.collection.mutable.ListBuffer

class BufferingSink extends SinkFunction[Event] with CheckpointedFunction{
  var threshold:Int = 0
  @transient
  private var checkpointedState: ListState[Event] = _
  private val bufferedElements = ListBuffer[Event]()

  override def invoke(value: Event, context: SinkFunction.Context): Unit = {
    bufferedElements += value
    if(bufferedElements.size >= threshold){
      println("")
    }
    bufferedElements.clear()
  }
  override def snapshotState(functionSnapshotContext: FunctionSnapshotContext): Unit = {
    checkpointedState.clear()
    for(element <- bufferedElements){
      checkpointedState.add(element)
    }
  }

  override def initializeState(functionInitializationContext: FunctionInitializationContext): Unit = {
    val descriptor = new ListStateDescriptor[Event]("buffer", classOf[Event])
    checkpointedState = functionInitializationContext.getOperatorStateStore.getListState(descriptor)
    if(functionInitializationContext.isRestored){
      for(element <- checkpointedState.get().asScala){
        bufferedElements += element
      }
    }
  }
}
