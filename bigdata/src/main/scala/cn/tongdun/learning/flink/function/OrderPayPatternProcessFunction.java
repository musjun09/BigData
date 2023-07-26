package cn.tongdun.learning.flink.function;

import cn.tongdun.learning.flink.entity.OrderEvent;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.functions.TimedOutPartialMatchHandler;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.List;
import java.util.Map;

public class OrderPayPatternProcessFunction extends
        PatternProcessFunction<OrderEvent, String> implements
        TimedOutPartialMatchHandler<OrderEvent> {
    @Override
    public void processMatch(Map<String, List<OrderEvent>> map, Context context, Collector<String> collector) throws Exception {
        OrderEvent payEvent = map.get("pay").get(0); collector.collect("订单 " + payEvent.orderId + " 已支付!");
    }

    @Override
    public void processTimedOutMatch(Map<String, List<OrderEvent>> map, Context context) throws Exception {
        OrderEvent createEvent = map.get("create").get(0);
        context.output(new OutputTag<String>("timeout"){}, "订单 " + createEvent.orderId + " 超时未支付!用户为:" + createEvent.userId);
    }
}
