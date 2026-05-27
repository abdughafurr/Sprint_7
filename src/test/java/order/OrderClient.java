package order;

import io.restassured.response.Response;
import util.Endpoints;

import static io.restassured.RestAssured.given;

public class OrderClient {

    public Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(Endpoints.BASE_URL)
                .body(order)
                .when()
                .post(Endpoints.ORDERS);
    }

    public Response cancelOrder(int track) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(Endpoints.BASE_URL)
                .queryParam("track", track)
                .when()
                .put(Endpoints.ORDERS_CANCEL);
    }

    public Response getOrders() {
        return given()
                .header("Content-type", "application/json")
                .baseUri(Endpoints.BASE_URL)
                .when()
                .get(Endpoints.ORDERS);
    }
}