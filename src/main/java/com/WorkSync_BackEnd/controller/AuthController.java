package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.dto.AuthRequest;
import com.WorkSync_BackEnd.domain.dto.AuthResponse;
import com.WorkSync_BackEnd.domain.dto.ForgotPasswordRequest;
import com.WorkSync_BackEnd.domain.dto.GoogleAuthRequest;
import com.WorkSync_BackEnd.domain.dto.RegisterRequest;
import com.WorkSync_BackEnd.domain.dto.ResetPasswordRequest;
import com.WorkSync_BackEnd.domain.dto.VerifyOtpRequest;
import com.WorkSync_BackEnd.domain.service.EmailService;
import com.WorkSync_BackEnd.security.CustomUserDetailsService;
import com.WorkSync_BackEnd.persistence.entity.enums.RolNombre;
import com.WorkSync_BackEnd.persistence.crud.RolCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import com.WorkSync_BackEnd.security.JwtUtil;
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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.springframework.web.client.RestTemplate;

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
    private final EmailService emailService;
    private final RestTemplate restTemplate;

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
        Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(authRequest.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getEstado() != null && !usuario.getEstado()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse("Error: La cuenta no ha sido verificada. Revisa tu correo."));
        }

        final String jwt = jwtUtil.generateToken(userDetails, usuario);

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

        String otp = String.format("%06d", new Random().nextInt(999999));

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .correoElectronico(request.getCorreoElectronico())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .estado(false)
                .otpCode(otp)
                .otpExpiration(LocalDateTime.now().plusMinutes(15))
                .rol(rol)
                .build();

        usuarioCrudRepository.save(usuario);

        String subject = "Verifica tu cuenta - WorkSync";
        String body = "<h3>Bienvenido a WorkSync</h3>"
                + "<p>Hola " + request.getNombre() + ",</p>"
                + "<p>Para activar tu cuenta, ingresa el siguiente código de verificación:</p>"
                + "<h2>" + otp + "</h2>"
                + "<p>Este código expirará en 15 minutos.</p>";

        emailService.sendEmail(usuario.getCorreoElectronico(), subject, body);

        // Devolvemos un 200 con un mensaje de éxito
        return ResponseEntity.ok(new AuthResponse("Usuario registrado. Verifica tu correo electrónico."));
    }

    @PostMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(@Valid @RequestBody VerifyOtpRequest request) {
        Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(request.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getOtpCode() == null || !usuario.getOtpCode().equals(request.getOtpCode())) {
            return ResponseEntity.badRequest().body("Código OTP inválido");
        }

        if (usuario.getOtpExpiration() == null || usuario.getOtpExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("El código OTP ha expirado");
        }

        usuario.setEstado(true);
        usuario.setOtpCode(null);
        usuario.setOtpExpiration(null);
        usuarioCrudRepository.save(usuario);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreoElectronico());
        final String jwt = jwtUtil.generateToken(userDetails, usuario);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(request.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese correo"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        usuario.setOtpCode(otp);
        usuario.setOtpExpiration(LocalDateTime.now().plusMinutes(15));
        usuarioCrudRepository.save(usuario);

        String subject = "Código de Recuperación de Contraseña - WorkSync";
        String body = "<h3>Recuperación de Contraseña</h3>"
                + "<p>Tu código de verificación es: <strong>" + otp + "</strong></p>"
                + "<p>Este código expirará en 15 minutos.</p>";

        emailService.sendEmail(usuario.getCorreoElectronico(), subject, body);

        return ResponseEntity.ok("Código OTP enviado al correo.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(request.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getOtpCode() == null || !usuario.getOtpCode().equals(request.getOtpCode())) {
            return ResponseEntity.badRequest().body("Código OTP inválido");
        }

        if (usuario.getOtpExpiration() == null || usuario.getOtpExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("El código OTP ha expirado");
        }

        return ResponseEntity.ok("Código verificado correctamente");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(request.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getOtpCode() == null || !usuario.getOtpCode().equals(request.getOtpCode())) {
            return ResponseEntity.badRequest().body("Código OTP inválido");
        }

        if (usuario.getOtpExpiration() == null || usuario.getOtpExpiration().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("El código OTP ha expirado");
        }

        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuario.setOtpCode(null);
        usuario.setOtpExpiration(null);
        usuarioCrudRepository.save(usuario);

        return ResponseEntity.ok("Contraseña restablecida correctamente");
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleAuthRequest request) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + request.getToken();
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = restTemplate.getForObject(url, Map.class);
            
            if (payload == null || !payload.containsKey("email")) {
                throw new RuntimeException("Token de Google inválido");
            }

            String email = (String) payload.get("email");
            String nombre = (String) payload.get("given_name");
            if (payload.containsKey("family_name")) {
                nombre += " " + payload.get("family_name");
            }

            Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(email).orElse(null);

            if (usuario == null) {
                // Crear usuario si no existe
                Rol rol = rolCrudRepository.findByNombre(RolNombre.COLABORADOR)
                        .orElseGet(() -> rolCrudRepository.save(Rol.builder()
                                .nombre(RolNombre.COLABORADOR)
                                .descripcion("Rol de COLABORADOR")
                                .build()));

                usuario = Usuario.builder()
                        .nombre(nombre)
                        .correoElectronico(email)
                        .contrasena(passwordEncoder.encode(UUID.randomUUID().toString())) // Contraseña aleatoria
                        .estado(true) // Activo porque viene de Google
                        .rol(rol)
                        .build();
                
                usuario = usuarioCrudRepository.save(usuario);
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreoElectronico());
            final String jwt = jwtUtil.generateToken(userDetails, usuario);

            return ResponseEntity.ok(new AuthResponse(jwt));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Error al validar el token de Google: " + e.getMessage()));
        }
    }
}
