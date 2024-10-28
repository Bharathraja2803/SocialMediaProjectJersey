package com.bharath.utils;

import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.core.util.JacksonFeature;

public class MyApplication extends ResourceConfig {
    public MyApplication() {
        packages("com.bharath"); // Replace with your package
        register(JacksonFeature.class);
        // Optionally, register the Java Time module if you're not using custom serializers/deserializers
        register(com.fasterxml.jackson.datatype.jsr310.JavaTimeModule.class);
    }
}