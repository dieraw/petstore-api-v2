package com.example.petstore.api.parameters;

import com.example.petstore.api.models.User;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.Random;

public class RandomUserResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(RandomUser.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // Генерация случайных данных для пользователя
        Random random = new Random();
        long id = random.nextLong(1, 1000); // случайный id пользователя
        String username = "user" + id; // случайное имя пользователя
        String firstName = "FirstName" + id; // случайное имя
        String lastName = "LastName" + id; // случайная фамилия
        String email = "user" + id + "@example.com"; // случайная почта
        String password = "password" + id; // случайный пароль
        String phone = "555-000-" + random.nextInt(9999); // случайный номер телефона
        int userStatus = random.nextInt(3); // случайный статус (например, 0, 1, 2)

        return new User(id, username, firstName, lastName, email, password, phone, userStatus);
    }
}
