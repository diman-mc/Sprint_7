package api.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    @BeforeEach
    public void setUpBase() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }
}
