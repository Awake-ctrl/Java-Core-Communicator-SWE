package interfaces;

import models.*;

public interface IControllerCloudService {
    OTPResponse sendOTP(OTPRequest request);
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
