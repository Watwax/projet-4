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
- Utiliser **JUnit** (JUnit Jupiter) et **Mockito** pour tester le code Java. Ajouter ou adapter des tests dans `back/src/test/java/`, en suivant la convention `*Test.java` et les exemples existants.
- Tester les services isolément en mockant leurs repositories et autres dépendances avec Mockito ; vérifier les valeurs de retour, les exceptions et les interactions pertinentes. Tester aussi les cas nominaux, les entrées invalides et les erreurs métier.
- Faire correspondre chaque test à un cas précis du plan de testing ; éviter de regrouper plusieurs comportements indépendants dans un même test.
- Pour les tests d’intégration de l’API, utiliser **Spring Boot Test** (par exemple `@SpringBootTest` et `@AutoConfigureMockMvc`) afin de vérifier le câblage Spring et les échanges HTTP réels. Réserver les mocks aux dépendances externes au comportement visé ; ne pas démarrer tout le contexte Spring pour un simple test unitaire.
- Veiller à ce que les tests d’intégration représentent au moins 30 % de la suite backend.
- Ne pas écrire de tests pour les packages contenant du code de type DTO, notamment `dto` et `payload` ; les exclure aussi des rapports de couverture.
- Depuis `back/`, lancer `mvn test` pour les tests et `mvn spring-boot:run` pour démarrer l'application.
- Pour cibler une classe, utiliser `mvn -Dtest=UserServiceTest test` en remplaçant le nom par la classe souhaitée.
- Les tests unitaires qui mockent les repositories ne doivent pas dépendre de MySQL. Les tests d’intégration qui sollicitent la base doivent utiliser la configuration de test prévue (H2 en mémoire) et ne pas nécessiter Docker Compose. Le démarrage local de l’application s’appuie sur Docker Compose pour MySQL.
- Exécuter les validations adaptées au changement et signaler explicitement celles qui n'ont pas pu être lancées.
