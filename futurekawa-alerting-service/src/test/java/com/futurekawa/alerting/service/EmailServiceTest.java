package com.futurekawa.alerting.service;

import com.futurekawa.alerting.dto.AlertRaisedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "from", "no-reply@futurekawa.local");
    }

    @Test
    void sendTemperatureAlert_buildsAndSendsExpectedEmail() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 13, 10, 0);
        AlertRaisedEvent event = AlertRaisedEvent.builder()
            .alertId(UUID.randomUUID())
            .type("TEMPERATURE_OUT_OF_RANGE")
            .containerReference("CT-001")
            .warehouseName("Sao Paulo")
            .countryCode("BR")
            .measuredTemperature(35.0f)
            .idealTemperature(22.0f)
            .tolerance(3.0f)
            .temperatureUnit("CELSIUS")
            .alertedAt(now)
            .recipient("ops@futurekawa.local")
            .build();

        emailService.sendTemperatureAlert(event);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).containsExactly("ops@futurekawa.local");
        assertThat(sent.getFrom()).isEqualTo("no-reply@futurekawa.local");
        assertThat(sent.getSubject()).contains("Sao Paulo");
        assertThat(sent.getText()).contains("CT-001", "35.0", "22.0");
    }
}
