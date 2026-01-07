package com.example.user.function.compile;

import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.springframework.stereotype.Component;

@Component
public class GroovyCompiler {

    private final GroovyClassLoader classLoader = new GroovyClassLoader();

    public Script compile(String script) {
        try {
            Class<?> clazz = classLoader.parseClass(script);
            return (Script) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Groovy compile failed", e);
        }
    }
}