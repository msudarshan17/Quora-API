package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping

public class AdminController {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}")

    //Controller Method to delete an user
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId") final String userUuid
            , @RequestHeader("authorization") final String authorization) throws UserNotFoundException, AuthorizationFailedException {
        //Splitting the Access Token
        String[] bearerToken = authorization.split("Bearer");
        final UserEntity deletedUserEntity = userAdminBusinessService.deleteUser(userUuid, bearerToken[1]);
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(deletedUserEntity.getUuid()).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }

}
