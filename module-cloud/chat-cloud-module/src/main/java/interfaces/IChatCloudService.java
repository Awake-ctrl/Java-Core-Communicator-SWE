package interfaces;

import models.ChatLoadRequest;
import models.ChatLoadResponse;
import models.ChatNewRequest;
import models.ChatNewResponse;

/**
 * IChatCloudService provides methods to create, update,
 * and fetch chat objects in the cloud. (Nikhil)
 */
public interface IChatCloudService {
    ChatNewResponse createChat(ChatNewRequest request);

    ChatLoadResponse loadChats(ChatLoadRequest request);

    byte[] getAttachment(String attachmentId) throws Exception;
}
