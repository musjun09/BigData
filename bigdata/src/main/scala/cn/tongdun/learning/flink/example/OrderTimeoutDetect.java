package cn.tongdun.learning.flink.example;

import cn.tongdun.learning.flink.entity.OrderEvent;
import cn.tongdun.learning.flink.function.OrderPayPatternProcessFunction;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

public class OrderTimeoutDetect {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        KeyedStream<OrderEvent, String> keyedStream = env.fromElements(
                new OrderEvent("user_1", "order_1", "create", 1000L),
                new OrderEvent("user_2", "order_1", "modify", 2000L),
                new OrderEvent("user_1", "order_1", "pay", 20 * 60 * 1000L),
                new OrderEvent("user_1", "order_1", "create", 10 * 1000L),
                new OrderEvent("user_2", "order_1", "pay", 20 * 1000L),
                new OrderEvent("user_2", "order_3", "pay", 20 * 60 * 1000L))
                .assignTimestampsAndWatermarks(WatermarkStrategy.<OrderEvent>forMonotonousTimestamps()
                        .withTimestampAssigner(new SerializableTimestampAssigner<OrderEvent>() {
                            @Override
                            public long extractTimestamp(OrderEvent orderEvent, long l) {
                                return orderEvent.timestamp;
                            }
                        })).keyBy(order -> order.orderId);

        Pattern<OrderEvent, ?> pattern = Pattern.<OrderEvent>begin("create").where(new SimpleCondition<OrderEvent>() {
            @Override
            public boolean filter(OrderEvent orderEvent) throws Exception {
                return orderEvent.eventType.equals("create");
            }
        }).followedBy("pay").where(new SimpleCondition<OrderEvent>() {
            @Override
            public boolean filter(OrderEvent orderEvent) throws Exception {
                return orderEvent.eventType.equals("pay");
            }
        }).within(Time.minutes(15));
        OutputTag<String> timeoutTag = new OutputTag<String>("timeout"){};
        PatternStream<OrderEvent> patternStream = CEP.pattern(keyedStream, pattern);
        SingleOutputStreamOperator<String> payedOrderStream =
                patternStream.process(new OrderPayPatternProcessFunction());
        // 将正常匹配和超时部分匹配的处理结果流打印输出
        payedOrderStream.print("payed");
        payedOrderStream.getSideOutput(timeoutTag).print("timeout");

        env.execute();
    }
}
