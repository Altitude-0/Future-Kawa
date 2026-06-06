package com.futurekawa.alerting.service;

import com.futurekawa.alerting.dto.AlertRaisedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envoie les mails d'alerte via SMTP (synchrone). En local, le SMTP cible
 * MailHog (voir docker-compose) afin de tester sans vrai compte mail.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${futurekawa.mail.from:no-reply@futurekawa.local}")
    private String from;

    /**
     * Construit et envoie le mail d'alerte de température.
     *
     * @throws org.springframework.mail.MailException si l'envoi SMTP échoue
     *         (propagée pour que le message ne soit pas confirmé).
     */
    public void sendTemperatureAlert(AlertRaisedEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.getRecipient());
        message.setSubject(buildSubject(event));
        message.setText(buildBody(event));

        mailSender.send(message);
        log.info("Temperature alert email sent to {} for container {}",
            event.getRecipient(), event.getContainerReference());
    }

    private String buildSubject(AlertRaisedEvent event) {
        return String.format("[FutureKawa] Alerte temperature - Entrepot %s", event.getWarehouseName());
    }

    private String buildBody(AlertRaisedEvent event) {
        String unit = event.getTemperatureUnit() == null ? "" : " " + event.getTemperatureUnit();
        return String.format(
            "Une alerte de temperature a ete declenchee.%n%n"
                + "Conteneur   : %s%n"
                + "Entrepot    : %s%n"
                + "Pays        : %s%n"
                + "Temperature mesuree : %s%s%n"
                + "Temperature ideale  : %s%s (tolerance +/- %s)%n"
                + "Date de l'alerte    : %s%n",
            event.getContainerReference(),
            event.getWarehouseName(),
            event.getCountryCode(),
            event.getMeasuredTemperature(), unit,
            event.getIdealTemperature(), unit,
            event.getTolerance(),
            event.getAlertedAt());
    }
}
