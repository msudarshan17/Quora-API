package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/users/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//Controller Function to retrive the User Details
    public ResponseEntity<com.upgrad.quora.api.model.UserDetailsResponse> getUserDetails(@PathVariable("userId") final String userUuid
            , @RequestHeader("authorization") String authorization) throws UserNotFoundException, AuthorizationFailedException {
        //Splitting teh Access Token
        String[] bearerToken = authorization.split("Bearer");
        final UserEntity userEntity = commonBusinessService.getUserDetails(userUuid, bearerToken[1]);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName()).userName(userEntity.getUserName()).emailAddress(userEntity.getEmailAddress())
                .country(userEntity.getCountry()).aboutMe(userEntity.getAboutMe()).dob(userEntity.getDob())
                .contactNumber(userEntity.getContactNumber());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }
}


