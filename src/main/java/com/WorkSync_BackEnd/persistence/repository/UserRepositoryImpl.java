package com.WorkSync_BackEnd.persistence.repository;

import com.WorkSync_BackEnd.domain.model.User;
import com.WorkSync_BackEnd.domain.repository.UserRepository;
import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import com.WorkSync_BackEnd.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UsuarioCrudRepository usuarioCrudRepository;
    private final UserMapper userMapper;

    @Override
    public List<User> getAll() {
        return userMapper.toUsers(usuarioCrudRepository.findAll());
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return usuarioCrudRepository.findByCorreoElectronico(email).map(userMapper::toUser);
    }

    @Override
    public Optional<User> getById(Long userId) {
        return usuarioCrudRepository.findById(userId).map(userMapper::toUser);
    }

    @Override
    public User save(User user) {
        return userMapper.toUser(
                usuarioCrudRepository.save(userMapper.toUsuario(user))
        );
    }
}
