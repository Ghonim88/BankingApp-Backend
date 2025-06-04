package com.inholland.bank.cucumber_testing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class CommonStepDefinitions {

  private ResponseEntity<String> response;

  public ResponseEntity<String> getResponse() {
    return response;
  }

  public void setResponse(ResponseEntity<String> response) {
    this.response = response;
  }
}