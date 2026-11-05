package com.jseed.web.starter.serializer;

import com.jseed.core.starter.utils.SpringUtils;
import com.jseed.web.starter.property.WebConfigProperty;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Immutable per-property serializer; safe when Jackson caches serializers.
 *
 * @author cyrus
 */
public class SensitiveJsonSerializer extends ValueSerializer<String> {
    private final SensitiveStrategy strategy;

    public SensitiveJsonSerializer() {
        this(null);
    }

    private SensitiveJsonSerializer(SensitiveStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializationContext context) {
        gen.writeString(strategy == null ? value : strategy.desensitizer().apply(value));
    }

    @Override
    public ValueSerializer<?> createContextual(
            SerializationContext context, BeanProperty property) {
        Sensitive annotation = property == null ? null : property.getAnnotation(Sensitive.class);
        var factory = SpringUtils.getBeanFactory();
        WebConfigProperty config =
                factory == null
                        ? null
                        : factory.getBeanProvider(WebConfigProperty.class).getIfAvailable();
        return annotation == null || (config != null && !config.isDesensitization())
                ? this
                : new SensitiveJsonSerializer(annotation.strategy());
    }
}
