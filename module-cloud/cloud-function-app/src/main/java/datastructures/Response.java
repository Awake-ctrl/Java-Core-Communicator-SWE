package datastructures;

import com.fasterxml.jackson.databind.JsonNode;

public record Response(int status_code, String message, JsonNode data) {}
