package com.sms.service;

import com.sms.entity.User;
import com.sms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public abstract class UserService<T extends User, R extends UserRepository<T>> {

    @Autowired
    protected R repository;

    public List<T> findAll() {
        return repository.findAll();
    }

    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<T> findByName(String name) {
        return repository.findByName(name);
    }

    public Optional<T> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public T save(T user) {
        return repository.save(user);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public boolean isUsernameAvailable(String username, Long currentUserId) {
        Optional<T> existingUser = findByName(username);
        return existingUser.isEmpty() || existingUser.get().getId().equals(currentUserId);
    }

}
