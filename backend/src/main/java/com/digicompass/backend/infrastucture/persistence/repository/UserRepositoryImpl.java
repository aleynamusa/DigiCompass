package com.digicompass.backend.infrastucture.persistence.repository;

import com.digicompass.backend.domain.entity.UserEntity;
import com.digicompass.backend.domain.repositories.UserJpaRepository;

import com.digicompass.backend.infrastucture.persistence.repository.interfaces.UserInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionSystemException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserInterface {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserEntity save(UserEntity user) {
        log.info("[INFRASTRUCTURE] Signing up user with username={} and email={}", user.getUsername(), user.getEmail());
        try {
            UserEntity saved = jpaRepository.save(user);
            log.debug("[INFRASTRUCTURE] User saved successfully with id={}", saved.getId());
            return saved;
        } catch (DataIntegrityViolationException e) {
            log.error("[INFRASTRUCTURE] Data integrity violation while signing up user {}", user.getUsername(), e);
        } catch (TransactionSystemException e) {
            log.error("[INFRASTRUCTURE] Transaction error while signing up user {}", user.getUsername(), e);
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while signing up user {}", user.getUsername(), e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error while signing up user {}", user.getUsername(), e);
        }
        return null;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        log.info("[INFRASTRUCTURE] Fetching user by id={}", id);
        try {
            Optional<UserEntity> user = jpaRepository.findById(id);
            if (user.isEmpty()) {
                log.warn("[INFRASTRUCTURE] User not found with id={}", id);
            } else {
                log.debug("[INFRASTRUCTURE] Found user with id={}", id);
            }
            return user;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching user id={}", id, e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching user id={}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<UserEntity> findAll() {
        log.info("[INFRASTRUCTURE] Fetching all users");
        try {
            List<UserEntity> users = jpaRepository.findAll();
            log.debug("[INFRASTRUCTURE] Found {} users", users.size());
            return users;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching all users", e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching all users", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void delete(UserEntity user) {
        log.info("[INFRASTRUCTURE] Deleting user with id={} and username={}", user.getId(), user.getUsername());
        try {
            jpaRepository.delete(user);
            log.debug("[INFRASTRUCTURE] User deleted successfully id={}", user.getId());
        } catch (EmptyResultDataAccessException e) {
            log.warn("[INFRASTRUCTURE] Attempted to delete non-existent user id={}", user.getId());
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while deleting user id={}", user.getId(), e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error deleting user id={}", user.getId(), e);
        }
    }

    @Override
    public UserEntity findByUsername(String username) {
        log.info("[INFRASTRUCTURE] Fetching user by username={}", username);
        try {
            UserEntity entity = jpaRepository.findUserDocumentByUsername(username);
            if (entity == null) {
                log.warn("[INFRASTRUCTURE] No user found with username={}", username);
            } else {
                log.debug("[INFRASTRUCTURE] Found user with username={}", username);
            }
            return entity;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching user username={}", username, e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching user username={}", username, e);
        }
        return null;
    }

    @Override
    public UserEntity findByEmail(String email) {
        log.info("[INFRASTRUCTURE] Fetching user by email={}", email);
        try {
            UserEntity entity = jpaRepository.findByEmail(email);
            if (entity == null) {
                log.warn("[INFRASTRUCTURE] No user found with email={}", email);
            } else {
                log.debug("[INFRASTRUCTURE] Found user with email={}", email);
            }
            return entity;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching user email={}", email, e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching user email={}", email, e);
        }
        return null;
    }

    @Override
    public List<String> findAllEmails() {
        log.info("[INFRASTRUCTURE] Fetching all user emails");
        try {
            List<String> emails = jpaRepository.findAllEmails();
            log.debug("[INFRASTRUCTURE] Found {} user emails", emails.size());
            return emails;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching all user emails", e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching all user emails", e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> findAllUsernames() {
        log.info("[INFRASTRUCTURE] Fetching all usernames");
        try {
            List<String> usernames = jpaRepository.findAllUsernames();
            log.debug("[INFRASTRUCTURE] Found {} usernames", usernames.size());
            return usernames;
        } catch (DataAccessResourceFailureException e) {
            log.error("[INFRASTRUCTURE] Database connection failed while fetching all usernames", e);
        } catch (Exception e) {
            log.error("[INFRASTRUCTURE] Unexpected error fetching all usernames", e);
        }
        return Collections.emptyList();
    }
}
