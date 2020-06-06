package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")

public class AnswerController
{
    // Autowired answer service from quora business service
    @Autowired
    AnswerBusinessService answerBusinessService;

    // Autowired authorization service from quora business service
    @Autowired
    private AuthorizationService authorizationService;

    // Autowired question service from quora business service
    @Autowired
    QuestionService questionService;

    // This controller method is called when the request pattern is of
    // type 'createAnswer' and incoming request is of POST Type
    // The method calls the createAnswer() method in the business logic
    // Seeks for a controller method with mapping of type '/question/{questionId}answer/create'

    /**
     * Method is used to create answer with respect to question id
     *
     * @param answerRequest
     * @param questionUuId
     * @param authorization
     * @return answer response with the status created
     * @throws AuthorizationFailedException and throws InvalidQuestionException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public <AnswerRequest> ResponseEntity<?> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionUuId, @RequestHeader final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = authorizationService.isValidActiveAuthToken(authorization, ActionType.CREATE_ANSWER);
        //Gets the question object from the database
        Question question = questionService.getQuestionForUuId(questionUuId);
        //Gets the answer object from the database
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setAnswer(answerRequest.getAnswer());
        answer.setUuid(UUID.randomUUID().toString());
        answer.setUser(userAuthTokenEntity.getUser());
        ZonedDateTime now = ZonedDateTime.now();
        answer.setDate(now);
        //sends the answer object created in the database
        Answer createdAnswer = answerService.createAnswer(answer);
        //Response object for the answer created
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

}
