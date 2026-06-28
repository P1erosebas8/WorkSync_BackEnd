package com.WorkSync_BackEnd.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.refresh.token}")
    private String refreshToken;

    private final RestTemplate restTemplate;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getAccessToken() {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", refreshToken);
        map.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }

        throw new RuntimeException("Error al obtener el Access Token de Google");
    }

    public void sendEmail(String to, String subject, String bodyHtml) {
        try {
            String accessToken = getAccessToken();
            String gmailUrl = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";

            String emailContent = "To: " + to + "\r\n" +
                    "Subject: " + subject + "\r\n" +
                    "Content-Type: text/html; charset=utf-8\r\n\r\n" +
                    bodyHtml;

            String encodedEmail = Base64.getUrlEncoder().encodeToString(emailContent.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("raw", encodedEmail);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(gmailUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Error al enviar correo: " + response.getBody());
            }

        } catch (Exception e) {
            System.err.println("Error en EmailService: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
