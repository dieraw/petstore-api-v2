package com.example.petstore.api;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class StoreApiBoundaryTest {

    @BeforeAll
    public static void setup() {
        // Настраиваем базовый URI
        RestAssured.baseURI = PetApiConfig.BASE_URL;
    }

    // Граничный тест: Проверка минимального допустимого значения (id = 1)
    @Test
    public void testGetOrderWithMinBoundaryId() {
        int minValidId = 1; // Минимальное допустимое значение

        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + minValidId)
                .then()
                .statusCode(200) // Ожидаем успешный ответ
                .body("id", equalTo(minValidId));
    }

    // Граничный тест: Проверка максимального допустимого значения (id = 10)
    @Test
    public void testGetOrderWithMaxBoundaryId() {
        int maxValidId = 10; // Максимальное допустимое значение

        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + maxValidId)
                .then()
                .statusCode(200) // Ожидаем успешный ответ
                .body("id", equalTo(maxValidId));
    }

    // Негативный тест: Проверка значения ниже минимального допустимого (id = 0)
    @Test
    public void testGetOrderWithBelowMinBoundaryId() {
        int belowMinValidId = 0; // Значение ниже допустимого диапазона

        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + belowMinValidId)
                .then()
                .statusCode(400) // Ожидаем ошибку, так как значение ниже допустимого
                .body("message", equalTo("Invalid ID supplied"));
    }

    // Негативный тест: Проверка значения выше максимального допустимого (id = 11)
    @Test
    public void testGetOrderWithAboveMaxBoundaryId() {
        int aboveMaxValidId = 11; // Значение выше допустимого диапазона

        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + aboveMaxValidId)
                .then()
                .statusCode(400) // Ожидаем ошибку 400, так как значение выходит за пределы допустимого
                .body("message", equalTo("Order not found"));
    }
}
