Feature: Labcorp Automation Suite

  # ==========================================
  # UI SCENARIOS (Selenium WebDriver)
  # ==========================================
  @UI
  Scenario: Search and verify job details on Labcorp Careers
    Given I open the Chrome browser and navigate to "https://www.labcorp.com"
    When I navigate to the Careers page
    And I search for position "QA Test Automation Developer"
    And I select and browse to the matching position
    Then I verify the job details match the expected data:
      | title    | QA Test Automation Developer |
      | location | Burlington, NC               |
      | jobId    | 24-12345                     |
    And I verify additional specific requirements and introduction text
    When I click the Apply Now button
    Then I confirm the job details match on the application proceeding page
    And I click to return to the job search

  # ==========================================
  # API SCENARIOS (REST Assured)
  # ==========================================
  @API
  Scenario: Validate GET Request from Beeceptor Endpoint
    Given I prepare a GET request to "https://echo.free.beeceptor.com/sample-request?author=beeceptor"
    When I execute the GET request
    Then the response status code should be 200
    And the response should validate the path field contains "/sample-request"
    And the response should contain a valid client IP address
    And the response headers should contain "host" and "user-agent"

  @API
  Scenario: Validate POST Request with Order Details payload
    Given I prepare a POST request to "https://echo.free.beeceptor.com/sample-request?author=beeceptor"
    And I set the request payload as:
      """
      {
        "order_id": "12345",
        "customer": {
          "name": "Jane Smith",
          "email": "janesmith@example.com",
          "phone": "1-987-654-3210",
          "address": {
            "street": "456 Oak Street",
            "city": "Metropolis",
            "state": "NY",
            "zipcode": "10001",
            "country": "USA"
          }
        },
        "items": [
          {
            "product_id": "A101",
            "name": "Wireless Headphones",
            "quantity": 1,
            "price": 79.99
          },
          {
            "product_id": "B202",
            "name": "Smartphone Case",
            "quantity": 2,
            "price": 15.99
          }
        ],
        "payment": {
          "method": "credit_card",
          "transaction_id": "txn_67890",
          "amount": 111.97,
          "currency": "USD"
        },
        "shipping": {
          "method": "standard",
          "cost": 5.99,
          "estimated_delivery": "2024-11-15"
        },
        "order_status": "processing",
        "created_at": "2024-11-07T12:00:00Z"
      }
      """
    When I execute the POST request
    Then the response status code should be 200
    And the response payload echo should match the client data:
      | customerName  | Jane Smith      |
      | customerEmail | janesmith@example.com |
      | paymentMethod | credit_card     |
      | paymentAmount | 111.97          |
      | firstProduct  | Wireless Headphones |