# FutureKawa - Solution IoT de Gestion des Stocks de Café

[![GitHub Actions](https://github.com/Altitude-0/Future-Kawa/actions/workflows/ci.yml/badge.svg)](https://github.com/Altitude-0/Future-Kawa/actions)
[![GHCR](https://img.shields.io/badge/registry-GHCR-blue)](https://ghcr.io/altitude-0/futurekawa-product)

## 📋 Vue d'Ensemble

FutureKawa est une **solution complète et générique** de suivi des stocks et des conditions de stockage pour la caféiculture.

**Modèle architectural** : Un **Backend Core réutilisable** (produit JAR Maven) + **Applications pays** (consommateurs) qui le dépendent + **Backend HQ** (agrégation centrale)

**Cas d'usage** : Chaque pays (Brésil, Équateur, Colombie) déploie son instance locale du backend-core avec sa configuration, surveille température/humidité, détecte risques qualité, trace lots de café vert.

---

## 🏗️ Architecture Monorepo

Le projet suit un modèle **monorepo avec pom parent** :

```
Future-Kawa/                                    ← Repo organisation
├── pom.xml                                     ← POM parent (versions centralisées)
│   └── modules: [futurekawa-backend-core, ...]
│
├── futurekawa-backend-core/                    ← Service 1: Produit générique
│   ├── pom.xml (hérite du parent)
│   ├── src/main/java/com/futurekawa/
│   │   ├── entity/                             (Stock, Warehouse, Measurement, Alert)
│   │   ├── service/                            (Métier: alertes, MQTT, email)
│   │   ├── controller/                         (REST API)
│   │   ├── config/                             (Spring Boot + Profiles pays)
│   │   └── security/                           (JWT)
│   ├── Dockerfile                              (Multi-stage: Maven builder + runtime)
│   └── src/main/resources/
│       ├── application.yaml                    (Config par défaut)
│       └── db/migration/                       (Flyway V1, V2, ...)
│
├── [futurekawa-frontend-web/]                  ← Service 2: Frontend React (futur)
├── [futurekawa-iot/]                           ← Service 3: Code microcontrôleur (futur)
│
├── .github/workflows/
│   └── ci.yml                                  (GitHub Actions: build + test + push GHCR)
│
├── ARCHITECTURE.md                             ← Détails techniques
├── informations/                               ← Cahier des charges
└── README.md                                   ← Ce fichier
```

**Avantage du modèle** : Versions cohérentes entre services, build unifié, scalable pour futurs microservices.

---

## 🚀 Quick Start

### Prérequis

- **Java 17+**
- **Maven 3.9+**
- **Docker + Docker Compose** (pour lancer les services)
- **PostgreSQL 15+** (peut être via container Docker)

### 1. Cloner le Repo

```bash
git clone https://github.com/Altitude-0/Future-Kawa.git
cd Future-Kawa
```

### 2. Build & Tests (Maven)

```bash
# Build parent + tous les modules
mvn clean test

# Output: Tous les modules compilés, 27+ tests passent
```

### 3. Build Docker (Image GHCR)

```bash
# Build l'image backend-core
docker build -f futurekawa-backend-core/Dockerfile -t futurekawa-product:latest .

# Test local
docker run -p 8080:8080 futurekawa-product:latest
curl http://localhost:8080/actuator/health
```

### 4. Lancer avec Docker Compose

```bash
cd futurekawa-backend-core

# Démarrer: PostgreSQL + Mosquitto + Backend
docker-compose up -d --build

# Vérifier
curl http://localhost:8080/swagger-ui/index.html

# Logs
docker-compose logs -f backend
```

---

## 📁 Structure des Fichiers

```
├── pom.xml                              ← Parent POM (version, modules, dépendances centralisées)
├── futurekawa-backend-core/
│   ├── pom.xml                          ← Module: Backend Core (hérite parent)
│   ├── Dockerfile                       ← Multi-stage builder (Maven → JAR → runtime)
│   ├── docker-compose.yaml              ← PostgreSQL + Mosquitto + Backend
│   ├── src/main/java/com/futurekawa/
│   │   ├── entity/                      (5 entités: User, Stock, Warehouse, Measurement, Alert)
│   │   ├── dto/                         (DTOs avec structure unilatérale)
│   │   ├── repository/                  (JPA repositories)
│   │   ├── service/                     (Services métier)
│   │   ├── controller/                  (REST endpoints)
│   │   ├── security/                    (JWT filter + config)
│   │   ├── mqtt/                        (MQTT config et listeners)
│   │   ├── strategy/                    (AlertingStrategy)
│   │   └── config/                      (FuturkawaProperties, SecurityConfig)
│   ├── src/main/resources/
│   │   ├── application.yaml             (Configuration principale)
│   │   └── db/migration/                (Flyway V1__init_core_schema.sql, V2__add_users_table.sql)
│   ├── seed_data.sql                    (7 stocks, 11 mesures, 3 alertes de test)
│   └── clean_data.sql                   (Script de nettoyage)
│
├── .github/workflows/
│   └── ci.yml                           (GitHub Actions: build + test + push ghcr.io)
│
├── CLAUDE.md                            (Directives projet, journal de bord)
├── ARCHITECTURE.md                      (Architecture complète)
├── README.md                            (Ce fichier)
└── informations/                        (Cahier des charges client)
```

---

## 🔐 Sécurité & Configuration

### JWT Authentication

```bash
# Enregistrer un utilisateur (retourne JWT token)
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'

# Utiliser le token pour les appels sécurisés
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/v1/stocks
```

### Configuration par Pays

Le fichier `application.yaml` expose des variables externalisées pour chaque pays :

```yaml
futurekawa:
  temperature-ideal: 29.0        # Brésil
  humidity-ideal: 55.0
  temperature-tolerance: 3.0
  humidity-tolerance: 2.0
  alert-old-lot-days: 365
```

À l'exécution (Docker ou local), surcharger via env vars :

```bash
docker run -e FUTUREKAWA_TEMPERATURE_IDEAL=31.0 \
           -e FUTUREKAWA_HUMIDITY_IDEAL=60.0 \
           futurekawa-product:latest
```

---

## 🧪 Tests & CI/CD

### Tests Locaux

```bash
# Tests unitaires + intégration
mvn clean test

# Résultat: 27 tests passent (UserServiceTest, StockServiceTest, MeasurementServiceTest)
```

### Pipeline CI/CD (GitHub Actions)

Chaque `push` sur `main` ou `pull_request` déclenche `.github/workflows/ci.yml`:

**Étapes**:
1. ✅ **Build & Test**: Maven clean test (JUnit 5 + Mockito)
2. ✅ **Build Docker**: Dockerfile multi-stage (Maven builder → JAR → runtime)
3. ✅ **Push GHCR**: Image taggée `ghcr.io/altitude-0/futurekawa-product:[sha]`

**Visualiser**:
- GitHub Actions: https://github.com/Altitude-0/Future-Kawa/actions
- Images: https://ghcr.io/altitude-0/futurekawa-product

---

## 📡 Endpoints API Principaux

```bash
# Auth
POST   /api/v1/auth/register               ← Créer utilisateur + obtenir JWT
POST   /api/v1/auth/login                  ← Login (futur)

# Stocks
GET    /api/v1/stocks                      ← Lister tous les lots
POST   /api/v1/stocks                      ← Créer un lot
GET    /api/v1/stocks/{id}                 ← Détail lot
PUT    /api/v1/stocks/{id}                 ← Remplacer lot
PATCH  /api/v1/stocks/{id}                 ← Modifier lot partiellement

# Mesures (Température/Humidité)
GET    /api/v1/measurements                ← Lister mesures
POST   /api/v1/measurements                ← Enregistrer mesure IoT
GET    /api/v1/measurements/stock/{id}     ← Historique mesures d'un lot

# Alertes
GET    /api/v1/alerts                      ← Lister alertes
GET    /api/v1/alerts/{id}                 ← Détail alerte
PATCH  /api/v1/alerts/{id}/mark-sent       ← Marquer alerte envoyée

# Health & Info
GET    /actuator/health                    ← Health check
GET    /swagger-ui/index.html              ← Documentation interactive (Swagger)
```

---

## 🔧 Développement & Extension

### Ajouter une Nouvelle Entité

1. Créer entité dans `entity/`:
   ```java
   @Entity
   public class MyEntity { ... }
   ```

2. Créer repository dans `repository/`:
   ```java
   public interface MyEntityRepository extends JpaRepository<MyEntity, UUID> { }
   ```

3. Créer service dans `service/`:
   ```java
   @Service
   public class MyEntityService { ... }
   ```

4. Créer controller dans `controller/`:
   ```java
   @RestController
   @RequestMapping("/api/v1/myentity")
   public class MyEntityController { ... }
   ```

5. Ajouter migration Flyway `src/main/resources/db/migration/V3__add_myentity_table.sql`

### Modifier pour un Pays Spécifique

**Approche 1**: Surcharger config via `application-{country}.yaml`

```yaml
# application-br.yaml
futurekawa:
  temperature-ideal: 29.0
```

**Approche 2**: Profils Spring `@Profile("br")`

**Approche 3**: Dépendre du core dans `futurekawa-app-brazil/pom.xml`

```xml
<dependency>
  <groupId>com.futurekawa</groupId>
  <artifactId>futurekawa-backend-core</artifactId>
  <version>1.0.0</version>
</dependency>
```

---

## 📊 État du Projet (2026-05-08)

| Élément | Status | Notes |
|---------|--------|-------|
| Backend Core | ✅ Fonctionnel | 5 entités, 27 tests, API complète |
| DTOs | ✅ Implémentés | Structure unilatérale (pas de boucles JSON) |
| JWT Auth | ⏳ Partiellement | Register fonctionne, login + validation à finir |
| MQTT | ⏳ À implémenter | Config Spring prête, listeners MQTT à écrire |
| Email Alerting | ⏳ À implémenter | Service prêt, stratégie à compléter |
| Tests Intégration | ⏳ À améliorer | TestContainers en place, couverture à augmenter |
| Docker | ✅ Fonctionnel | Multi-stage, prêt pour production |
| CI/CD GitHub Actions | ✅ Actif | Build + test + push GHCR automatisés |
| Frontend | ❌ Not started | À implémenter en React |
| IoT (ESP32) | ❌ Not started | À implémenter |

---

## 📚 Ressources & Documentation

- **[ARCHITECTURE.md](ARCHITECTURE.md)**: Détails complets, décisions de design
- **[CLAUDE.md](CLAUDE.md)**: Directives projet, journal de bord, questions certification
- **[informations/](informations/)**: Cahier des charges client complet
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html (quand le backend tourne)

---

## 🎓 Pour la Certification (Jury)

**Points clés à présenter**:
1. **Architecture générique** : Un produit core, consommé par N pays
2. **Monorepo scalable** : Pom parent, modules, CI/CD unifiée
3. **Infrastructure cloud-ready** : Docker, GHCR, GitHub Actions
4. **Code de qualité** : Tests, DTOs structurées, JWT, migrations DB
5. **Documentation** : Architecture complète, journal de bord, README

**Prochaines phases**:
- Phase 2 (Automation): Actuateurs (chauffage, humidification, aération)
- Phase 3 (HQ): Backend agrégation + Frontend React
- Phase 4 (IoT): Prototype ESP32 + DHT22

---

## 🤝 Équipe & Support

Pour des questions: Consulter [CLAUDE.md](CLAUDE.md) pour le contexte du projet et l'historique des décisions.
