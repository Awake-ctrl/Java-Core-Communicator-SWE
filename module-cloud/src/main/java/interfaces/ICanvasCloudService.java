package interfaces;

import models.*;

public interface ICanvasCloudService {
    CanvasCreateResponse createCanvas(CanvasCreateRequest request);
    CanvasUpdateResponse updateCanvas(CanvasUpdateRequest request);
    CanvasFetchResponse fetchCanvas(CanvasFetchRequest request);
}
