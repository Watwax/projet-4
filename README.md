# Yoga App

Application de gestion de séances de yoga composée d’une API REST Spring Boot et d’une application web Angular. Le dépôt contient également des tests unitaires, d’intégration et end-to-end (E2E), ainsi que la configuration nécessaire pour vérifier les seuils de couverture.

## Architecture

- [`front/`](./front/) : application Angular 19, tests Jest et scénarios Cypress.
- [`back/`](./back/) : API Java 21 / Spring Boot 3.5, persistance MySQL et tests JUnit.

## Prérequis

- Node.js 18.19 ou supérieur et npm (Angular CLI 19 requiert au minimum Node.js 18.19).
- JDK 21 et Maven 3.9 ou supérieur.
- Docker avec Docker Compose, démarré avant le backend.
- Les ports 3306 (MySQL), 8080 (API) et 4200 (frontend) disponibles.

## Installation et démarrage

### Récupérer le projet

Clonez le dépôt puis placez-vous dans son dossier :

```bash
git clone https://github.com/Watwax/projet-4.git
cd projet-4
```

### 1. Démarrer le backend et sa base MySQL

Ouvrez un terminal à la racine du dépôt, puis placez-vous dans `back/` :

```bash
cd back
```

Le backend lit les variables `DB_USER`, `DB_PASSWORD`, `DB_HOST`, `DB_PORT`, `DB_NAME` et `TOKEN_SECRET`. Vérifiez que le fichier `.env` du dossier `back/` contient les valeurs adaptées à votre environnement. Démarrer le backend :

```bash
mvn spring-boot:run
```

Spring Boot démarre automatiquement le conteneur MySQL défini dans `back/compose.yaml`, puis lance l’API sur `http://localhost:8080`. Gardez ce terminal ouvert pendant l’utilisation de l’application.

La base est créée au démarrage, mais aucun compte administrateur n’est inséré automatiquement. Pour créer le compte de démonstration, exécutez le script [`insert_user.sql`](./back/src/main/resources/sql/insert_user.sql) une seule fois après le démarrage de MySQL ; les identifiants de ce compte sont documentés dans le [README du backend](./back/README.md).

### 2. Installer et lancer le frontend

Ouvrez un second terminal à la racine du dépôt :

```bash
cd front
npm install
npm run start
```

`npm install` est nécessaire lors de la première installation (et après une modification des dépendances). Le serveur Angular démarre sur `http://localhost:4200` ; ouvrez cette adresse dans votre navigateur.

Le proxy Angular transmet les requêtes `/api` au backend sur `http://localhost:8080`. Pour que les fonctionnalités utilisant l’API marchent, démarrez donc le backend avant le frontend et laissez les deux terminaux ouverts.

Pour arrêter les serveurs, utilisez `Ctrl+C` dans chacun des terminaux.

## Tests

### Backend : JUnit, Mockito et Spring Boot Test

Depuis `back/` :

```bash
mvn test
```

Les tests unitaires vérifient notamment les services et les composants de sécurité. Le test d’intégration Spring Boot/MockMvc exerce l’API avec H2 en mémoire ; il ne nécessite ni MySQL ni Docker Compose.

Pour générer le rapport JaCoCo et vérifier les seuils configurés, lancez :

```bash
mvn clean verify
```

Le rapport HTML est généré dans `back/target/site/jacoco/index.html`. JaCoCo exige au moins **80 % pour chacun** des indicateurs suivants : instructions, branches, lignes et méthodes (fonctions). Les packages `dto` et `payload` sont exclus du calcul.

### Frontend : tests unitaires et d’intégration Jest

Depuis `front/` :

```bash
npm test -- --runInBand
```

Pour générer le rapport de couverture et faire vérifier les seuils Jest :

```bash
npm run test:coverage
```

Le rapport HTML se trouve dans `front/coverage/jest/lcov-report/index.html` et le fichier LCOV dans `front/coverage/jest/lcov.info`. Jest impose au niveau global au moins **80 % pour chacun** des indicateurs : instructions, branches, fonctions et lignes.

### E2E : Cypress

Depuis `front/`, lancez les scénarios Cypress :

```bash
npm run cypress:run
```

Cette commande lance le navigateur headless et le serveur Angular instrumenté pour mesurer la couverture E2E. Après l’exécution, générez le rapport et contrôlez les seuils :

```bash
npm run e2e:coverage
```

Le rapport HTML est disponible dans `front/coverage/lcov-report/index.html`. NYC vérifie au moins **80 % pour chacun** des indicateurs : instructions, branches, fonctions et lignes. Les scénarios E2E couvrent les parcours de l’interface ; les appels API sont simulés pour que ces scénarios restent reproductibles.

Pour exécuter également le scénario E2E qui utilise le véritable backend, démarrez d’abord l’API et sa base MySQL, puis lancez depuis `front/` :

```bash
CYPRESS_realApi=true npm run cypress:run
```

Ce scénario vérifie l’inscription, l’authentification, le chargement de données protégées et la suppression du compte créé pour le test.
