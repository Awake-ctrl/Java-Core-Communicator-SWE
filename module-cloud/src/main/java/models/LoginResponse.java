package models;

public record LoginResponse(boolean success, String access_token, String message) { }
