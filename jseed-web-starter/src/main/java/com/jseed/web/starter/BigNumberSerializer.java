package com.jseed.web.starter;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigInteger;

/**
 * @author cyrus
 */
public class BigNumberSerializer extends ValueSerializer<Number> {
    private static final BigInteger MAX = BigInteger.valueOf(9007199254740991L);

    public BigNumberSerializer(Class<? extends Number> type) {}

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializationContext context) {
        BigInteger integer =
                value instanceof BigInteger b ? b : BigInteger.valueOf(value.longValue());
        if (integer.abs().compareTo(MAX) <= 0) gen.writeNumber(value.longValue());
        else gen.writeString(value.toString());
    }
}
