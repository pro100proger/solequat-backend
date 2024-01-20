package com.solequat.businesslogic.service;

import java.io.IOException;

import javax.mail.SendFailedException;

import org.hibernate.service.spi.ServiceException;

import com.core.dto.EmailDTO;
import com.core.dto.PasswordDTO;
import com.core.dto.UserEmailDTO;
import com.core.dto.UsernameDTO;
import com.core.entity.User;

public interface UserService {

    void enableUser(String email);

    User findUserByEmail(String email);

    EmailDTO getEmail(User user);
    UsernameDTO getUsername(User user);

    UsernameDTO updateUsername(User user, UsernameDTO usernameDTO);

    EmailDTO updateEmail(User user, UserEmailDTO userEmailDTO) throws IOException, SendFailedException;

    String updatePassword(User user, PasswordDTO newPassword) throws ServiceException;
}