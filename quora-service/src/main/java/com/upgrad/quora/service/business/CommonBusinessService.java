package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    // Business Layer Function to retrive all the user details based on UUID
    public com.upgrad.quora.service.entity.UserEntity getUserDetails(final String UserUuid, final String authorizationToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getAuthByAccessToken(authorizationToken);
        UserEntity userEntity = userDao.getUser(UserUuid);
        //If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException'
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        //If the user has signed out, throw "AuthorizationFailedException"
        if (userAuthEntity != null && userAuthEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        //If the user with uuid whose profile is to be retrieved does not exist in the database, throw 'UserNotFoundException'
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return userEntity;
    }

}
