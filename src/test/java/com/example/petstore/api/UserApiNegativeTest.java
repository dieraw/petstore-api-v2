package com.example.petstore.api;

import com.example.petstore.api.models.User;
import com.example.petstore.api.parameters.RandomUser;
import com.example.petstore.api.parameters.RandomUserResolver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(RandomUserResolver.class)
public class UserApiNegativeTest {

    @BeforeAll
    public static void setup() {
        // Настраиваем базовый URI
        RestAssured.baseURI = PetApiConfig.BASE_URL;

    }

    private void deleteUserIfExists(String username) {
        // Удаляем пользователя, если он существует
        given()
                .when()
                .delete(PetApiConfig.USER_ENDPOINT + "/" + username)
                .then()
                .statusCode(anyOf(is(200), is(404))); // Ожидаем 200 (пользователь удален) или 404 (пользователь не найден)
    }

    @Test
    public void testGetNonExistentUser() {
        String nonExistentUsername = "nonExistentUser"; // Имя пользователя, которого не существует
        deleteUserIfExists(nonExistentUsername); // Удаляем пользователя перед тестом

        given()
                .when()
                .get(PetApiConfig.USER_ENDPOINT + "/" + nonExistentUsername)
                .then()
                .statusCode(404) // Ожидаем, что пользователь не найден
                .body("message", containsString("User not found")); // Проверка сообщения об ошибке
    }

    @Test
    public void testUpdateNonExistentUser() {
        // Создаем нового пользователя с необходимыми данными
        User userToUpdate = new User(
                0L, // ID, который не имеет значения для несуществующего пользователя
                "nonExistentUser", // Имя пользователя, которого не существует
                "TestFirstName",
                "TestLastName",
                "email@example.com", // Можем использовать любые данные для несуществующего пользователя
                "password",
                "1234567890",
                0 // userStatus
        );

        deleteUserIfExists(userToUpdate.getUsername()); // Удаляем пользователя перед тестом

        given()
                .contentType(ContentType.JSON)
                .body(userToUpdate)
                .when()
                .put(PetApiConfig.USER_ENDPOINT + "/" + userToUpdate.getUsername())
                .then()
                .statusCode(404) // Ожидаем, что пользователь не найден
                .body("message", containsString("User not found")); // Проверка сообщения об ошибке
    }

    @Test
    public void testDeleteNonExistentUser() {
        String nonExistentUsername = "nonExistentUser"; // Имя пользователя, которого не существует
        deleteUserIfExists(nonExistentUsername); // Удаляем пользователя перед тестом

        given()
                .when()
                .delete(PetApiConfig.USER_ENDPOINT + "/" + nonExistentUsername)
                .then()
                .statusCode(404); // Ожидаем, что пользователь не найден
    }


    @Test
    public void testUserLoginWithInvalidCredentials() {
        deleteUserIfExists("invalidUser");
        given()
                .queryParam("username", "invalidUser") // Некорректное имя пользователя
                .queryParam("password", "invalidPassword") // Некорректный пароль
                .when()
                .get(PetApiConfig.USER_LOGIN_ENDPOINT)
                .then()
                .statusCode(400) // Ожидаем, что авторизация не удалась
                .body("message", containsString("Invalid username/password supplied")); // Проверка сообщения об ошибке
    }

    @Test
    public void testCreateUserWithoutUsername(@RandomUser User userWithoutUsername) {
        userWithoutUsername.setUsername(null); // Убираем username
        userWithoutUsername.setFirstName("FirstName");
        userWithoutUsername.setLastName("LastName");
        userWithoutUsername.setEmail("email@example.com");

        given()
                .contentType(ContentType.JSON)
                .body(userWithoutUsername)
                .when()
                .post(PetApiConfig.USER_ENDPOINT) // Используем конечную точку для создания пользователя
                .then()
                .statusCode(400) // Ожидаем, что запрос не прошел
                .body("message", containsString("username is required")); // Проверка сообщения об ошибке
    }

    @Test
    public void testCreateUserWithInvalidEmail(@RandomUser User userWithInvalidEmail) {
        userWithInvalidEmail.setEmail("invalidEmailFormat"); // Некорректный формат email

        given()
                .contentType(ContentType.JSON)
                .body(userWithInvalidEmail)
                .when()
                .post(PetApiConfig.USER_ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", containsString("invalid email format"));
    }
}
