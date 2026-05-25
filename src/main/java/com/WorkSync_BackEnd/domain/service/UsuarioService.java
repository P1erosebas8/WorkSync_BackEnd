package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioCrudRepository usuarioCrudRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listar() {
        return usuarioCrudRepository.findAll();
    }

    public Usuario guardar(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuario.setEstado(true);
        return usuarioCrudRepository.save(usuario);
    }
}
