package com.example.jwtexample.model;

//az autentikáció után felküldött válasz modellje
public class AuthenticationResponse {
  private String token;

  public AuthenticationResponse() {
  }

  public AuthenticationResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
