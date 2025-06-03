package com.inholland.bank.cucumber_testing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class CommonStepDefinitions {

  @Value("${local.server.port}")
  private int port;

  private final RestTemplate restTemplate = new RestTemplate();
  private String baseUrl;

  public void setBaseUrl() {
    this.baseUrl = "http://localhost:" + port;
  }

  public ResponseEntity<String> getResponseEntity(String endpoint, String requestBody) {
    return restTemplate.postForEntity(baseUrl + endpoint, requestBody, String.class);
  }
}