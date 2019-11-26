package com.zc.study;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class KafkaConsumer extends Thread {
    private static String zookeeperHost = null;
    private static String kafkaBootstrap = null;
    private static String kafkaGroup = null;
    private static String kafkaTopic = null;
    private static String hdfsUri;
    private static String hdfsDir = null;
    private static String localDir = null;
    private static String hadoopUser = null;
    private static Boolean isDebug = false;
    public volatile boolean exit = false;
    private long timeStamp = 0L;
    private String stamp2Date = null;


    public static void main(String[] args) throws InterruptedException {

        try {
            init(args);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("开始启动服务...");

        KafkaConsumer selfObj = new KafkaConsumer();
        selfObj.start();
        System.out.println("服务启动完毕，监听执行中");
    }


    public void run() {
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeperHost);
        props.put("group.id", kafkaGroup);
        props.put("bootstrap.servers", kafkaBootstrap);
        //props.put("zookeeper.session.timeout.ms", "10000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.offset.reset", "latest");
        // 是否自动确认offset
        props.put("auto.commit.enable", "true");
        // 自动确认offset的时间间隔
        props.put("auto.commit.interval.ms", "1000");
        // key的序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // value的序列化类
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");


        org.apache.kafka.clients.consumer.KafkaConsumer kafkaConsumer = new org.apache.kafka.clients.consumer.KafkaConsumer(props);

        kafkaConsumer.subscribe(Arrays.asList("bigdata_test_temp"));


        while (!exit) {
            // 读取数据，读取超时时间为100ms
            ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
    }

    private static void init(String[] args) throws IOException {
        zookeeperHost = "10.38.64.184:2181";
        kafkaBootstrap = "10.38.64.180:9092,10.38.64.181:9092,10.38.64.69:9092";
        kafkaGroup = "bh_app_1";
      
    }
}
