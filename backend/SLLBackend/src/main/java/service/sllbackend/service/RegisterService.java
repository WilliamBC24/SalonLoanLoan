package service.sllbackend.service;

import service.sllbackend.web.dto.UserRegisterDTO;

public interface RegisterService {
    void registerUser(UserRegisterDTO userRegisterDTO);
}
