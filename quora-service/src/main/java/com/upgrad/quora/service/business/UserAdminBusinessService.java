package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    //Business Layer function to delete user
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String UserUuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        //FUnction to retrive the user details
        UserEntity userEntity = findUser(UserUuid, authorizationToken);
        return userDao.deleteUserbyAdmin(userEntity);

    }

    // Business Layer function to retrive the user details based on UUID
    public UserEntity findUser(final String UserUuid, final String authorizationToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getAuthByAccessToken(authorizationToken);
        UserEntity userEntity = userDao.getUser(UserUuid);
        //If the access token provided by the user does not exist in the database, throw 'AuthorizationFailedException'
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //If the user has signed out, throw 'AuthorizationFailedException'
        if (userAuthEntity != null && userAuthEntity.getLogoutAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }
        //If the user with uuid whose profile is to be deleted does not exist in the database, throw 'UserNotFoundException'
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }
        //If the role of the user is 'nonadmin',  throw 'AuthorizationFailedException'
        String role = userAuthEntity.getUser().getRole();
        if (role.equalsIgnoreCase("nonadmin") || role == null) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        return userEntity;
    }


}
