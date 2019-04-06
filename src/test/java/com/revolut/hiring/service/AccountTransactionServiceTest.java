package com.revolut.hiring.service;

import com.revolut.hiring.bean.Currency;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTransactionServiceTest {

    @Test
    public void testGetSinkAmount() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        final Double inputAmount = new Double(100);
        final Currency sourceCurrency = Currency.USD;
        final Currency sinkCurrency = Currency.EUR;

        final Double expectedValue = Double.valueOf(89);

        final AccountTransactionService testClass = new AccountTransactionService();

        Method method = testClass.getClass().getDeclaredMethod("getSinkAmount", Double.class, Currency.class, Currency.class);
        method.setAccessible(true);

        final Double actualValue = (Double) method.invoke(testClass, inputAmount, sourceCurrency, sinkCurrency);

        assertEquals(expectedValue, actualValue);
    }

}
