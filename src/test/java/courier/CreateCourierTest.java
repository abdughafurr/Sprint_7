package courier;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private final CourierClient courierClient = new CourierClient();
    private int courierId;

    private String randomLogin() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @Story("Курьера можно создать")
    @Description("Проверяем что курьер успешно создаётся и возвращается ok: true")
    public void courierCanBeCreated() {
        Courier courier = new Courier(randomLogin(), "testPass123", "TestName");
        Response response = createCourier(courier);
        checkStatusCode(response, HttpStatus.SC_CREATED);
        checkOkTrue(response);
        courierId = loginAndGetId(courier);
    }

    @Test
    @Story("Нельзя создать двух одинаковых курьеров")
    @Description("Проверяем что при создании дубликата возвращается 409 и сообщение об ошибке")
    public void cannotCreateDuplicateCourier() {
        String login = randomLogin();
        Courier courier = new Courier(login, "dupPass123", "DupName");
        createCourier(courier);
        courierId = loginAndGetId(courier);
        Response response = createCourier(courier);
        checkStatusCode(response, HttpStatus.SC_CONFLICT);
        checkErrorMessage(response, "Этот логин уже используется. Попробуйте другой.");
    }

    @Test
    @Story("Нельзя создать курьера без логина")
    @Description("Проверяем что без логина возвращается 400 и сообщение об ошибке")
    public void cannotCreateCourierWithoutLogin() {
        Courier courier = new Courier(null, "testPass123", "TestName");
        Response response = createCourier(courier);
        checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        checkErrorMessage(response, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Story("Нельзя создать курьера без пароля")
    @Description("Проверяем что без пароля возвращается 400 и сообщение об ошибке")
    public void cannotCreateCourierWithoutPassword() {
        Courier courier = new Courier(randomLogin(), null, "TestName");
        Response response = createCourier(courier);
        checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        checkErrorMessage(response, "Недостаточно данных для создания учетной записи");
    }

    @Step("Создать курьера")
    public Response createCourier(Courier courier) {
        return courierClient.create(courier);
    }

    @Step("Проверить статус код: {expectedCode}")
    public void checkStatusCode(Response response, int expectedCode) {
        response.then().statusCode(expectedCode);
    }

    @Step("Проверить ok: true в ответе")
    public void checkOkTrue(Response response) {
        response.then().body("ok", equalTo(true));
    }

    @Step("Проверить сообщение об ошибке: {message}")
    public void checkErrorMessage(Response response, String message) {
        response.then().body("message", equalTo(message));
    }

    @Step("Залогиниться и получить id курьера")
    public int loginAndGetId(Courier courier) {
        return courierClient.login(
                new CourierCredentials(courier.getLogin(), courier.getPassword())
        ).then().extract().path("id");
    }
}