package com.coffeeShop.service.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.money.Money;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;

@JsonComponent
public class MoneySerializer extends StdSerializer<Money> {
    NumberFormat nf = NumberFormat.getNumberInstance();

    protected MoneySerializer() {
        super(Money.class);
    }

    @Override
    public void serialize(Money money, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(money.getAmount().setScale(2, RoundingMode.UP));
    }
}
