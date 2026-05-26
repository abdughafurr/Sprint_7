package courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
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
    public void courierCanBeCreated() {
        Courier courier = new Courier(randomLogin(), "testPass123", "TestName");
        Response response = createCourier(courier);
        checkStatusCode(response, 201);
        checkOkTrue(response);
        courierId = loginAndGetId(courier);
    }

    @Test
    public void cannotCreateDuplicateCourier() {
        String login = randomLogin();
        Courier courier = new Courier(login, "dupPass123", "DupName");
        createCourier(courier);
        courierId = loginAndGetId(courier);
        Response response = createCourier(courier);
        checkStatusCode(response, 409);
    }

    @Test
    public void cannotCreateCourierWithoutLogin() {
        Courier courier = new Courier(null, "testPass123", "TestName");
        Response response = createCourier(courier);
        checkStatusCode(response, 400);
    }

    @Test
    public void cannotCreateCourierWithoutPassword() {
        Courier courier = new Courier(randomLogin(), null, "TestName");
        Response response = createCourier(courier);
        checkStatusCode(response, 400);
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

    @Step("Залогиниться и получить id курьера")
    public int loginAndGetId(Courier courier) {
        return courierClient.login(
                new CourierCredentials(courier.getLogin(), courier.getPassword())
        ).then().extract().path("id");
    }
}