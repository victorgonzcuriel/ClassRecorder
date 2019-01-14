package com.victorgonzcuriel.classrecorder.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer{

	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/subscription");
        config.setApplicationDestinationPrefixes("/crws");
    }
 
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
         registry.addEndpoint("/").setAllowedOrigins("*").withSockJS().setHttpMessageCacheSize(102400* 1024).setStreamBytesLimit(102400* 1024);
    }
    
    @Override 
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) { 

    } 
	
}
