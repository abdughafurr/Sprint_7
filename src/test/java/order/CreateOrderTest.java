package order;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final List<String> color;
    private int track;
    private final OrderClient orderClient = new OrderClient();

    public CreateOrderTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет самоката: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()}
        });
    }

    @After
    public void tearDown() {
        if (track != 0) {
            orderClient.cancelOrder(track);
        }
    }

    @Test
    @Story("Создание заказа с разными цветами")
    @Description("Проверяем что заказ создаётся и возвращается track для всех вариантов цвета")
    public void createOrderWithDifferentColors() {
        Order order = new Order(
                "Иван", "Иванов", "Москва, ул. Ленина 1",
                "1", "+79001234567", 3,
                "2024-12-31", "Позвоните заранее", color
        );
        Response response = createOrder(order);
        checkStatusCode(response, HttpStatus.SC_CREATED);
        checkTrackNotNull(response);
        track = response.then().extract().path("track");
    }

    @Step("Создать заказ")
    public Response createOrder(Order order) {
        return orderClient.createOrder(order);
    }

    @Step("Проверить статус код: {expectedCode}")
    public void checkStatusCode(Response response, int expectedCode) {
        response.then().statusCode(expectedCode);
    }

    @Step("Проверить что track не null")
    public void checkTrackNotNull(Response response) {
        response.then().body("track", notNullValue());
    }
}