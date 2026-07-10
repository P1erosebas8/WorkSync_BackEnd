package com.WorkSync_BackEnd.domain.repository;

import com.WorkSync_BackEnd.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();
    Optional<User> getByEmail(String email);
    Optional<User> getById(Long userId);
    User save(User user);
}
