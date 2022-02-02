package com.mt.example.gcppubsub.service;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.mt.example.gcppubsub.model.Product;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessagePublisher {

	private final MessageHandler defaultMessageSender;

	private final ObjectMapper objectMapper;

	public void sendMessageToPubSub() throws JsonProcessingException {
		Product product = Product.builder().id(101).name("Cricket Bat").description("Suitable for kids").build();
		String jsonString = objectMapper.writeValueAsString(product);
		Message<ByteString> message = MessageBuilder.withPayload(ByteString.copyFromUtf8(jsonString)).build();
		defaultMessageSender.handleMessage(message);
	}
}
