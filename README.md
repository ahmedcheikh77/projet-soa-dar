# DME System - Electronic Medical Records Management

## Vue d'ensemble
DME System est une application distribuée de gestion de dossiers médicaux électroniques conçue avec une architecture SOA multi-couches. Elle combine des services web (SOAP et REST), une communication distribuée (RMI, Sockets, JNDI), et une persistance de données sécurisée.

## Architecture

### Couches principales

1. **Couche Présentation** (`dme-presentation`)
   - Contrôleurs REST pour accès client
   - Points d'entrée API
   - Authentification et autorisation

2. **Couche SOA** (`dme-soa`)
   - Services web SOAP
   - DTOs (Data Transfer Objects)
   - Services métier

3. **Couche Distribuée** (`dme-distributed`)
   - RMI pour synchronisation distribuée
   - Sockets pour streaming de données
   - JNDI pour découverte de services

4. **Couche Persistance** (`dme-persistence`)
   - Entités JPA
   - Repositories (DAOs)
   - Interactions base de données

5. **Couche Infrastructure** (`dme-infrastructure`)
   - Authentification JWT
   - Chiffrement des données
   - Configuration de sécurité

## Configuration d'IntelliJ

### Prérequis
- JDK 17 ou supérieur
- Maven 3.8+
- PostgreSQL 13+
- IntelliJ IDEA 2023+

### Étapes de configuration

1. **Cloner/Ouvrir le projet dans IntelliJ**
   ```
   File → Open → Sélectionner le répertoire du projet
   ```

2. **Configurer le JDK**
   - File → Project Structure → Project
   - SDK: Sélectionner JDK 17
   - Language level: 17

3. **Configuration Maven**
   - File → Settings → Build, Execution, Deployment → Maven
   - Maven home path: Spécifier votre répertoire Maven
   - Local repository: ~/.m2/repository

4. **Recharger le projet Maven**
   - View → Tool Windows → Maven
   - Cliquer sur "Reload All Maven Projects" (icône de actualisation)

5. **Configurer la base de données**
   - Créer une base PostgreSQL:
     ```sql
     CREATE DATABASE dme_db;
     CREATE USER dme_user WITH PASSWORD 'dme_password';
     ALTER ROLE dme_user WITH CREATEDB;
     GRANT ALL PRIVILEGES ON DATABASE dme_db TO dme_user;
     ```

## Démarrage du projet

### Option 1: Avec Docker Compose (Recommandé)

```bash
# À la racine du projet
docker-compose up --build

# L'application démarrera sur http://localhost:8080/dme
```

### Option 2: Avec IntelliJ

1. **Compiler le projet**
   - Maven → dme-system → Lifecycle → clean
   - Maven → dme-system → Lifecycle → install

2. **Lancer l'application**
   - Edit Configurations → + → Spring Boot
   - Name: DME System
   - Main class: com.dme.DmeSystemApplication
   - Module: dme-presentation
   - Click Run

3. **Vérifier le démarrage**
   - Consulter les logs pour: "Started DmeSystemApplication"

## Endpoints API

### Authentification
```
POST /dme/api/auth/login
{
  "username": "doctor1",
  "password": "password123"
}

POST /dme/api/auth/register
{
  "username": "patient1",
  "email": "patient@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "role": "PATIENT"
}

POST /dme/api/auth/logout
```

### Rendez-vous
```
GET /dme/api/appointments/{id}
GET /dme/api/appointments/patient/{patientId}
GET /dme/api/appointments/doctor/{doctorId}

POST /dme/api/appointments
{
  "patientId": 1,
  "doctorId": 2,
  "appointmentDate": "2024-12-15T14:00:00",
  "notes": "Consultation générale"
}

PUT /dme/api/appointments/{id}
{
  "status": "COMPLETED",
  "notes": "Visite effectuée"
}

DELETE /dme/api/appointments/{id}
```

### Dossiers Médicaux
```
GET /dme/api/medical-records/{id}
GET /dme/api/medical-records/patient/{patientId}
GET /dme/api/medical-records/doctor/{doctorId}

POST /dme/api/medical-records
{
  "patientId": 1,
  "doctorId": 2,
  "diagnosis": "Diagnostic préliminaire",
  "treatment": "Traitement prescrit",
  "prescription": "Médicament X 2x/jour",
  "encrypted": true
}

PUT /dme/api/medical-records/{id}
{
  "diagnosis": "Diagnostic révisé",
  "treatment": "Traitement modifié"
}

DELETE /dme/api/medical-records/{id}
```

### Gestion des Utilisateurs
```
GET /dme/api/users (Admin)
GET /dme/api/users/{id}
GET /dme/api/users/username/{username}

PUT /dme/api/users/{id}
{
  "email": "newemail@example.com",
  "fullName": "Nouveau Nom",
  "active": true
}

DELETE /dme/api/users/{id} (Admin)

POST /dme/api/users/{id}/deactivate (Admin)
```

## Authentification

### JWT Token
Tous les endpoints (sauf login/register) nécessitent un token JWT dans l'en-tête:
```
Authorization: Bearer <token>
```

### Rôles et Permissions
- **ADMIN**: Accès complet
- **DOCTOR**: Gestion rendez-vous et dossiers médicaux
- **PATIENT**: Lecture propres dossiers et rendez-vous
- **HOSPITAL**: Accès dossiers institutionnels

## Tests

### Exécuter les tests
```bash
# Via Maven
mvn test

# Via IntelliJ
Cliquer droit sur dme-persistence/src/test → Run Tests
```

### Exemple de test avec cURL
```bash
# Enregistrement
curl -X POST http://localhost:8080/dme/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "doctor1",
    "email": "doctor@hospital.com",
    "password": "SecurePass123",
    "fullName": "Dr. Jane Smith",
    "role": "DOCTOR"
  }'

# Connexion
curl -X POST http://localhost:8080/dme/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "doctor1",
    "password": "SecurePass123"
  }'

# Utiliser le token retourné
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

# Créer un rendez-vous
curl -X POST http://localhost:8080/dme/api/appointments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "doctorId": 2,
    "appointmentDate": "2024-12-20T10:30:00",
    "notes": "Consultation"
  }'
```

## Structure du Projet

```
dme-system/
├── dme-persistence/          # Couche base de données
│   ├── src/main/java/com/dme/persistence/
│   │   ├── entity/           # Entités JPA
│   │   └── repository/       # Repositories
│   └── pom.xml
├── dme-infrastructure/       # Couche sécurité
│   ├── src/main/java/com/dme/infrastructure/
│   │   └── security/         # JWT, Chiffrement
│   └── pom.xml
├── dme-distributed/          # Couche distribuée
│   ├── src/main/java/com/dme/distributed/
│   │   ├── rmi/             # RMI Services
│   │   ├── socket/          # Socket Server
│   │   └── jndi/            # JNDI Registry
│   └── pom.xml
├── dme-soa/                  # Couche SOA
│   ├── src/main/java/com/dme/soa/
│   │   ├── service/          # Services métier
│   │   └── dto/              # Data Transfer Objects
│   └── pom.xml
├── dme-presentation/         # Couche présentation
│   ├── src/main/java/com/dme/
│   │   ├── DmeSystemApplication.java
│   │   └── presentation/controller/  # Contrôleurs REST
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── pom.xml                   # Pom parent
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Configuration PostgreSQL

### Initialisation de la base
```sql
-- Créer la base
CREATE DATABASE dme_db;

-- Créer l'utilisateur
CREATE USER dme_user WITH PASSWORD 'dme_password';

-- Donner les droits
GRANT ALL PRIVILEGES ON DATABASE dme_db TO dme_user;
ALTER ROLE dme_user WITH CREATEDB;

-- Vérifier la connexion
psql -U dme_user -d dme_db -h localhost
```

### Les tables seront créées automatiquement par Hibernate (ddl-auto=update)

## Chiffrement et Sécurité

### Chiffrement des données sensibles
```java
encryptionService.encrypt(medicalData)  // Chiffrer
encryptionService.decrypt(encryptedData) // Déchiffrer
```

### Hash des mots de passe
```java
encryptionService.hashPassword(password)    // Hasher
encryptionService.verifyPassword(password, hash) // Vérifier
```

## Déploiement en Production

### Variables d'environnement requises
```
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/dme_db
SPRING_DATASOURCE_USERNAME=dme_user
SPRING_DATASOURCE_PASSWORD=dme_password
JWT_SECRET=<clé secrète longue et sécurisée>
JWT_EXPIRATION=86400000
```

### Build production
```bash
mvn clean package -DskipTests -P production
docker build -t dme-app:latest .
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/dme_db \
  -e SPRING_DATASOURCE_USERNAME=dme_user \
  -e SPRING_DATASOURCE_PASSWORD=dme_password \
  dme-app:latest
```

## Troubleshooting

### Issue: "cannot find symbol"
**Solution**: Maven → Reload All Maven Projects

### Issue: Port 8080 déjà utilisé
**Solution**: 
```properties
# Changer dans application.properties
server.port=8081
```

### Issue: Connexion PostgreSQL échouée
**Vérifier**:
- PostgreSQL est en cours d'exécution
- Les identifiants sont corrects
- La base `dme_db` existe
- L'utilisateur a les droits nécessaires

### Issue: Token JWT invalide
**Cause**: Token expiré ou secret JWT différent
**Solution**: 
- Rafraîchir le token en se reconnectant
- Vérifier JWT_SECRET dans application.properties

## Contact et Support

Pour plus d'informations ou signaler des bugs:
- Documentation API: http://localhost:8080/dme/swagger-ui.html (après activation)
- Logs applicatif: `target/logs/dme-app.log`

---

**Version**: 1.0.0  
**Dernière mise à jour**: Décembre 2024
