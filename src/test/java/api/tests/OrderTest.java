package api.tests;

import api.OrderApi;
import api.models.Order;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.notNullValue;

public class OrderTest extends BaseTest { //Наследование от BaseTest

    @ParameterizedTest
    @MethodSource("provideColors")
    @DisplayName("Создание заказа с разными вариантами цветов")
    @Step("Тест: Создание заказа с разными вариантами цветов")
    public void testCreateOrderWithDifferentColors(String[] colors) { // ← varargs
        Order order = createOrderWithColors(colors);
        Response response = sendCreateOrderRequest(order);
        verifyOrderCreation(response);
    }

    private static Stream<Arguments> provideColors() {
        return Stream.of(
                Arguments.of((Object) new String[]{"BLACK"}),
                Arguments.of((Object) new String[]{"GREY"}),
                Arguments.of((Object) new String[]{"BLACK", "GREY"}),
                Arguments.of((Object) new String[]{})
        );
    }

    @Step("Создание заказа с цветами: {colors}")
    private Order createOrderWithColors(String[] colors) {
        return new Order("Ван", "Ким", "Москва", "Маяковская", "+79168887788", 1, "26.01.2025", "Комментарий", colors);
    }

    @Step("Отправка запроса на создание заказа")
    private Response sendCreateOrderRequest(Order order) {
        return OrderApi.createOrder(order);
    }

    @Step("Проверка успешного создания заказа")
    private void verifyOrderCreation(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}