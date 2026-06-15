# Architecture globale

## Modèle

- Backend Core (JAR Maven réutilisable)
- Instances pays (Brazil, etc.)
- Backend HQ (agrégation)

## Flux

IoT → MQTT → Backend Core → Database → HQ