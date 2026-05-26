package courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

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
    public void courierCanLogin() {
        String login = randomLogin();
        Courier courier = new Courier(login, "loginPass111", "LoginName");
        courierClient.create(courier);
        Response response = loginCourier(new CourierCredentials(login, "loginPass111"));
        checkStatusCode(response, 200);
        checkIdNotNull(response);
        courierId = response.then().extract().path("id");
    }

    @Test
    public void loginFailsWithWrongPassword() {
        String login = randomLogin();
        Courier courier = new Courier(login, "loginPass222", "LoginName");
        courierClient.create(courier);
        courierId = loginAndGetId(courier);
        Response response = loginCourier(new CourierCredentials(login, "wrongPassword"));
        checkStatusCode(response, 404);
    }

    @Test
    public void loginFailsWithWrongLogin() {
        String login = randomLogin();
        Courier courier = new Courier(login, "loginPass333", "LoginName");
        courierClient.create(courier);
        courierId = loginAndGetId(courier);
        Response response = loginCourier(new CourierCredentials("wrongLogin_xyz", courier.getPassword()));
        checkStatusCode(response, 404);
    }

    @Test
    public void loginFailsWithoutLogin() {
        Response response = loginCourier(new CourierCredentials("", "somePassword"));
        checkStatusCode(response, 400);
    }

    @Test
    public void loginFailsWithoutPassword() {
        Response response = loginCourier(new CourierCredentials("someLogin", ""));
        checkStatusCode(response, 400);
    }

    @Test
    public void loginFailsForNonExistentCourier() {
        Response response = loginCourier(new CourierCredentials("noSuchUser_xyz999", "noSuchPass999"));
        checkStatusCode(response, 404);
    }

    @Step("Логин курьера")
    public Response loginCourier(CourierCredentials credentials) {
        return courierClient.login(credentials);
    }

    @Step("Проверить статус код: {expectedCode}")
    public void checkStatusCode(Response response, int expectedCode) {
        response.then().statusCode(expectedCode);
    }

    @Step("Проверить что id не null")
    public void checkIdNotNull(Response response) {
        response.then().body("id", notNullValue());
    }

    @Step("Залогиниться и получить id")
    public int loginAndGetId(Courier courier) {
        return courierClient.login(
                new CourierCredentials(courier.getLogin(), courier.getPassword())
        ).then().extract().path("id");
    }
}