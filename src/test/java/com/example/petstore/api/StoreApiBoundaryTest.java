package com.example.petstore.api;

import com.example.petstore.api.models.Order;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class StoreApiBoundaryTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = PetApiConfig.BASE_URL;
    }

    // Метод для создания заказа перед тестом
    private void createOrder(Order order) {
        given()
                .contentType("application/json")
                .body(order)
                .when()
                .post(PetApiConfig.ORDER_ENDPOINT)
                .then()
                .statusCode(200); // Ожидаем успешное создание заказа
    }

    // Граничный тест: Проверка минимального допустимого значения (id = 1)
    @Test
    public void testGetOrderWithMinBoundaryId() {
        int minValidId = 1;

        // Создаем заказ с минимальным id
        Order order = new Order(minValidId, 100, 1, "2024-10-20T12:34:56", "placed", true);
        createOrder(order);

        // Проверяем, что заказ можно получить по этому id
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
        int maxValidId = 10;

        // Создаем заказ с максимальным id
        Order order = new Order(maxValidId, 101, 2, "2024-10-21T12:34:56", "approved", false);
        createOrder(order);

        // Проверяем, что заказ можно получить по этому id
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

        // Проверяем, что при запросе заказа с id ниже допустимого API возвращает ошибку
        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + belowMinValidId)
                .then()
                .statusCode(400) // Ожидаем ошибку
                .body("message", equalTo("Invalid ID supplied"));
    }

    // Негативный тест: Проверка значения выше максимального допустимого (id = 11)
    @Test
    public void testGetOrderWithAboveMaxBoundaryId() {
        int aboveMaxValidId = 11; // Значение выше допустимого диапазона

        // Проверяем, что при запросе заказа с id выше допустимого API возвращает ошибку
        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + aboveMaxValidId)
                .then()
                .statusCode(400) // Ожидаем ошибку
                .body("message", equalTo("Order not found"));
    }
}
