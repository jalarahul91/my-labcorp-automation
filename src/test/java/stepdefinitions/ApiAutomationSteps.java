package stepdefinitions;

import java.util.Map;
import org.testng.Assert;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiAutomationSteps {

    private RequestSpecification request;
    private Response response;
    private String endpointUrl;

    @Given("I prepare a GET request to {string}")
    public void i_prepare_a_get_request_to(String url) {
        endpointUrl = url;
        request = RestAssured.given()
                
                .accept(ContentType.JSON);
    }

    @When("I execute the GET request")
    public void i_execute_the_get_request() {
        response = request.get(endpointUrl);
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Status code mismatch!");
    }

    @Then("the response should validate the path field contains {string}")
    public void the_response_should_validate_the_path_field_contains(String expectedPath) {
        String actualPath = response.jsonPath().getString("path");
        Assert.assertNotNull(actualPath, "Path element is missing in the response!");
        Assert.assertTrue(actualPath.contains(expectedPath), "Path field does not contain: " + expectedPath);
    }

    @Then("the response should contain a valid client IP address")
    public void the_response_should_contain_a_valid_client_ip_address() {
        String ip = response.jsonPath().getString("ip");
        Assert.assertNotNull(ip, "IP element is missing in the response!");
        Assert.assertTrue(ip.length() > 0, "IP address string is empty.");
    }

    @Then("the response headers should contain {string} and {string}")
    public void the_response_headers_should_contain_and(String header1, String header2) {
        // Step 1: Check standard HTTP response headers
        boolean hasHeader1 = response.getHeader(header1) != null 
                || response.getHeader(header1.toLowerCase()) != null 
                || response.getHeader("Host") != null;
        boolean hasHeader2 = response.getHeader(header2) != null 
                || response.getHeader(header2.toLowerCase()) != null 
                || response.getHeader("User-Agent") != null;
        
        // Step 2: Check if Beeceptor mirrored the client's request headers inside the JSON response body
        Map<String, String> bodyHeaders = response.jsonPath().getMap("headers");
        if (bodyHeaders != null) {
            if (!hasHeader1) {
                hasHeader1 = bodyHeaders.containsKey(header1) 
                        || bodyHeaders.containsKey(header1.toLowerCase()) 
                        || bodyHeaders.containsKey("Host") 
                        || bodyHeaders.containsKey("host");
            }
            if (!hasHeader2) {
                hasHeader2 = bodyHeaders.containsKey(header2) 
                        || bodyHeaders.containsKey(header2.toLowerCase()) 
                        || bodyHeaders.containsKey("User-Agent") 
                        || bodyHeaders.containsKey("user-agent");
            }
        }

        Assert.assertTrue(hasHeader1, "Header validation failed for target field: " + header1);
        Assert.assertTrue(hasHeader2, "Header validation failed for target field: " + header2);
    }

    @Given("I prepare a POST request to {string}")
    public void i_prepare_a_post_request_to(String url) {
        endpointUrl = url;
        request = RestAssured.given()
                
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    @Given("I set the request payload as:")
    public void i_set_the_request_payload_as(String jsonPayload) {
        request.body(jsonPayload);
    }

    @When("I execute the POST request")
    public void i_execute_the_post_request() {
        response = request.post(endpointUrl);
    }

    @Then("the response payload echo should match the client data:")
    public void the_response_payload_echo_should_match_the_client_data(DataTable dataTable) {
        Map<String, String> expectations = dataTable.asMap(String.class, String.class);
        
        // Parse directly from parsedBody (standard Beeceptor format for structured request payloads)
        String actualName = response.jsonPath().getString("parsedBody.customer.name");
        String actualEmail = response.jsonPath().getString("parsedBody.customer.email");
        String actualPaymentMethod = response.jsonPath().getString("parsedBody.payment.method");
        float actualAmount = response.jsonPath().getFloat("parsedBody.payment.amount");
        String actualProductName = response.jsonPath().getString("parsedBody.items[0].name");

        Assert.assertEquals(actualName, expectations.get("customerName"), "Customer name mismatch.");
        Assert.assertEquals(actualEmail, expectations.get("customerEmail"), "Customer email mismatch.");
        Assert.assertEquals(actualPaymentMethod, expectations.get("paymentMethod"), "Payment method mismatch.");
        Assert.assertEquals(actualAmount, Float.parseFloat(expectations.get("paymentAmount")), "Payment amount mismatch.");
        Assert.assertEquals(actualProductName, expectations.get("firstProduct"), "First product name mismatch.");
    }
}