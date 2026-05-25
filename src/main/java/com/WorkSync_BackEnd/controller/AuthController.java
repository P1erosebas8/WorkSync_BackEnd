package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.dto.AuthRequest;
import com.WorkSync_BackEnd.domain.dto.AuthResponse;
import com.WorkSync_BackEnd.security.CustomUserDetailsService;
import com.WorkSync_BackEnd.security.JwtUtil;
import com.WorkSync_BackEnd.domain.dto.RegisterRequest;
import com.WorkSync_BackEnd.persistence.entity.enums.RolNombre;
import com.WorkSync_BackEnd.persistence.crud.RolCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Rol;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioCrudRepository usuarioCrudRepository;
    private final RolCrudRepository rolCrudRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getCorreoElectronico(),
                            authRequest.getContrasena()));
        } catch (BadCredentialsException e) {
            throw new Exception("Correo o contraseña incorrectos", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getCorreoElectronico());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioCrudRepository.findByCorreoElectronico(request.getCorreoElectronico()).isPresent()) {
            throw new RuntimeException("El correo ya está en uso");
        }

        Rol rol = rolCrudRepository.findByNombre(RolNombre.COLABORADOR)
                .orElseGet(() -> rolCrudRepository.save(Rol.builder()
                        .nombre(RolNombre.COLABORADOR)
                        .descripcion("Rol de COLABORADOR")
                        .build()));

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correoElectronico(request.getCorreoElectronico())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .estado(true)
                .rol(rol)
                .build();

        usuarioCrudRepository.save(usuario);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreoElectronico());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
