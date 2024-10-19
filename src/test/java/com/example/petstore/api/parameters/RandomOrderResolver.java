package com.example.petstore.api.parameters;

import com.example.petstore.api.models.Order;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.time.OffsetDateTime;
import java.util.Random;

public class RandomOrderResolver implements ParameterResolver {

    private static final Random random = new Random();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(RandomOrder.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();

        // Проверяем, что параметр является объектом Order
        if (Order.class.equals(type)) {
            long id = random.nextInt(10) + 1; // Генерация ID от 1 до 10
            long petId = random.nextInt(10) + 1; // Генерация petId от 1 до 10
            int quantity = random.nextInt(10) + 1; // Генерация quantity от 1 до 10
            String shipDate = "2024-10-18T20:54:42.482Z"; // Пример даты
            String status = "placed"; // Пример статуса
            boolean complete = false; // Пример значения

            return new Order(id, (int) petId, quantity, shipDate, status, complete);
        }

        throw new ParameterResolutionException("Unsupported parameter type: " + type.getName());
    }

}
