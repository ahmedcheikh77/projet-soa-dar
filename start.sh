#!/bin/bash

# Script de démarrage de l'application DME System

echo "=========================================="
echo "Démarrage du système DME"
echo "=========================================="

# Vérifier si Maven est installé
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé. Veuillez installer Maven."
    exit 1
fi

# Vérifier si Docker est installé (optionnel)
if ! command -v docker &> /dev/null; then
    echo "⚠️  Docker n'est pas installé. Utilisation du mode local."
    USE_DOCKER=false
else
    USE_DOCKER=true
fi

# Option 1: Démarrage avec Docker
if [ "$USE_DOCKER" = true ]; then
    echo ""
    echo "1️⃣  Démarrage avec Docker Compose..."
    echo "Vérification de PostgreSQL..."
    
    docker-compose up -d
    
    echo "✅ Application démarrée sur http://localhost:8080/dme"
    echo ""
    echo "Vérification du statut..."
    sleep 5
    curl -s http://localhost:8080/dme/api/auth/login || echo "Application en cours de démarrage..."
    
else
    echo ""
    echo "2️⃣  Démarrage en mode local..."
    echo "Vérification de PostgreSQL..."
    
    # Vérifier si PostgreSQL est accessible
    if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
        echo "❌ PostgreSQL n'est pas accessible sur localhost:5432"
        echo "Assurez-vous que PostgreSQL est en cours d'exécution."
        exit 1
    fi
    
    # Nettoyer et compiler
    echo "Nettoyage et compilation du projet..."
    mvn clean package -DskipTests -q
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilation réussie"
        
        # Démarrer l'application
        echo "Démarrage de l'application..."
        cd dme-presentation
        java -jar target/dme-presentation-1.0.0.jar
    else
        echo "❌ Erreur lors de la compilation"
        exit 1
    fi
fi
