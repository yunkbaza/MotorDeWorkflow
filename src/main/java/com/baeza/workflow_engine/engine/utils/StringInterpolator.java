package com.baeza.workflow_engine.engine.utils;

import java.util.Map;

public class StringInterpolator {

    // Procura qualquer {{chave}} no texto e troca pelo valor que está na Memória (Contexto)
    public static String interpolate(String text, Map<String, Object> context) {
        if (text == null || context == null) return text;

        String result = text;
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            if (entry.getValue() != null) {
                String keyToFind = "{{" + entry.getKey() + "}}";
                result = result.replace(keyToFind, entry.getValue().toString());
            }
        }
        return result;
    }
}