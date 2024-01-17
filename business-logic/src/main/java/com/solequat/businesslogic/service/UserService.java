package com.solequat.businesslogic.service;

import java.io.IOException;

import javax.mail.SendFailedException;

import com.core.dto.UserEmailDTO;
import com.core.dto.UsernameDTO;
import com.core.entity.User;

public interface UserService {

    void enableUser(String email);

    User findUserByEmail(String email);

    UsernameDTO updateUsername(User user, UsernameDTO usernameDTO);

    UserEmailDTO updateEmail(User user, UserEmailDTO userEmailDTO) throws IOException, SendFailedException;
}