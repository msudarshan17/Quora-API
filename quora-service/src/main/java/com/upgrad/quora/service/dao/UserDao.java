package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;
import com.upgrad.quora.service.entity.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


// This is User dao class which will fetch data from DB and send object to business service class .
@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUserByUserName(final String username) {

        try {
            return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("userName", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (Exception nre) {
            return null;
        }
    }


    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    //Function to get the complete user details based on UUID
    public UserEntity getUser(String userUuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid)
                    .getSingleResult();
        } catch (Exception nre) {
            return null;
        }
    }

    public UserAuthEntity getAuthByAccessToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("authByAccessToken", UserAuthEntity.class).setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (Exception nre) {
            return null;
        }
    }

    public UserAuthEntity signOut(UserAuthEntity userAuthEntity) {
        entityManager.merge(userAuthEntity);
        return userAuthEntity;
    }

    //Function to delete the user by Admin
    public UserEntity deleteUserbyAdmin(UserEntity userentity) {
        entityManager.remove(userentity);
        return userentity;
    }

    public void deleteUser(UserEntity userEntity, UserAuthEntity userAuthEntity) {
        entityManager.remove(userEntity);
        entityManager.remove(userAuthEntity);
    }

    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }

}
