package com.currency.demo_app.util;

import com.currency.demo_app.exceptions.ExchangeRateException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static com.currency.demo_app.enums.ErrorCode.INVALID_RATE_FORMAT;

@SuppressWarnings("unchecked")
public class ParseUtil {

    public static Map<String, Object> safeCastToMap(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        throw new ClassCastException("Expected a Map but got: " + obj.getClass());
    }

    public static BigDecimal parseRate(Object rateObj) {
        try {
            if (rateObj instanceof Number number) {
                return BigDecimal.valueOf(number.doubleValue());
            } else if (rateObj instanceof String str) {
                return new BigDecimal(str);
            } else {
                throw new ExchangeRateException(INVALID_RATE_FORMAT);
            }
        } catch (Exception e) {
            throw new ExchangeRateException(INVALID_RATE_FORMAT, e.getMessage());
        }
    }
}
