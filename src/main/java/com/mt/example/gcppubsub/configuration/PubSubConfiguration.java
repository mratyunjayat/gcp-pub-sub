package com.mt.example.gcppubsub.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.mt.example.gcppubsub.service.MessageConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class PubSubConfiguration {

	//Subscription
	
	@Value("${pubsub.subscription-id}")
	private String subscriptionId;
	
	@Value("${pubsub.topic-id}")
	private String topicId;

	@Bean
	public DirectChannel pubSubInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(
			@Qualifier("pubSubInputChannel") MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionId);
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);
		adapter.setPayloadType(String.class);
		return adapter;
	}

	@Bean("defaultMessageReceiver")
	@ServiceActivator(inputChannel = "pubSubInputChannel")
	public MessageHandler messageReceiver(MessageConsumer consumer) {
		return message -> {
			BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
					.get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
			consumer.consume(originalMessage);
		};
	}

	//Publisher

	@Bean
	public DirectChannel pubSubOutputChannel() {
		return new DirectChannel();
	}
	
	@Bean("defaultMessageSender")
	@ServiceActivator(inputChannel = "pubSubOutputChannel")
	public MessageHandler messageSender(PubSubTemplate pubSubTemplate) {
		var adapter = new PubSubMessageHandler(pubSubTemplate, topicId);
		adapter.setSync(true);
		return adapter;
	}
	
}
