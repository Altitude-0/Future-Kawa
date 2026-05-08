# FutureKawa - Solution IoT de Gestion des Stocks de Café

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📋 Vue d'Ensemble

FutureKawa est une **solution complète et réutilisable** de suivi des stocks et des conditions de stockage pour la caféiculture. 

**Architecture** : Backend Core générique (produit) + Applications par pays (consommateurs) + Backend HQ (agrégation)

**Cas d'usage** : Surveiller température/humidité des entrepôts, détecter les risques de qualité, tracer les lots de café vert.

---

## 🏗️ Architecture Produit

Le projet suit un modèle **produit réutilisable**:

```
futurekawa-backend-core/           ← Produit générique (Maven JAR)
    ├─ Entities, Services, Controllers, MQTT, Email
    └─ Zéro mention de pays

    ↓ Consommé par

futurekawa-app-{country}/         ← Applications pays
    ├─ Dépend du core
    ├─ Configuration locale (seuils, MQTT broker)
    ├─ MQTT local + PostgreSQL local
    └─ Docker Compose propre

futurekawa-backend-hq/            ← Siège
    ├─ Agrégation des apps pays
    ├─ Dashboard consolidé
    └─ Frontend React
```

📚 **Voir [ARCHITECTURE.md](ARCHITECTURE.md) pour les détails complets**.

---

## 🚀 Quick Start

### Prérequis

- Java 17+
- Maven 3.8+
- Docker + Docker Compose
- PostgreSQL 15+ (local ou container)

### 1. Cloner et Initialiser

```bash
cd /Users/mohameddjebali/Desktop/mspr-2

# Initialiser Git (si pas déjà fait)
git init
git add .
git commit -m "Initial commit: FutureKawa Backend Core"
```

### 2. Construire le Backend Core

```bash
cd futurekawa-backend-core

# Compiler et tester
mvn clean install

# Résultat: JAR disponible dans ~/.m2 pour consommation
```

### 3. Lancer une App Pays (Exemple: Brésil)

```bash
cd ../futurekawa-app-brazil

# Démarrer les services (MQTT + PostgreSQL + Backend)
docker-compose up --build

# Vérifier la santé de l'API
curl http://localhost:8080/api/v1/health

# Voir les logs
docker-compose logs -f backend-brazil
```

### 4. Lancer le Backend HQ (Siège)

```bash
cd ../futurekawa-backend-hq

# Démarrer HQ + Frontend React
docker-compose up --build

# Frontend: http://localhost:3000
# API HQ: http://localhost:8080/api/v1/...
```

---

## 📁 Structure du Projet

```
futurekawa/
├── futurekawa-backend-core/          ← Produit (à maintenir en priorité)
│   ├── pom.xml
│   ├── src/main/java/com/futurekawa/
│   │   ├── entity/                   (Entities génériques)
│   │   ├── repository/               (JPA Repositories)
│   │   ├── service/                  (Services métier)
│   │   ├── controller/               (API REST)
│   │   ├── mqtt/                     (MQTT Consumer/Publisher)
│   │   └── config/                   (Config Spring + Profiles)
│   └── src/main/resources/
│       ├── application.yaml          (Config par défaut)
│       └── db/migration/             (Flyway migrations)
│
├── futurekawa-app-brazil/            ← App Brésil (consomme core)
├── futurekawa-app-ecuador/           ← App Équateur (consomme core)
├── futurekawa-app-colombia/          ← App Colombie (consomme core)
│
├── futurekawa-backend-hq/            ← Backend Siège (agrégation)
│
├── ARCHITECTURE.md                   ← Détails architecture
├── informations/                     ← Cahier des charges + docs métier
└── README.md                         ← Ce fichier
```

---

## 🔑 Concepts Clés

### Backend Core

**Rôle**: Produit générique réutilisable
- Entités: Stock, Warehouse, Measurement, Alert
- Services: StockService, AlertService, MeasurementService, EmailService
- Stratégies d'alerte (extensibles par profils Spring)
- Configuration par properties (@Value injection)

**Profils Spring**:
- `br`: Brésil (29°C, 55% humidité)
- `ec`: Équateur (31°C, 60% humidité)
- `co`: Colombie (26°C, 80% humidité)

### Applications Pays

**Rôle**: Consomment le core, ajoutent le contexte local
- Dépendent du core via Maven
- Surcharge config (application-{country}.yaml)
- Lancement: docker-compose local (MQTT + PostgreSQL)
- API: héritée du core (générique)

### Backend HQ

**Rôle**: Agrégation + console centrale
- Requête les APIs des apps pays
- Consolide les données
- Expose API unifiée pour le siège
- Frontend React pour le dashboard

---

## 📡 Flux de Données

```
IoT (ESP32 + DHT22)
        ↓ MQTT
MQTT Broker local (Mosquitto)
        ↓
Backend Core (App Pays)
        ↓
PostgreSQL local
        ↓ (Alertes)
Email Service
        ↓ (API)
Backend HQ
        ↓
Frontend React (Dashboard)
```

---

## 🧪 Tests & CI/CD

### Tests Backend Core

```bash
cd futurekawa-backend-core

# Tests unitaires + intégration
mvn test

# Avec coverage
mvn test jacoco:report
```

### Pipeline CI/CD (Jenkins)

À implémenter: Jenkinsfile pour build, test, package, déploiement.

---

## 📝 Configuration par Pays

Chaque app pays surcharge les seuils via properties:

```yaml
# application-br.yaml (Brésil)
futurekawa:
  temperature-ideal: 29.0
  humidity-ideal: 55.0
  temperature-tolerance: 3.0
  humidity-tolerance: 2.0
  alert-old-lot-days: 365
  email-recipient: ops@brazil.futurekawa.com
```

---

## 🔗 Endpoints API

### App Pays (générique)

```
GET    /api/v1/stocks
POST   /api/v1/stocks
GET    /api/v1/stocks/{id}
GET    /api/v1/stocks/{id}/measurements
GET    /api/v1/alerts
PUT    /api/v1/alerts/{id}/mark-sent
GET    /api/v1/health
GET    /api/v1/summary
```

### Backend HQ (agrégation)

```
GET    /api/v1/countries
GET    /api/v1/countries/{country}/stocks
GET    /api/v1/all-stocks
GET    /api/v1/alerts
GET    /api/v1/dashboard
GET    /api/v1/dashboard/country/{country}
```

---

## 📚 Ressources

- **[ARCHITECTURE.md](ARCHITECTURE.md)**: Détails complets de l'architecture
- **[informations/](informations/)**: Cahier des charges FutureKawa
- **[Backend Core](futurekawa-backend-core/)**: Code produit

---

## 🛠️ Développement

### Ajouter une Nouvelle Entité au Core

1. Créer la classe entité dans `entity/`
2. Créer le repository dans `repository/`
3. Créer le service dans `service/`
4. Créer le controller dans `controller/`
5. Migration Flyway: `src/main/resources/db/migration/V{N}__*.sql`

### Surcharger pour un Pays

Dans l'app pays, surcharger via:
1. Configuration (properties YAML)
2. Composants Spring (@ConditionalOnProperty)
3. Extension de services (extends ServiceClass)

---

## 📄 Licences & Crédits

- **Stack**: Spring Boot 3.2, PostgreSQL, MQTT, React
- **Cas d'usage**: FutureKawa - Caféiculture & Logistique

---

## 📧 Support

Pour des questions sur l'architecture ou le code, consulter l'équipe de développement.

**Prochaines phases**:
- Phase 2: Automatisation des équipements (chauffage, humidification, aération)
- Scalabilité multi-cloud
- Authentification (JWT/OAuth2)
