package com.futurekawa.strategy;

import com.futurekawa.entity.Alert;
import com.futurekawa.entity.Stock;

import java.util.List;

/**
 * Interface for implementing country/region-specific alerting strategies.
 * Each implementation can define custom rules for evaluating alerts.
 */
public interface AlertingStrategy {

    /**
     * Evaluate alerts for a given stock based on its measurements.
     *
     * @param stock The stock to evaluate
     * @return List of alerts that should be triggered
     */
    List<Alert> evaluateAlerts(Stock stock);

    /**
     * Get the ideal temperature for this region (in Celsius)
     */
    Float getIdealTemperature();

    /**
     * Get the ideal humidity for this region (in percentage)
     */
    Float getIdealHumidity();

    /**
     * Get the temperature tolerance (±)
     */
    Float getTemperanceTolerance();

    /**
     * Get the humidity tolerance (±)
     */
    Float getHumidityTolerance();
}
