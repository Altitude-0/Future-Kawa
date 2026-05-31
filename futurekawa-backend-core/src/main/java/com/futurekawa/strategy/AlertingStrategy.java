package com.futurekawa.strategy;

import com.futurekawa.entity.Alert;
import com.futurekawa.entity.Container;

import java.util.List;

public interface AlertingStrategy {

    List<Alert> evaluateAlerts(Container container);

    Float getIdealTemperature();

    Float getIdealHumidity();

    Float getTemperatureTolerance();

    Float getHumidityTolerance();
}
