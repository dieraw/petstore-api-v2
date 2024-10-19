package com.example.petstore.api.parameters;

import com.example.petstore.api.models.Pet;
import com.example.petstore.api.models.Tag;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

public class RandomPetResolver implements ParameterResolver {

    private static final Random random = new Random();
    private static final String MOCK_IMAGE_PATH = "src/test/resources/mocked_image.jpg"; // Путь к мок-изображению

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(RandomPet.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();

        // Проверяем, что параметр является объектом Pet
        if (Pet.class.equals(type)) {
            // Используем путь к мок-изображению в качестве photoUrl
            File mockImageFile = new File(MOCK_IMAGE_PATH);
            if (!mockImageFile.exists()) {
                throw new ParameterResolutionException("Mock image not found at " + MOCK_IMAGE_PATH);
            }

            return new Pet(
                    random.nextInt(1000),  // Случайный ID
                    "RandomPet" + random.nextInt(1000),  // Случайное имя
                    Arrays.asList(mockImageFile.getAbsolutePath()),  // Используем путь к мок-изображению как photoUrl
                    Arrays.asList(new Tag(random.nextInt(100), "Tag" + random.nextInt(100))),  // Случайные теги
                    random.nextBoolean() ? "available" : "sold" // Случайный статус
            );
        }

        throw new ParameterResolutionException("Unsupported parameter type: " + type.getName());
    }
}
