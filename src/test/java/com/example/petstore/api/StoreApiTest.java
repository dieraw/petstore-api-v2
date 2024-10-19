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
public class StoreApiTest {

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

        // Убедитесь, что идентификатор заказа установлен после создания
    }

    @Test
    public void testGetOrderInfoType() {
        int orderId = (int) randomOrder.getId(); // Извлекаем ID из randomOrder
        System.out.println(randomOrder);

        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + orderId) // Используем ID из randomOrder
                .then()
                .statusCode(200)
                .body("id", instanceOf(Number.class)) // Проверка, что id - это числовой тип
                .body("petId", instanceOf(Number.class)) // Проверка типа petId
                .body("quantity", instanceOf(Number.class)) // Проверка типа quantity
                .body("status", instanceOf(String.class)) // Проверка типа status
                .body("complete", instanceOf(Boolean.class)); // Проверка типа complete
    }

    @Test
    public void testGetOrderById() {
        given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + randomOrder.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo((int) randomOrder.getId()))
                .body("petId", equalTo((int) randomOrder.getPetId()))
                .body("quantity", equalTo(randomOrder.getQuantity()))
                .body("status", equalTo(randomOrder.getStatus()));
    }

    @Test
    public void testDeleteOrder() {
        // Убедитесь, что заказ существует перед удалением
        given()
                .when()
                .delete(PetApiConfig.ORDER_ENDPOINT + "/" + randomOrder.getId())
                .then()
                .statusCode(200); // Ожидаем, что заказ был успешно удалён
    }

    @Test
    public void testGetInventory() {
        given()
                .when()
                .get(PetApiConfig.INVENTORY_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$", is(notNullValue()));
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
        // Логика для проверки, был ли заказ удалён.
        // Например, можно сделать GET запрос и проверить статус.
        return given()
                .when()
                .get(PetApiConfig.ORDER_ENDPOINT + "/" + order.getId())
                .then()
                .extract()
                .statusCode() == 404; // 404 означает, что заказ не существует
    }

}
