package api.tests;

import api.OrderApi;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest extends BaseTest { //Наследование от BaseTest

    @Test
    @DisplayName("Получение списка заказов через ручку /v1/orders")
    @Step("Тест: Получение списка заказов")
    public void testGetOrders() {
        Response response = OrderApi.getOrders();
        verifyGetOrdersResponse(response);
    }

    @Step("Проверка ответа на запрос списка заказов")
    private void verifyGetOrdersResponse(Response response) {
        response.then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .and()
                .body(notNullValue())
                .body("orders", notNullValue());
    }
}