package order;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    public Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    public Response getOrders() {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .when()
                .get("/api/v1/orders");
    }
}