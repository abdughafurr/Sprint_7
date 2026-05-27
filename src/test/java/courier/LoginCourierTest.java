package courier;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private final CourierClient courierClient = new CourierClient();
    private int courierId;
    private Courier courier;

    private String randomLogin() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Before
    public void setUp() {
        courier = new Courier(randomLogin(), "loginPass111", "LoginName");
        courierClient.create(courier);
        courierId = loginAndGetId(courier);
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @Story("Курьер может авторизоваться")
    @Description("Проверяем успешную авторизацию и наличие id в ответе")
    public void courierCanLogin() {
        Response response = loginCourier(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        checkStatusCode(response, HttpStatus.SC_OK);
        checkIdNotNull(response);
    }

    @Test
    @Story("Логин с неверным паролем возвращает ошибку")
    @Description("Проверяем что при неверном пароле возвращается 404 и сообщение об ошибке")
    public void loginFailsWithWrongPassword() {
        Response response = loginCourier(new CourierCredentials(courier.getLogin(), "wrongPassword"));
        checkStatusCode(response, HttpStatus.SC_NOT_FOUND);
        checkErrorMessage(response, "Учетная запись не найдена");
    }

    @Test
    @Story("Логин с неверным логином возвращает ошибку")
    @Description("Проверяем что при неверном логине возвращается 404 и сообщение об ошибке")
    public void loginFailsWithWrongLogin() {
        Response response = loginCourier(new CourierCredentials("wrongLogin_xyz", courier.getPassword()));
        checkStatusCode(response, HttpStatus.SC_NOT_FOUND);
        checkErrorMessage(response, "Учетная запись не найдена");
    }

    @Test
    @Story("Логин без логина возвращает ошибку")
    @Description("Проверяем что без логина возвращается 400 и сообщение об ошибке")
    public void loginFailsWithoutLogin() {
        Response response = loginCourier(new CourierCredentials("", "somePassword"));
        checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        checkErrorMessage(response, "Недостаточно данных для входа");
    }

    @Test
    @Story("Логин без пароля возвращает ошибку")
    @Description("Проверяем что без пароля возвращается 400 и сообщение об ошибке")
    public void loginFailsWithoutPassword() {
        Response response = loginCourier(new CourierCredentials("someLogin", ""));
        checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        checkErrorMessage(response, "Недостаточно данных для входа");
    }

    @Test
    @Story("Логин несуществующего курьера возвращает ошибку")
    @Description("Проверяем что при несуществующем пользователе возвращается 404 и сообщение об ошибке")
    public void loginFailsForNonExistentCourier() {
        Response response = loginCourier(new CourierCredentials("noSuchUser_xyz999", "noSuchPass999"));
        checkStatusCode(response, HttpStatus.SC_NOT_FOUND);
        checkErrorMessage(response, "Учетная запись не найдена");
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

    @Step("Проверить сообщение об ошибке: {message}")
    public void checkErrorMessage(Response response, String message) {
        response.then().body("message", equalTo(message));
    }

    @Step("Залогиниться и получить id")
    public int loginAndGetId(Courier courier) {
        return courierClient.login(
                new CourierCredentials(courier.getLogin(), courier.getPassword())
        ).then().extract().path("id");
    }
}