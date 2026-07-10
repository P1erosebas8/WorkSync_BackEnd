package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.dto.AuthRequest;
import com.WorkSync_BackEnd.domain.dto.AuthResponse;
import com.WorkSync_BackEnd.domain.dto.ForgotPasswordRequest;
import com.WorkSync_BackEnd.domain.dto.GoogleAuthRequest;
import com.WorkSync_BackEnd.domain.dto.RegisterRequest;
import com.WorkSync_BackEnd.domain.dto.ResetPasswordRequest;
import com.WorkSync_BackEnd.domain.dto.VerifyOtpRequest;
import com.WorkSync_BackEnd.persistence.crud.RolCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Rol;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import com.WorkSync_BackEnd.persistence.entity.enums.RolNombre;
import com.WorkSync_BackEnd.security.CustomUserDetailsService;
import com.WorkSync_BackEnd.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioCrudRepository usuarioCrudRepository;
    private final RolCrudRepository rolCrudRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    public AuthResponse login(AuthRequest authRequest) throws Exception {
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
            throw new RuntimeException("Error: La cuenta no ha sido verificada. Revisa tu correo.");
        }

        final String jwt = jwtUtil.generateToken(userDetails, usuario);
        return new AuthResponse(jwt);
    }

    public AuthResponse register(RegisterRequest request) {
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

        String subject = "WorkSync - Verifica tu cuenta";
        String body = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #333; border-radius: 8px; overflow: hidden; background-color: #1a1a1a; color: #fff;\">" +
                "<div style=\"background-color: #f59e0b; padding: 20px; text-align: center;\">" +
                "<h2 style=\"color: #000; margin: 0; font-size: 24px;\">WorkSync</h2>" +
                "</div>" +
                "<div style=\"padding: 30px;\">" +
                "<h3 style=\"color: #f59e0b; margin-top: 0;\">Bienvenido, " + request.getNombre() + "</h3>" +
                "<p style=\"color: #ccc; line-height: 1.6;\">Para activar tu cuenta, ingresa el siguiente código de verificación:</p>" +
                "<div style=\"background-color: #242424; border: 1px dashed #f59e0b; padding: 15px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #f59e0b; margin: 20px 0; border-radius: 8px;\">" + otp + "</div>" +
                "<p style=\"color: #888; font-size: 12px; margin-bottom: 0;\">Este código expirará en 15 minutos.</p>" +
                "</div>" +
                "</div>";

        emailService.sendEmail(usuario.getCorreoElectronico(), subject, body);

        return new AuthResponse("Usuario registrado. Verifica tu correo electrónico.");
    }

    public AuthResponse verifyAccount(VerifyOtpRequest request) {
        Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(request.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        validateOtp(usuario, request.getOtpCode());

        usuario.setEstado(true);
        usuario.setOtpCode(null);
        usuario.setOtpExpiration(null);
        usuarioCrudRepository.save(usuario);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreoElectronico());
        final String jwt = jwtUtil.generateToken(userDetails, usuario);

        return new AuthResponse(jwt);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(request.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese correo"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        usuario.setOtpCode(otp);
        usuario.setOtpExpiration(LocalDateTime.now().plusMinutes(15));
        usuarioCrudRepository.save(usuario);

        String subject = "WorkSync - Recuperación de Contraseña";
        String body = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #333; border-radius: 8px; overflow: hidden; background-color: #1a1a1a; color: #fff;\">" +
                "<div style=\"background-color: #dc2626; padding: 20px; text-align: center;\">" +
                "<h2 style=\"color: #fff; margin: 0; font-size: 24px;\">WorkSync</h2>" +
                "</div>" +
                "<div style=\"padding: 30px;\">" +
                "<h3 style=\"color: #ef4444; margin-top: 0;\">Recuperación de Contraseña</h3>" +
                "<p style=\"color: #ccc; line-height: 1.6;\">Has solicitado restablecer tu contraseña. Tu código de verificación es:</p>" +
                "<div style=\"background-color: #242424; border: 1px dashed #ef4444; padding: 15px; text-align: center; font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #ef4444; margin: 20px 0; border-radius: 8px;\">" + otp + "</div>" +
                "<p style=\"color: #888; font-size: 12px; margin-bottom: 0;\">Este código expirará en 15 minutos.</p>" +
                "</div>" +
                "</div>";

        emailService.sendEmail(usuario.getCorreoElectronico(), subject, body);
    }

    public void verifyOtp(VerifyOtpRequest request) {
        Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(request.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        validateOtp(usuario, request.getOtpCode());
    }

    public void resetPassword(ResetPasswordRequest request) {
        Usuario usuario = usuarioCrudRepository.findByCorreoElectronico(request.getCorreoElectronico())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        validateOtp(usuario, request.getOtpCode());

        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuario.setOtpCode(null);
        usuario.setOtpExpiration(null);
        usuarioCrudRepository.save(usuario);
    }

    public AuthResponse googleLogin(GoogleAuthRequest request) {
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
                Rol rol = rolCrudRepository.findByNombre(RolNombre.COLABORADOR)
                        .orElseGet(() -> rolCrudRepository.save(Rol.builder()
                                .nombre(RolNombre.COLABORADOR)
                                .descripcion("Rol de COLABORADOR")
                                .build()));

                usuario = Usuario.builder()
                        .nombre(nombre)
                        .correoElectronico(email)
                        .contrasena(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .estado(true)
                        .rol(rol)
                        .build();

                usuario = usuarioCrudRepository.save(usuario);
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreoElectronico());
            final String jwt = jwtUtil.generateToken(userDetails, usuario);

            return new AuthResponse(jwt);

        } catch (Exception e) {
            throw new RuntimeException("Error al validar el token de Google: " + e.getMessage());
        }
    }

    private void validateOtp(Usuario usuario, String otp) {
        if (usuario.getOtpCode() == null || !usuario.getOtpCode().equals(otp)) {
            throw new RuntimeException("Código OTP inválido");
        }
        if (usuario.getOtpExpiration() == null || usuario.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El código OTP ha expirado");
        }
    }
}
