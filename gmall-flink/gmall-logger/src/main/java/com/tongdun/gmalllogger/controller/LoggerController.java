package com.tongdun.gmalllogger.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.util.Properties;

@RestController
@Slf4j
public class LoggerController {
  //  @Autowired
   // private KafkaTemplate<String, String> kafkaTemplate;
    private Properties properties = new Properties();
    private KafkaProducer<String, String> producer;
    {
        properties.put("bootstrap.servers", "slave1:9092,slave2:9092,slave3:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer  = new KafkaProducer<>(properties);
    }

    @RequestMapping("test1")
    public String test1() {
        System.out.println("success");
        return "success";
    }
    @RequestMapping("test2")
    public String test2(@RequestParam("name") String name, @RequestParam("age") int age) {
        System.out.println(name + ":" + age);
        return "success";
    }

    @RequestMapping("applog")
    public String getLogger(@RequestParam("param") String jsonStr) {
        //落盘
        log.info(jsonStr);
        ProducerRecord<String, String> record = new ProducerRecord<>("ods_base_log", null, jsonStr);
        producer.send(record);
        return "success";
    }
    @PreDestroy
    public void destory(){
        producer.close();
        System.out.println("关闭producer");
    }
}
