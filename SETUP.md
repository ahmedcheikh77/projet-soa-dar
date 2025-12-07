# Guide Complet - Configuration et Déploiement DME System sur IntelliJ

## 🎯 Objectif
Configurer et lancer le Système de Gestion de Dossiers Médicaux Électroniques (DME) sur IntelliJ IDEA avec toutes les couches architecturales.

---

## 📋 Prérequis

### Logiciels requis
- **Java Development Kit (JDK) 17+** → [Télécharger](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **Apache Maven 3.8+** → [Télécharger](https://maven.apache.org/download.cgi)
- **PostgreSQL 13+** → [Télécharger](https://www.postgresql.org/download/)
- **Docker & Docker Compose** (optionnel mais recommandé)
- **IntelliJ IDEA 2023+** → [Télécharger](https://www.jetbrains.com/idea/)

### Configuration système
```bash
# Vérifier les versions installées
java -version         # Doit être 17 ou supérieur
mvn -version         # Doit être 3.8 ou supérieur
psql --version       # PostgreSQL
docker --version     # Docker (optionnel)
```

---

## 🚀 Étapes de Configuration Complètes

### Étape 1: Ouvrir le Projet dans IntelliJ

```
1. Ouvrir IntelliJ IDEA
2. File → Open
3. Sélectionner le répertoire: c:\Users\msi\OneDrive\Bureau\soa+dar
4. Cliquer sur "Open as Project"
5. Attendre le chargement complet (peut prendre 1-2 minutes)
```

### Étape 2: Configurer le JDK

```
1. File → Project Structure
2. Project → Project SDK
3. Cliquer sur "Edit"
4. Sélectionner JDK 17 (ou cliquer + pour ajouter)
5. Language level: 17
6. Appliquer et OK
```

### Étape 3: Configurer Maven

```
1. File → Settings (Ctrl+Alt+S)
2. Build, Execution, Deployment → Maven
3. Maven home path: C:\Program Files\Apache\maven (ou votre chemin)
4. User settings file: C:\Users\[username]\.m2\settings.xml
5. Appliquer et OK
```

### Étape 4: Recharger le Projet Maven

```
Dans l'onglet Maven (View → Tool Windows → Maven):
1. Cliquer sur "M" (recharger tous les projets)
2. Ou: dme-system → Reload All Maven Projects
3. Attendre la fin du téléchargement des dépendances
```

### Étape 5: Installer et Configurer PostgreSQL

#### Sous Windows:
```bash
# Télécharger et installer PostgreSQL depuis le site officiel

# Dans pgAdmin ou via cmd:
# Ouvrir cmd avec droits d'administrateur

# Créer la base de données
psql -U postgres

# Dans le prompt psql:
CREATE DATABASE dme_db;
CREATE USER dme_user WITH PASSWORD 'dme_password';
ALTER ROLE dme_user WITH CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE dme_db TO dme_user;
\connect dme_db
GRANT ALL PRIVILEGES ON SCHEMA public TO dme_user;
\q
```

#### Sous macOS/Linux:
```bash
# Installation via Homebrew (macOS)
brew install postgresql
brew services start postgresql

# Création de la base
createdb dme_db
psql dme_db << EOF
CREATE USER dme_user WITH PASSWORD 'dme_password';
GRANT ALL PRIVILEGES ON DATABASE dme_db TO dme_user;
EOF
```

#### Vérifier la connexion:
```bash
psql -U dme_user -d dme_db -h localhost
# Password: dme_password
# Vous devez voir le prompt: dme_db=>
```

### Étape 6: Exécuter le Script d'Initialisation SQL

```bash
# Option 1: Via pgAdmin
# 1. Ouvrir pgAdmin
# 2. Ouvrir le Query Tool pour la base dme_db
# 3. Copier le contenu de: src/main/resources/sql/init.sql
# 4. Exécuter

# Option 2: Via ligne de commande
psql -U dme_user -d dme_db -f src/main/resources/sql/init.sql
```

### Étape 7: Configurer la Source de Données dans IntelliJ

```
1. View → Tool Windows → Database (ou Ctrl+Shift+A "Database")
2. Cliquer sur le "+" pour ajouter une datasource
3. Sélectionner "PostgreSQL"
4. Configurer:
   - Driver: PostgreSQL (laisser IntelliJ télécharger)
   - Host: localhost
   - Port: 5432
   - Database: dme_db
   - User: dme_user
   - Password: dme_password
5. Test Connection → OK
6. Apply et OK
```

---

## 🔧 Compilation et Build

### Option 1: Compilation via Maven dans IntelliJ

```
Vue Maven:
1. dme-system → Lifecycle → clean
2. Attendre la fin
3. dme-system → Lifecycle → install
4. Attendre le message "BUILD SUCCESS"
```

### Option 2: Compilation via Terminal

```bash
cd c:\Users\msi\OneDrive\Bureau\soa+dar

# Nettoyer
mvn clean

# Compiler
mvn install -DskipTests

# Attendre "BUILD SUCCESS"
```

---

## ▶️ Lancer l'Application

### Option A: Avec Docker Compose (Recommandé)

#### Prérequis:
- Docker Desktop installé et en cours d'exécution

#### Étapes:
```bash
cd c:\Users\msi\OneDrive\Bureau\soa+dar

# Créer et démarrer les conteneurs
docker-compose up --build

# Ou en arrière-plan
docker-compose up -d --build

# Vérifier le statut
docker-compose ps

# Voir les logs
docker-compose logs -f dme-app

# Arrêter
docker-compose down
```

**Application accessible sur**: http://localhost:8080/dme

### Option B: Lancer via IntelliJ

#### 1. Créer une Configuration de Run

```
1. Run → Edit Configurations (ou Alt+Shift+F10)
2. Cliquer sur le "+"
3. Sélectionner "Spring Boot"
4. Configurer:
   - Name: "DME System"
   - Main class: com.dme.DmeSystemApplication
   - Module: dme-presentation
   - Working directory: /chemin/vers/projet
   - VM options: -Dspring.profiles.active=dev
   - Environment variables: (laisser vide)
5. Apply et OK
```

#### 2. Démarrer l'application

```
1. Sélectionner la configuration "DME System" dans le dropdown en haut à droite
2. Cliquer sur le bouton ▶️ (Run) ou Shift+F10
3. Attendre le message: "Started DmeSystemApplication in X seconds"
```

#### 3. Vérifier le démarrage

```
Les logs doivent afficher:
- "Configuring Spring Datasource from PostgreSQL"
- "Hibernate: create table users"
- "Started DmeSystemApplication"
- "Tomcat started on port(s): 8080"
```

**Application accessible sur**: http://localhost:8080/dme

### Option C: Lancer via Terminal

```bash
cd c:\Users\msi\OneDrive\Bureau\soa+dar

# Compiler et exécuter
mvn spring-boot:run

# Ou directement le JAR
cd dme-presentation
java -jar target/dme-presentation-1.0.0.jar
```

---

## ✅ Tester l'Application

### 1. Vérifier le serveur

```bash
# Dans une autre fenêtre de terminal
curl -X GET http://localhost:8080/dme/api/users

# Réponse attendue:
# {"message":"..."}
```

### 2. Tester via Postman

```
1. Importer DME_API_Collection.postman_collection.json dans Postman
2. Cliquer sur "Collections" → "DME System API"
3. Exécuter les tests dans cet ordre:
   a. Authentication → Register User
   b. Authentication → Login (copier le token)
   c. Appointments → Create Appointment
   d. Medical Records → Get Patient Records
```

### 3. Tester via IntelliJ HTTP Client

```
Créer un fichier: test.http

### Test Login
POST http://localhost:8080/dme/api/auth/login
Content-Type: application/json

{
  "username": "doctor1",
  "password": "SecurePass123"
}

### Utiliser le token retourné
@token = copier la valeur du champ "token" de la réponse ci-dessus

### Créer un rendez-vous
POST http://localhost:8080/dme/api/appointments
Authorization: Bearer @token
Content-Type: application/json

{
  "patientId": 4,
  "doctorId": 2,
  "appointmentDate": "2024-12-25T10:30:00",
  "notes": "Consultation"
}
```

---

## 🐛 Troubleshooting

### Problème 1: "Symbol cannot be found"
**Cause**: Les dépendances Maven n'ont pas été téléchargées
**Solution**:
```
Maven → dme-system → Reload All Maven Projects
Ou: Supprimer ~/.m2/repository et réinstaller
```

### Problème 2: "Connection to localhost:5432 refused"
**Cause**: PostgreSQL n'est pas en cours d'exécution
**Solution**:
```bash
# Vérifier si PostgreSQL est actif
pg_isready -h localhost -p 5432

# Démarrer PostgreSQL (Windows)
net start postgresql-x64-13  # Ou votre version

# Ou via Services Windows
services.msc → Trouver PostgreSQL → Démarrer
```

### Problème 3: "Port 8080 already in use"
**Cause**: Un autre processus utilise le port
**Solution**:
```bash
# Trouver le processus
netstat -ano | findstr :8080  # Windows
lsof -i :8080  # macOS/Linux

# Terminer le processus ou changer le port
# Dans application.properties: server.port=8081
```

### Problème 4: "JWT token invalid or expired"
**Cause**: Token expiré ou secret différent
**Solution**:
```
Vérifier que jwt.secret dans application.properties est identique partout
Se reconnecter pour obtenir un nouveau token
```

### Problème 5: "User authentication failed"
**Cause**: Credentials incorrects ou utilisateur n'existe pas
**Solution**:
```bash
# Vérifier les utilisateurs de test
psql -U dme_user -d dme_db
SELECT username, role, active FROM users;

# Réinitialiser les données
Exécuter: src/main/resources/sql/init.sql
```

### Problème 6: "Maven build failure"
**Solution**:
```bash
# Nettoyer complètement
rm -rf ~/.m2/repository/com/dme
mvn clean -U
mvn install

# Ou depuis IntelliJ:
File → Invalidate Caches → Invalidate and Restart
```

---

## 📊 Structure du Projet Finalisée

```
dme-system/
├── README.md                          # Guide d'utilisation
├── SETUP.md                          # Ce fichier
├── pom.xml                           # POM parent
├── Dockerfile                        # Docker image
├── docker-compose.yml                # Orchestration Docker
├── start.sh                          # Script de démarrage
│
├── dme-persistence/                  # Couche Persistance
│   ├── pom.xml
│   ├── src/main/java/com/dme/persistence/
│   │   ├── entity/                   # JPA Entities
│   │   └── repository/               # Spring Data JPA
│   └── src/test/java/...
│
├── dme-infrastructure/               # Couche Infrastructure
│   ├── pom.xml
│   └── src/main/java/com/dme/infrastructure/security/
│       ├── JwtTokenProvider.java
│       ├── JwtAuthenticationFilter.java
│       ├── EncryptionService.java
│       └── SecurityConfig.java
│
├── dme-distributed/                  # Couche Distribuée
│   ├── pom.xml
│   └── src/main/java/com/dme/distributed/
│       ├── rmi/                      # RMI Services
│       ├── socket/                   # Socket Server
│       └── jndi/                     # JNDI Registry
│
├── dme-soa/                          # Couche SOA
│   ├── pom.xml
│   └── src/main/java/com/dme/soa/
│       ├── dto/                      # Data Transfer Objects
│       └── service/                  # SOAP/REST Services
│
├── dme-presentation/                 # Couche Présentation
│   ├── pom.xml
│   ├── src/main/java/com/dme/
│   │   ├── DmeSystemApplication.java
│   │   └── presentation/controller/  # REST Controllers
│   ├── src/main/resources/
│   │   └── application.properties
│   └── src/test/java/...
│
└── src/main/resources/
    └── sql/
        └── init.sql                  # Script d'initialisation
```

---

## 🔐 Sécurité

### Meilleures pratiques implémentées:
- ✅ Authentification JWT
- ✅ Chiffrement AES des données sensibles
- ✅ Hash bcrypt des mots de passe
- ✅ Contrôle d'accès basé sur les rôles (RBAC)
- ✅ Logs d'audit
- ✅ HTTPS prêt (configuration TLS)

### Configuration de production:
```properties
# ⚠️ À changer avant déploiement en production:

# Générer une clé JWT sécurisée
jwt.secret=<clé-secrète-longue-et-aléatoire-256-bits>

# Changer les credentials PostgreSQL
spring.datasource.username=<nouvel_utilisateur>
spring.datasource.password=<nouveau_mot_de_passe>

# Activer HTTPS
server.ssl.key-store=<chemin-vers-keystore>
server.ssl.key-store-password=<motdepasse>
```

---

## 📈 Métriques et Monitoring

### Activer Spring Boot Actuator (optionnel)
```xml
<!-- Ajouter au pom.xml parent -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# application.properties
management.endpoints.web.exposure.include=health,metrics,env
```

Accéder à: `http://localhost:8080/dme/actuator/health`

---

## 📚 Ressources Utiles

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Jakarta JPA](https://jakarta.ee/specifications/persistence/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [Maven Guide](https://maven.apache.org/guides/)

---

## ✨ Prochaines Étapes

1. **Ajouter Swagger/OpenAPI** pour la documentation API
2. **Implémenter des tests d'intégration** complets
3. **Ajouter du monitoring** (Prometheus, Grafana)
4. **Configurer CI/CD** (GitHub Actions, GitLab CI)
5. **Scalabilité**: Kubernetes pour orchestration
6. **Microservices**: Découper en services indépendants

---

**Besoin d'aide ?** Consultez le README.md ou les logs applicatifs.

**Dernière mise à jour**: Décembre 2024
