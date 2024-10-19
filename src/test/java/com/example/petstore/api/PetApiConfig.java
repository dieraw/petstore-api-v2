package com.example.petstore.api;

public class PetApiConfig {

    public static final String BASE_URL = "https://petstore.swagger.io/v2";

    // Пути к endpoints
    public static final String PET_ENDPOINT = "/pet";
    public static final String UPLOAD_IMAGE_ENDPOINT = "/uploadImage";
    public static final String FIND_BY_STATUS_ENDPOINT = "/pet/findByStatus";
    public static final String ORDER_ENDPOINT = "/store/order";
    public static final String INVENTORY_ENDPOINT = "/store/inventory";

    // Путь к тестовому изображению
    public static final String MOCK_IMAGE_PATH = "src/test/resources/mocked_image.jpg";
    public static final String DEFAULT_PET_PHOTO_URL = "http://example.com/photo.jpg"; // Вынесенный URL
    public static final String USER_ENDPOINT = "/user";
    public static final String USER_LOGIN_ENDPOINT = "/user/login";
    public static final String USER_LOGOUT_ENDPOINT = "/user/logout";
    public static final String USER_CREATE_WITH_LIST_ENDPOINT = "/user/createWithList";
    public static final String USER_CREATE_WITH_ARRAY_ENDPOINT = "/user/createWithArray";

    // Параметры по умолчанию
    public static final String DEFAULT_STATUS = "available";
}
