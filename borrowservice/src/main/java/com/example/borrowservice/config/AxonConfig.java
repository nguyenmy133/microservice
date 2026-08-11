package com.example.borrowservice.config;

import com.thoughtworks.xstream.XStream;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    /**
     * Khởi tạo XStream bean dùng chung cho toàn bộ Axon Framework Serializers
     * (DefaultSerializer, MessageSerializer, EventSerializer).
     *
     * AxonAutoConfiguration tự động inject bean XStream này vào tất cả Serializer.
     */
    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();
        xStream.allowTypesByWildcard(new String[]{
                "com.example.**",
                "org.axonframework.**",
                "java.util.**",
                "java.lang.**",
                "java.time.**"
        });
        return xStream;
    }

    /**
     * Cấu hình ListenerInvocationErrorHandler cho saga processor group.
     */
    @Autowired
    public void configureEventProcessing(EventProcessingConfigurer configurer) {
        configurer.registerListenerInvocationErrorHandler(
                "borrowing-saga",
                configuration -> PropagatingErrorHandler.instance()
        );
    }
}
