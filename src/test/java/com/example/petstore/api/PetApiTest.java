package com.example.petstore.api;

import com.example.petstore.api.models.Pet;
import com.example.petstore.api.parameters.RandomPet;
import com.example.petstore.api.parameters.RandomPetResolver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(RandomPetResolver.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class PetApiTest {

    private Pet randomPet;

    @BeforeAll
    public void setup(@RandomPet Pet pet) {
        // Настраиваем базовый URI
        RestAssured.baseURI = PetApiConfig.BASE_URL;

        // Сохраняем случайного питомца для всех тестов
        randomPet = pet;
        // Добавляем питомца перед выполнением тестов
        given()
                .contentType(ContentType.JSON)
                .body(randomPet)
                .when()
                .post(PetApiConfig.PET_ENDPOINT)
                .then()
                .statusCode(200)
                .body("name", equalTo(randomPet.getName()))
                .body("status", equalTo(randomPet.getStatus()));
    }

    @BeforeAll
    public static void createMockImage() throws IOException {
        // Создаем тестовое изображение
        BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        File imageFile = new File(PetApiConfig.MOCK_IMAGE_PATH);
        ImageIO.write(bufferedImage, "jpg", imageFile);
    }

    @Test
    public void testGetPetById() {
        given()
                .when()
                .get(PetApiConfig.PET_ENDPOINT + "/" + randomPet.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo((int) randomPet.getId()))
                .body("name", equalTo(randomPet.getName()))
                .body("status", equalTo(randomPet.getStatus()));
    }

    @Test
    public void testFindPetsByStatusAvailable() {
        given()
                .when()
                .get(PetApiConfig.FIND_BY_STATUS_ENDPOINT + "?status=available")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    public void testUpdatePet() {
        // Обновляем имя питомца
        randomPet.setName("UpdatedDoggie");

        given()
                .contentType(ContentType.JSON)
                .body(randomPet)
                .when()
                .put(PetApiConfig.PET_ENDPOINT)
                .then()
                .statusCode(200)
                .body("name", equalTo("UpdatedDoggie"));
    }

    @Test
    public void testFindPetsByStatus() {
        given()
                .when()
                .get(PetApiConfig.FIND_BY_STATUS_ENDPOINT + "?status=" + randomPet.getStatus())
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("findAll { it.id == " + randomPet.getId() + " }.name", hasItem(randomPet.getName()));
    }

    @Test
    public void testUploadPetImage() {
        File imageFile = new File(PetApiConfig.MOCK_IMAGE_PATH);

        given()
                .contentType(ContentType.MULTIPART)
                .multiPart("file", imageFile)
                .when()
                .post(PetApiConfig.PET_ENDPOINT + "/" + randomPet.getId() + PetApiConfig.UPLOAD_IMAGE_ENDPOINT)
                .then()
                .statusCode(200)
                .body("message", containsString("uploaded"));
    }

    @Test
    public void testGetPetInfoType() {
        int petId = (int) randomPet.getId(); // Извлекаем ID из randomPet
        System.out.println(randomPet);

        given()
                .when()
                .get(PetApiConfig.PET_ENDPOINT + "/" + petId) // Используем ID из randomPet
                .then()
                .statusCode(200)
                .body("id", instanceOf(Number.class)) // Проверка, что id - это числовой тип
                .body("name", instanceOf(String.class)) // Проверка типа name
                .body("photoUrls", instanceOf(List.class)) // Проверка типа photoUrls
                .body("photoUrls[0]", instanceOf(String.class)) // Проверка типа первого элемента в photoUrls
                .body("tags", instanceOf(List.class)) // Проверка типа tags
                .body("tags[0]", instanceOf(Map.class)) // Проверка типа первого элемента в tags
                .body("tags[0].id", instanceOf(Number.class)) // Проверка типа id в tags
                .body("tags[0].name", instanceOf(String.class)) // Проверка типа name в tags
                .body("status", instanceOf(String.class)); // Проверка типа status
    }

    @AfterAll
    public void tearDown() {
        // Удаляем питомца после завершения всех тестов
        given()
                .when()
                .delete(PetApiConfig.PET_ENDPOINT + "/" + randomPet.getId())
                .then()
                .statusCode(200);
    }


    @AfterAll
    public static void cleanup() {
        // Удаляем тестовое изображение после всех тестов
        File imageFile = new File(PetApiConfig.MOCK_IMAGE_PATH);
        if (imageFile.exists()) {
            boolean deleted = imageFile.delete();
            if (!deleted) {
                System.err.println("Не удалось удалить тестовое изображение: " + PetApiConfig.MOCK_IMAGE_PATH);
            }
        }
    }
}

