package com.digicompass.backend.application.services;

import com.digicompass.backend.infrastructure.EmailClientImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender  mailSender;
    @InjectMocks
    private EmailClientImpl emailService;

    private String toEmail;
    private String token;

    @BeforeEach
    void setUp() {
        toEmail = "user@example.com";
        token = "abc123";
    }


    @Test
    void sendResetLink_ThrowsIllegalArgumentException_WhenEmailIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendResetLink(null, "xyz"));
        assertThrows(IllegalArgumentException.class, () -> emailService.sendResetLink("", "xyz"));
    }

    @Test
    void sendResetLink_ThrowsIllegalArgumentException_WhenEmailIsNotCorrect() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendResetLink("abc", "xyz"));
    }

    @Test
    void sendResetLink_ThrowsIllegalArgumentException_WhenResetTokenIsNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> emailService.sendResetLink("test@gmail.com", null));
        assertThrows(IllegalArgumentException.class, () -> emailService.sendResetLink("test@gmail.com", ""));
    }

    @Test
    void sendResetLink_SendsEmailSuccessfully() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendResetLink(toEmail, token));

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendResetLink_ThrowsRuntimeException_WhenMailSenderFails() {
        doThrow(new MailException("SMTP error") {}).when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailService.sendResetLink(toEmail, token)
        );

        assertTrue(exception.getMessage().contains("Unable to send password reset email"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendResetLink_ThrowsRuntimeException_WhenUnexpectedErrorOccurs() {
        doAnswer(invocation -> { throw new NullPointerException("Unexpected failure"); })
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailService.sendResetLink(toEmail, token)
        );

        assertTrue(exception.getMessage().contains("unexpected error"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendResetLink_CreatesEmailWithCorrectBodyAndSubject() {
        final SimpleMailMessage[] capturedMessage = new SimpleMailMessage[1];

        doAnswer(invocation -> {
            capturedMessage[0] = invocation.getArgument(0);
            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendResetLink(toEmail, token);

        SimpleMailMessage msg = capturedMessage[0];
        assertNotNull(msg);
        assertEquals("Password Reset Request", msg.getSubject());
        assertEquals(toEmail, msg.getTo()[0]);
        assertTrue(msg.getText().contains("http://localhost:5173/reset-password?token=" + token));
        assertTrue(msg.getText().contains("This link will expire in 15 minutes."));
    }

}