package com.mt.example.gcppubsub.service;

import org.springframework.stereotype.Component;

import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageConsumer {

	public void consume(BasicAcknowledgeablePubsubMessage message) {
		log.info("Message received from PubSub : {}", message.getPubsubMessage());
		message.ack();
	}
}
