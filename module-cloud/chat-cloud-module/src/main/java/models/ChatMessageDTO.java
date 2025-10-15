package models;

public record ChatMessageDTO(String from, String content, String attachment, long timestamp) { }