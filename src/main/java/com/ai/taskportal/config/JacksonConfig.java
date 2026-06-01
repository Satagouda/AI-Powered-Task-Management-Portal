package com.ai.taskportal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // ✅ Handles Hibernate proxies — no more ByteBuddyInterceptor errors
        Hibernate6Module hibernate6Module = new Hibernate6Module();
        // Do NOT force lazy loading during serialization — return null instead
        hibernate6Module.disable(
                Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        // Serialize uninitialized proxies as null rather than throwing
        hibernate6Module.enable(
                Hibernate6Module.Feature.WRITE_MISSING_ENTITIES_AS_NULL);
        mapper.registerModule(hibernate6Module);

        // ✅ Handles Java 8+ date/time types (LocalDate, LocalDateTime etc.)
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
