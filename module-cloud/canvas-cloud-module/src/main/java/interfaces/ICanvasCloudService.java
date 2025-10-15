package interfaces;

import models.CanvasCreateRequest;
import models.CanvasCreateResponse;
import models.CanvasUpdateRequest;
import models.CanvasUpdateResponse;
import models.CanvasFetchResponse;
import models.CanvasFetchRequest;

/**
 * ICanvasCloudService provides methods to create, update,
 * and fetch canvas objects in the cloud.  (Prabhu)
 */
public interface ICanvasCloudService {
    CanvasCreateResponse createCanvas(CanvasCreateRequest request);

    CanvasUpdateResponse updateCanvas(CanvasUpdateRequest request);

    CanvasFetchResponse fetchCanvas(CanvasFetchRequest request);
}
