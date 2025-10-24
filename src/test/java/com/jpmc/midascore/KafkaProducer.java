package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final String topic;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public KafkaProducer(
            @Value("${general.kafka-topic}") String topic,
            KafkaTemplate<String, Transaction> kafkaTemplate
    ) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String transactionLine) {
        String[] parts = transactionLine.split(", ");
        long senderId = Long.parseLong(parts[0]);
        long recipientId = Long.parseLong(parts[1]);
        double amount = Double.parseDouble(parts[2]);

        Transaction t = new Transaction(senderId, recipientId, amount);
        kafkaTemplate.send(topic, t);
    }
}
