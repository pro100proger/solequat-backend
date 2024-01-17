package com.solequat.businesslogic.service;

import com.core.dto.UsernameDTO;
import com.core.entity.User;

public interface UserService {

    void enableUser(String email);

    User findUserByEmail(String email);

    UsernameDTO updateUsername(User user, UsernameDTO usernameDTO);

}