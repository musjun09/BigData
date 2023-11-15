package cn.xlearning.flink

import cn.xlearning.flink.source.ClickSource
import com.ververica.cdc.connectors.postgres.PostgreSQLSource
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.functions.{AggregateFunction, MapFunction, Partitioner, ReduceFunction}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector
import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema
import org.apache.flink.connector.kafka.sink.KafkaSink

import java.time.Duration
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaProducer, FlinkKafkaProducer011}

object Application {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.enableCheckpointing(2 * 1000 * 60)
    val config = env.getCheckpointConfig
    config.enableUnalignedCheckpoints(true)
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(1000 * 60 * 2)
    env.getCheckpointConfig.setCheckpointTimeout(1000 * 60 * 5)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)

    config.setExternalizedCheckpointCleanup(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    val source = env.addSource(new ClickSource).map(x=> x.url)

//    val sink = KafkaSink.builder[String].setBootstrapServers("localhost:9092")
//      .setRecordSerializer(KafkaRecordSerializationSchema.builder.setTopic("test")
//        .setValueSerializationSchema(new SimpleStringSchema())
//        .build)
//      .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE).build
//
//    source.sinkTo(sink)

    source.addSink(new FlinkKafkaProducer[String]("localhost:9092", "test", new SimpleStringSchema()))
    env.execute()
  }
}
