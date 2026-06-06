package com.futurekawa.alerting.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Déclare les queues partagées avec backend-core. Les noms doivent rester
 * strictement identiques de part et d'autre (contrat de messagerie).
 */
@Configuration
public class RabbitConfig {

    /** Queue d'entrée : événements d'alerte à notifier. */
    public static final String ALERTS_QUEUE = "alerts";
    /** Queue de sortie : confirmations d'envoi renvoyées à backend-core. */
    public static final String ALERT_CONFIRMATIONS_QUEUE = "alert-confirmations";

    @Bean
    public Queue alertsQueue() {
        return new Queue(ALERTS_QUEUE, true);
    }

    @Bean
    public Queue alertConfirmationsQueue() {
        return new Queue(ALERT_CONFIRMATIONS_QUEUE, true);
    }
}
