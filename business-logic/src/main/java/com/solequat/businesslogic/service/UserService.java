package com.solequat.businesslogic.service;

import java.io.IOException;

import javax.mail.SendFailedException;

import org.hibernate.service.spi.ServiceException;

import com.core.dto.PasswordDTO;
import com.core.dto.UserEmailDTO;
import com.core.dto.UsernameDTO;
import com.core.entity.User;

public interface UserService {

    void enableUser(String email);

    User findUserByEmail(String email);

    UsernameDTO updateUsername(User user, UsernameDTO usernameDTO);

    String updateEmail(User user, UserEmailDTO userEmailDTO) throws IOException, SendFailedException;

    String updatePassword(User user, PasswordDTO newPassword) throws ServiceException;
}