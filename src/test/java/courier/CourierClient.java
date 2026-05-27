package courier;

import io.restassured.response.Response;
import util.Endpoints;

import static io.restassured.RestAssured.given;

public class CourierClient {

    public Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(Endpoints.BASE_URL)
                .body(courier)
                .when()
                .post(Endpoints.COURIER);
    }

    public Response login(CourierCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(Endpoints.BASE_URL)
                .body(credentials)
                .when()
                .post(Endpoints.COURIER_LOGIN);
    }

    public Response delete(int id) {
        return given()
                .header("Content-type", "application/json")
                .baseUri(Endpoints.BASE_URL)
                .when()
                .delete(Endpoints.COURIER + "/" + id);
    }
}