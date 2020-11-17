package com.tesco.oms.events;

import com.yantra.yfs.japi.YFSEnvironment;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.BasicConfigurator;
import org.w3c.dom.Document;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class EventPublisher {

    public EventPublisher() {
        BasicConfigurator.configure();
    }

    public Document publishEvent(YFSEnvironment env, Document inputDoc) {

       Producer<String, String> producer = new KafkaProducer<>(props());
       producer.send(new ProducerRecord<String, String>("quickstart-events", "name", "ARTEM1"));
       producer.close();
       System.out.println("must be sent");
       return inputDoc;
    }


    private Properties props() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    public static void main(String[] args) {
        EventPublisher ep = new EventPublisher();
        ep.publishEvent(null,null);
    }
}
