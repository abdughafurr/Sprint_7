package order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {

    private final OrderClient orderClient = new OrderClient();

    @Test
    public void getOrdersReturnsList() {
        Response response = getOrders();
        checkStatusCode(response, 200);
        checkOrdersListNotNull(response);
    }

    @Step("Получить список заказов")
    public Response getOrders() {
        return orderClient.getOrders();
    }

    @Step("Проверить статус код: {expectedCode}")
    public void checkStatusCode(Response response, int expectedCode) {
        response.then().statusCode(expectedCode);
    }

    @Step("Проверить что список заказов не null")
    public void checkOrdersListNotNull(Response response) {
        response.then().body("orders", notNullValue());
    }
}