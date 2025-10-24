package datastructures;

import com.fasterxml.jackson.databind.JsonNode;

public record Entity(String module, String table, String id, String type, int lastN, TimeRange timeRange, JsonNode data) {
}
