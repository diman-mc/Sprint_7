package api.tests;

import api.CourierApi;
import api.models.Courier;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest extends BaseTest { // Наследование

    private Courier courier;
    private String courierId;

    @BeforeEach
    @Step("Настройка тестовых данных")
    public void setUp() {
        courier = new Courier("default_login", "default_password", "default_firstName");
        //Создание курьера
        /*Response createResponse = CourierApi.createCourier(courier);
        createResponse.then().statusCode(SC_CREATED);*/
    }

    @AfterEach
    @Step("Удаление тестовых данных")
    public void tearDown() {
        if (courierId != null) {
            CourierApi.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Логин курьера")
    @Step("Тест: Логин курьера")
    public void testLoginCourier() {
        // Создание
        Response createResponse = CourierApi.createCourier(courier);
        createResponse.then().statusCode(SC_CREATED);
        // Логинимся
        Response response = CourierApi.loginCourier(courier);
        verifySuccessfulLogin(response);
        courierId = response.then().extract().path("id").toString();
    }

    @Test
    @DisplayName("Логин курьера с неверным логином")
    @Step("Тест: Логин курьера с неверным логином")
    public void testLoginCourierWithInvalidLogin() {
        Courier invalidCourier = new Courier("invalid", courier.getPassword(), "");
        Response response = CourierApi.loginCourier(invalidCourier);
        verifyInvalidLoginError(response);
    }

    @Test
    @DisplayName("Логин курьера с неверным паролем")
    @Step("Тест: Логин курьера с неверным паролем")
    public void testLoginCourierWithInvalidPassword() {
        Courier invalidCourier = new Courier(courier.getLogin(), "invalid", "");
        Response response = CourierApi.loginCourier(invalidCourier);
        verifyInvalidLoginError(response);
    }

    @Step("Проверка успешного логина")
    private void verifySuccessfulLogin(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_OK)
                .and()
                .body("id", notNullValue());
    }

    @Step("Проверка ошибки при неверных данных")
    private void verifyInvalidLoginError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Удаление курьера с ID: {courierId}")
    private void deleteCourier(String courierId) {
        CourierApi.deleteCourier(courierId);
    }
}
