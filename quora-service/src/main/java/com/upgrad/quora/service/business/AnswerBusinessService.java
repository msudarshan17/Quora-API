package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.upgrad.quora.service.entity.Answer;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.type.ActionType;
import com.upgrad.quora.service.type.RoleType;

import java.util.List;

@Service
public class AnswerBusinessService {
    @Autowired
    UserDao UserDao;

    @Autowired
    QuestionDao questionDao;

    @Autowired
    AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Answer createAnswer(Answer answer) {
        return answerDao.createAnswer(answer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Answer create(Answer answer, String authorizationToken, String questionId) throws AuthorizationFailedException,
            InvalidQuestionException {

        QuestionEntity question = questionDao.getQuestionById(questionId);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        UserAuthEntity token = UserDao.getAuthByAccessToken(authorizationToken);
        //if the access token is not there in the database, AuthorizationFailedException is thrown
        if (token == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //if the access token is valid but the user has not logged in, AuthorizationFailedException is thrown
        if (token.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }
        //else the user and the question of the answer is set and saved in the database
        //     answer.setQuestion(Question);
        UserEntity user = token.getUser();
        answer.setUser(user);
        Answer answerId = answerDao.createAnswer(answer);

        return answerId;
    }

    //Check if entered answer has existing UUID
    @Transactional(propagation = Propagation.REQUIRED)
    public Answer getAnswerForUuId(String answerUuId) throws AnswerNotFoundException {
        Answer answer = answerDao.getAnswerForUuId(answerUuId);
        if (answer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else {
            return answer;
        }
    }

    // Checks whether answer owner edits the answer and provides proper response to user
    @Transactional(propagation = Propagation.REQUIRED)
    public Answer isUserAnswerOwner(String answerUuId, UserAuthEntity authorizedUser, ActionType actionType) throws AnswerNotFoundException, AuthorizationFailedException {
        Answer answer = answerDao.getAnswerForUuId(answerUuId);

        if (answer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");

        } else if (
                ActionType.DELETE_ANSWER.equals(actionType)) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        } else {
            return answer;
        }
    }

    //An abstract interface for editing answer
    @Transactional(propagation = Propagation.REQUIRED)
    public Answer editAnswer(Answer answer) {
        return answerDao.editAnswer(answer);
    }


    //An abstract interface for deleting the answer
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(Answer answer) {
        answerDao.deleteAnswer(answer);
    }


    //An abstract interface for getting answer for question
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Answer> getAnswersForQuestion(String questionUuId) throws AnswerNotFoundException, InvalidQuestionException {

        QuestionEntity question = questionDao.getQuestionByQUuid(questionUuId);

        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered is invalid");
        }

        //throws an exception when there is no answer available for specific question uuid
        List<Answer> answerList = answerDao.getAnswersForQuestion(questionUuId);
        if (answerList == null) {
            throw new AnswerNotFoundException("OTHR-001", "No Answers available for the given question uuid");
        } else {
            return answerList;
        }
    }

}
