package com.example.petstore.api;

import com.example.petstore.api.models.Order;
import com.example.petstore.api.parameters.RandomOrder;
import com.example.petstore.api.parameters.RandomOrderResolver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(RandomOrderResolver.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StoreApiNegativeTest {

    private Order randomOrder;

    @BeforeAll
    public void setup() {
        // Настраиваем базовый URI
        RestAssured.baseURI = PetApiConfig.BASE_URL;
    }

    @BeforeEach
    public void createOrder(@RandomOrder Order order) {
        // Сохраняем случайный заказ для текущего теста
        randomOrder = order;

        // Создаем заказ перед каждым тестом
        given()
                .contentType(ContentType.JSON)
                .body(randomOrder)
                .when()
                .post(PetApiConfig.ORDER_ENDPOINT)
                .then()
                .statusCode(200)
                .body("petId", equalTo(randomOrder.getPetId()))
                .body("quantity", equalTo(randomOrder.getQuantity()))
                .body("status", equalTo(randomOrder.getStatus()));
    }

    // Негативный тест: Попытка получить заказ по несуществующему ID
    @Test
    public void testGetOrderByNonExistentId() {
        long nonExistentOrderId = 99999; // Несуществующий ID

        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + nonExistentOrderId)
                .then()
                .statusCode(404) // Ожидаем статус 404
                .body("message", containsString("Order not found"));
    }

    // Негативный тест: Попытка удалить заказ по несуществующему ID
    @Test
    public void testDeleteOrderByNonExistentId() {
        long nonExistentOrderId = 99999; // Несуществующий ID

        given()
                .when()
                .delete(PetApiConfig.ORDER_ENDPOINT + "/" + nonExistentOrderId)
                .then()
                .statusCode(404) // Ожидаем статус 404
                .body("message", containsString("Order Not Found"));
    }

    // Негативный тест: Создание заказа с некорректными данными (например, отрицательный petId)
    @Test
    public void testCreateOrderWithInvalidData() {
        randomOrder.setPetId(-1); // Некорректный petId

        given()
                .contentType(ContentType.JSON)
                .body(randomOrder)
                .when()
                .post(PetApiConfig.ORDER_ENDPOINT)
                .then()
                .statusCode(400) // Ожидаем статус 400
                .body("message", containsString("Invalid input"));
    }

  //Попытка получить заказ с некорректным ID (например, строкой)
    @Test
    public void testGetOrderWithInvalidId() {
        String invalidOrderId = "abc"; // Некорректный ID

        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + invalidOrderId)
                .then()
                .statusCode(400) // Ожидаем статус 400
                .body("message", containsString("Invalid ID"));
    }

    // Негативный тест: Попытка удалить заказ с некорректным ID (например, строкой)
    @Test
    public void testDeleteOrderWithInvalidId() {
        String invalidOrderId = "abc"; // Некорректный ID

        given()
                .when()
                .delete(PetApiConfig.ORDER_ENDPOINT + "/" + invalidOrderId)
                .then()
                .statusCode(400) // Ожидаем статус 400
                .body("message", containsString("Invalid ID"));
    }

    // Негативный тест: Некорректный запрос для получения инвентаря
    @Test
    public void testGetInventoryWithInvalidEndpoint() {
        // Используем неправильный endpoint для инвентаря
        given()
                .when()
                .get(PetApiConfig.BASE_URL + "/store/wrong_inventory")
                .then()
                .statusCode(404) // Ожидаем статус 404
                .body("message", containsString("Not Found"));
    }

    @AfterEach
    public void tearDown() {
        // Удаляем заказ после всех тестов, если он не был удалён
        if (randomOrder != null && !isOrderDeleted(randomOrder)) {
            given()
                    .when()
                    .delete(PetApiConfig.ORDER_ENDPOINT + "/" + randomOrder.getId())
                    .then()
                    .statusCode(200);
        }
    }

    private boolean isOrderDeleted(Order order) {
        // Логика для проверки, был ли заказ удалён
        return given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + order.getId())
                .then()
                .extract()
                .statusCode() == 404; // 404 означает, что заказ не существует
    }
}
