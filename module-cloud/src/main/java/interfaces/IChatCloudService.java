package interfaces;

import models.ChatNewRequest;
import models.ChatNewResponse;
import models.ChatLoadRequest;
import models.ChatLoadResponse;

public interface IChatCloudService {
    ChatNewResponse createChat(ChatNewRequest request);
    ChatLoadResponse loadChats(ChatLoadRequest request);
    byte[] getAttachment(String attachmentId) throws Exception;
}
