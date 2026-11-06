---
description: "Use when working on the Yoga App backend in back/: Spring Boot REST controllers, services, repositories, JPA entities, JWT security, DTOs, or backend tests."
applyTo: "back/**"
---

# Consignes du backend — Yoga App

## Stack et organisation
- Le backend est une application Java 21 avec Spring Boot 3.5.5, Maven, Spring Web, Spring Data JPA, Spring Security, MySQL, JWT et Jakarta Validation.
- Le code applicatif est dans `back/src/main/java/com/openclassrooms/starterjwt/`, organisé en `controllers`, `services`, `repository`, `models`, `dto`, `mapper`, `payload`, `security` et `exception`.
- Respecter la séparation Controller -> Service -> Repository. Garder les contrôleurs centrés sur HTTP, la logique métier dans les services et l'accès aux données dans les repositories.

## API, modèles et persistance
- Ne pas exposer directement les entités JPA dans l'API : utiliser les DTOs et les mappers MapStruct existants.
- Préserver les routes, formats de requête/réponse et codes HTTP déjà consommés par le frontend (`front/src/app/core/service/`).
- Valider les payloads d'entrée avec les contraintes Jakarta Validation et `@Valid`, en cohérence avec les DTOs et les endpoints existants.
- Pour les changements de persistance, vérifier ensemble les entités, relations JPA et repositories ; préserver les contraintes et relations utilisées par les services.
- Utiliser l'injection par constructeur. Définir les dépendances comme champs `private final` lorsque c'est cohérent avec la classe.
- Encadrer les opérations métier multi-étapes qui modifient la base dans une transaction appropriée.

## Sécurité et erreurs
- Préserver la configuration Spring Security et le flux d'authentification JWT existants. Toute modification d'accès doit rester cohérente avec les règles et routes actuelles.
- Retourner des codes HTTP explicites et réutiliser les exceptions métier présentes, notamment `BadRequestException` et `NotFoundException`, plutôt que masquer les erreurs par des réponses de succès.
- Ne jamais journaliser de mot de passe, de token JWT ou d'autre donnée sensible ; limiter les logs aux informations nécessaires au diagnostic.

## Tests et commandes
- Ajouter ou adapter des tests JUnit/Spring pour les comportements modifiés, en isolant la logique métier et en couvrant les cas nominaux et les erreurs.
- Depuis `back/`, lancer `mvn test` pour les tests et `mvn spring-boot:run` pour démarrer l'application.
- Le démarrage local s'appuie sur Docker Compose pour MySQL ; tenir compte de cette dépendance pour les tests d'intégration et le lancement.
- Exécuter les validations adaptées au changement et signaler explicitement celles qui n'ont pas pu être lancées.
