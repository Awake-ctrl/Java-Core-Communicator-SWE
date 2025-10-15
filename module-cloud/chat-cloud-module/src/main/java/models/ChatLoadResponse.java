package models;

import java.util.List;

public record ChatLoadResponse(boolean success, List<ChatMessageDTO> chats, String message) { }