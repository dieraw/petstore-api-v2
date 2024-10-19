package com.example.petstore.api;

import com.example.petstore.api.models.Pet;
import com.example.petstore.api.parameters.RandomPetResolver;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import io.restassured.response.Response;

import java.util.Collections;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(RandomPetResolver.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class PetApiNegativeTest {

    @BeforeAll
    public void setup() {
        RestAssured.baseURI = PetApiConfig.BASE_URL;
    }

    // Метод для удаления питомца по ID перед тестами, где это нужно
    private void deletePetIfExists(int petId) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete(PetApiConfig.PET_ENDPOINT + "/" + petId)
                .then()
                .statusCode(anyOf(is(200), is(404)));  // Успешное удаление или питомец уже не существует
    }

    @Test
    @DisplayName("Invalid JSON returns 400")
    public void testInvalidJsonReturns400() {
        String invalidJson = "{ 'name': 'Doggie', 'status': 'available' "; // Неверный JSON

        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post(PetApiConfig.PET_ENDPOINT)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(400));
        assertThat(response.path("message"), equalTo("bad input"));
    }

    @Test
    @DisplayName("Empty request body returns 405")
    public void testEmptyRequestBodyReturns405() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("") // Пустое тело запроса
                .when()
                .post(PetApiConfig.PET_ENDPOINT)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(405));
        assertThat(response.path("message"), containsString("no data"));
    }

    @Test
    @DisplayName("Missing required fields returns 400")
    public void testMissingRequiredFieldsReturns400() {
        Pet petWithoutName = new Pet(0, null, Collections.singletonList(PetApiConfig.DEFAULT_PET_PHOTO_URL), null, "");

        deletePetIfExists(0); // Удаляем питомца, если он существует

        Response response = given()
                .contentType(ContentType.JSON)
                .body(petWithoutName)
                .when()
                .post(PetApiConfig.PET_ENDPOINT)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(400));
        assertThat(response.path("message"), containsString("missing required field"));
    }

    @Test
    @DisplayName("Invalid data type returns 400")
    public void testInvalidDataTypeReturns400() {
        Pet petWithNegativeId = new Pet(-1, "Doggie", Collections.singletonList(PetApiConfig.DEFAULT_PET_PHOTO_URL), null, "available");

        deletePetIfExists(-1); // Удаляем питомца с отрицательным ID, если он существует

        Response response = given()
                .contentType(ContentType.JSON)
                .body(petWithNegativeId)
                .when()
                .post(PetApiConfig.PET_ENDPOINT)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(400));
        assertThat(response.path("message"), containsString("Invalid ID supplied"));
    }

    @Test
    @DisplayName("Non-existent ID returns 404")
    public void testNonExistentIdReturns404() {
        Pet nonExistentPet = new Pet(99999999, "UpdatedDoggie", Collections.singletonList(PetApiConfig.DEFAULT_PET_PHOTO_URL), null, "available");

        deletePetIfExists(99999999); // Удаляем питомца с этим ID, если он существует

        Response response = given()
                .contentType(ContentType.JSON)
                .body(nonExistentPet)
                .when()
                .put(PetApiConfig.PET_ENDPOINT)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(404));
        assertThat(response.path("message"), equalTo("Pet not found"));
    }

    @Test
    @DisplayName("Invalid enum value returns 400")
    public void testInvalidEnumValueReturns400() {
        Pet petWithInvalidStatus = new Pet(0, "Doggie", Collections.singletonList(PetApiConfig.DEFAULT_PET_PHOTO_URL), null, "invalid_status");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(petWithInvalidStatus)
                .when()
                .post(PetApiConfig.PET_ENDPOINT)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(400));
        assertThat(response.path("message"), containsString("Invalid status supplied"));
    }

    @Test
    @DisplayName("Get non-existent pet returns 404")
    public void testGetNonExistentPetReturns404() {
        int nonExistentPetId = 99999999;

        deletePetIfExists(nonExistentPetId); // Удаляем питомца с этим ID, если он существует

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get(PetApiConfig.PET_ENDPOINT + "/" + nonExistentPetId)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(404));
        assertThat(response.path("message"), equalTo("Pet not found"));
    }

    @Test
    @DisplayName("Find pets by invalid status returns 400")
    public void testFindPetsByInvalidStatusReturns400() {
        String invalidStatus = "not_available"; // Невалидный статус

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get(PetApiConfig.FIND_BY_STATUS_ENDPOINT + "?status=" + invalidStatus) // Исправлено здесь
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(400));
        assertThat(response.path("message"), equalTo("Invalid status value"));
    }

    @Test
    @DisplayName("Upload invalid pet image returns 400")
    public void testUploadInvalidPetImageReturns400() {
        int petId = 1; // ID существующего питомца

        Response response = given()
                .multiPart("file", "invalid_file.txt", "Это не изображение".getBytes(), "text/plain")
                .when()
                .post(PetApiConfig.PET_ENDPOINT + "/" + petId + "/uploadImage")
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(400));
        assertThat(response.path("message"), equalTo("Invalid file format"));
    }

    @Test
    @DisplayName("Create pet with max length name")
    public void testCreatePetWithMaxLengthName() {
        String longName = "A".repeat(1000); // Предположим, максимальная длина имени — 1000 символов
        Pet petWithLongName = new Pet(0, longName, Collections.singletonList(PetApiConfig.DEFAULT_PET_PHOTO_URL), null, "available");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(petWithLongName)
                .when()
                .post(PetApiConfig.PET_ENDPOINT)
                .then()
                .extract().response();

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.path("name"), equalTo(longName));
    }

    @Test
    @DisplayName("Create Pet with Minimum ID")
    public void testCreatePetWithMinId() {
        Pet petWithMinId = new Pet(0, "Doggie", Collections.singletonList(PetApiConfig.DEFAULT_PET_PHOTO_URL), null, "available");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(petWithMinId)
                .when()
                .post(PetApiConfig.PET_ENDPOINT)
                .then()
                .extract().response();

        // Убедитесь, что код ответа 200 и ID больше 0
        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.path("id"), is(not(equalTo(0L))));
    }

}
