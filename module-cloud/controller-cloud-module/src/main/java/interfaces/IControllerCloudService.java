package interfaces;

import models.OTPRequest;
import models.OTPResponse;
import models.RegisterRequest;
import models.RegisterResponse;
import models.LoginResponse;
import models.LoginRequest;

/**
 * IControllerCloudService provides methods to create, update,
 * and fetch controller objects in the cloud. (Soorya)
 */
public interface IControllerCloudService {
    OTPResponse sendOTP(OTPRequest request);

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
