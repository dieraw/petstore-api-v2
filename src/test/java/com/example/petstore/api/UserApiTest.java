package com.example.petstore.api;

import com.example.petstore.api.models.User;
import com.example.petstore.api.parameters.RandomUser;
import com.example.petstore.api.parameters.RandomUserResolver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(RandomUserResolver.class)

public class UserApiTest {

    private User randomUser;  // Поле для хранения случайного пользователя

    @BeforeAll
    public static void setup() {
        // Настраиваем базовый URI
        RestAssured.baseURI = PetApiConfig.BASE_URL;
    }

    @BeforeEach
    public void createUser(@RandomUser User user) {
        // Сохраняем случайного пользователя для использования во всех тестах
        randomUser = user;

        // Создаем пользователя перед каждым тестом
        given()
                .contentType(ContentType.JSON)
                .body(randomUser)
                .when()
                .post(PetApiConfig.USER_ENDPOINT)
                .then()
                .statusCode(200);
    }
    @Test
    public void testGetUserInfoType() {
        String username = randomUser.getUsername(); // Извлекаем username из randomUser
        System.out.println(randomUser);

        given()
                .when()
                .get(PetApiConfig.USER_ENDPOINT + "/" + username) // Используем username из randomUser
                .then()
                .statusCode(200)
                .body("id", instanceOf(Number.class)) // Проверка, что id - это числовой тип
                .body("username", instanceOf(String.class)) // Проверка типа username
                .body("firstName", instanceOf(String.class)) // Проверка типа firstName
                .body("lastName", instanceOf(String.class)) // Проверка типа lastName
                .body("email", instanceOf(String.class)) // Проверка типа email
                .body("password", instanceOf(String.class)) // Проверка типа password
                .body("phone", instanceOf(String.class)) // Проверка типа phone
                .body("userStatus", instanceOf(Number.class)); // Проверка типа userStatus
    }


    @Test
    public void testGetUserById() {
        // Тест получения пользователя по его ID
        given()
                .when()
                .get(PetApiConfig.USER_ENDPOINT + "/" + randomUser.getUsername())
                .then()
                .statusCode(200)
                .body("id", equalTo((int) randomUser.getId()))
                .body("username", equalTo(randomUser.getUsername()))
                .body("email", equalTo(randomUser.getEmail()));
    }

    @Test
    public void testUpdateUser() {
        // Тест обновления данных пользователя
        randomUser.setFirstName("UpdatedFirstName");
        randomUser.setLastName("UpdatedLastName");

        given()
                .contentType(ContentType.JSON)
                .body(randomUser)
                .when()
                .put(PetApiConfig.USER_ENDPOINT + "/" + randomUser.getUsername())
                .then()
                .statusCode(200);

        // Проверяем обновленные данные
        given()
                .when()
                .get(PetApiConfig.USER_ENDPOINT + "/" + randomUser.getUsername())
                .then()
                .statusCode(200)
                .body("firstName", equalTo("UpdatedFirstName"))
                .body("lastName", equalTo("UpdatedLastName"));
    }

    @Test
    public void testDeleteUser() {
        // Тест удаления пользователя
        given()
                .when()
                .delete(PetApiConfig.USER_ENDPOINT + "/" + randomUser.getUsername())
                .then()
                .statusCode(200);

        // Проверяем, что пользователь был удален
        given()
                .when()
                .get(PetApiConfig.USER_ENDPOINT + "/" + randomUser.getUsername())
                .then()
                .statusCode(404);  // Ожидаем, что пользователя больше не существует
    }

    @Test
    public void testUserLogin() {
        String username = randomUser.getUsername();
        String password = randomUser.getPassword();

        given()
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get(PetApiConfig.USER_LOGIN_ENDPOINT)
                .then()
                .statusCode(200)
                .body("message", containsString("logged in user session"));
    }

    @Test
    public void testUserLogout() {
        String username = randomUser.getUsername();
        String password = randomUser.getPassword();

        // Выполняем вход в систему перед логаутом
        given()
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get(PetApiConfig.USER_LOGIN_ENDPOINT) // Логиним пользователя
                .then()
                .statusCode(200) // Убеждаемся, что логин успешен
                .body("message", containsString("logged in user session"));

        // Выполняем логаут после успешного входа
        given()
                .when()
                .get(PetApiConfig.USER_LOGOUT_ENDPOINT) // Выполняем логаут
                .then()
                .statusCode(200)
                .body("message", containsString("ok"));
    }
    @Test
    public void testCreateUsersWithList(@RandomUser User user1, @RandomUser User user2) {
        // Создаем список пользователей
        List<User> users = Arrays.asList(user1, user2);

        // Тест создания списка пользователей
        given()
                .contentType(ContentType.JSON)
                .body(users)  // Передаем список пользователей в теле запроса
                .when()
                .post(PetApiConfig.USER_CREATE_WITH_LIST_ENDPOINT) // Используем конечную точку для создания списка
                .then()
                .statusCode(200) // Проверяем, что запрос выполнен успешно
                .body("message", notNullValue());  // Проверяем, что сообщение присутствует
    }
    @Test
    public void testCreateUsersWithArray() {
        // Генерация массива случайных пользователей
        RandomUserResolver userResolver = new RandomUserResolver();
        User[] userArray = new User[5]; // Создаём массив из 5 пользователей

        for (int i = 0; i < userArray.length; i++) {
            userArray[i] = (User) userResolver.resolveParameter(null, null); // Заполняем массив случайными пользователями
        }

        // Отправляем запрос на создание массива пользователей
        given()
                .contentType(ContentType.JSON)
                .body(userArray)
                .when()
                .post(PetApiConfig.USER_CREATE_WITH_ARRAY_ENDPOINT)
                .then()
                .statusCode(200); // Ожидаем успешный ответ
    }


}
