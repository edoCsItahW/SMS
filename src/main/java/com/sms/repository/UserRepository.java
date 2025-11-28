package com.sms.repository;


import com.sms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;


@NoRepositoryBean
public interface UserRepository<T extends User> extends JpaRepository<T, Long> {

    Optional<T> findByName(String name);

    Optional<T> findByEmail(String email);

    Optional<T> findByPhone(String phone);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

}
