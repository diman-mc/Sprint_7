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

public class CourierCreationTest extends BaseTest { // Наследование

    private Courier courier;
    private String courierId;

    @BeforeEach
    @Step("Настройка тестовых данных")
    public void setUp() {
        courier = new Courier("default_login", "default_password", "default_firstName");
    }

    @AfterEach
    @Step("Удаление тестовых данных")
    public void tearDown() {
        if (courierId != null) {
            CourierApi.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Создание курьера")
    @Step("Тест: Создание курьера")
    public void testCreateCourier() {
        String uniqueLogin = generateUniqueLogin();
        courier.setLogin(uniqueLogin);
        Response response = CourierApi.createCourier(courier);
        verifyCourierCreation(response);
        Response loginResponse = CourierApi.loginCourier(courier);
        courierId = loginResponse.then().extract().path("id").toString();
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Step("Тест: Нельзя создать двух одинаковых курьеров")
    public void testCreateDuplicateCourier() {
        CourierApi.createCourier(courier);
        Response response = CourierApi.createCourier(courier);
        verifyDuplicateCourierError(response);
        //Сохраняем ID, чтобы удалить в tearDown
        Response loginResponse = CourierApi.loginCourier(courier);
        courierId = loginResponse.then().extract().path("id").toString();
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Step("Тест: Создание курьера без логина")
    public void testCreateCourierWithoutLogin() {
        Courier invalidCourier = new Courier("", courier.getPassword(), courier.getFirstName());
        Response response = CourierApi.createCourier(invalidCourier);
        verifyRequiredFieldsError(response);
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Step("Тест: Создание курьера без пароля")
    public void testCreateCourierWithoutPassword() {
        Courier invalidCourier = new Courier(courier.getLogin(), "", courier.getFirstName());
        Response response = CourierApi.createCourier(invalidCourier);
        verifyRequiredFieldsError(response);
    }

    @Step("Удаление курьера с ID: {courierId}")
    private void deleteCourier(String courierId) {
        CourierApi.deleteCourier(courierId);
    }

    @Step("Генерация уникального логина")
    private String generateUniqueLogin() {
        return "courier_" + System.currentTimeMillis();
    }

    @Step("Проверка успешного создания курьера")
    private void verifyCourierCreation(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_CREATED)
                .and()
                .body("ok", equalTo(true));
    }

    @Step("Проверка ошибки при создании дубликата курьера")
    private void verifyDuplicateCourierError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Проверка ошибки при отсутствии обязательных полей")
    private void verifyRequiredFieldsError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}