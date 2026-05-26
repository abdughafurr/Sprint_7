package courier;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    public Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    public Response login(CourierCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .body(credentials)
                .when()
                .post("/api/v1/courier/login");
    }

    public Response delete(int id) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(BASE_URL)
                .when()
                .delete("/api/v1/courier/" + id);
    }
}