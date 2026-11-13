---
description: "Use when working on the Yoga App frontend in front/: Angular components, templates, routing, authentication, sessions, API services, or frontend tests."
applyTo: "front/**"
---

# Consignes du frontend — Yoga App

## Stack et organisation
- Le frontend est une SPA Angular 19 en TypeScript, avec Angular Material, RxJS, Jest et Cypress.
- Le code applicatif se trouve dans `front/src/app/` : pages et composants dans `pages/` et `components/`, modèles dans `core/models/`, services dans `core/service/`, guards dans `guards/`, intercepteur HTTP dans `interceptors/`.
- Respecter les responsabilités existantes : composants pour l'interface, services pour les appels API et la logique partagée, modèles TypeScript pour les échanges.

## Angular et TypeScript
- Créer des composants standalone et déclarer leurs dépendances (`CommonModule`, modules Material, directives et pipes) dans leurs imports, en cohérence avec les composants voisins.
- Utiliser les blocs de contrôle Angular (`@if`, `@else`, `@for`) plutôt que les directives structurelles obsolètes `*ngIf` et `*ngFor`. Fournir une expression `track` stable à chaque `@for`.
- Conserver la séparation entre template, styles et composant utilisée dans le dossier concerné.
- Respecter les conventions d'injection déjà présentes à proximité (`inject()` ou injection par constructeur) ; éviter de mélanger les deux styles dans un même composant.
- Annoter explicitement le type de retour de chaque méthode et fonction (`void` lorsqu'elle ne retourne rien).
- Décrire les réponses et requêtes avec des types précis et les interfaces de `core/models/`. Ne jamais utiliser `any`, y compris dans les callbacks, services et helpers de test ; préférer un type concret ou `unknown`.
- Utiliser les APIs Angular Router pour les navigations et paramètres d'URL, et centraliser les appels HTTP dans les services existants plutôt que dans les composants.
- Pour les nouvelles fonctionnalités, réutiliser Angular Material et les composants partagés existants avant d'ajouter un nouveau composant ou module.

## API, authentification et navigation
- Les services communiquent avec l'API via `HttpClient` et RxJS. Réutiliser les services existants pour les sessions, enseignants, utilisateurs et l'authentification.
- Préserver les routes et le format des endpoints consommés par le backend. Vérifier également l'impact sur `customJwtInterceptorFn`, les guards et l'état de session avant de modifier le flux d'authentification.
- Ne jamais stocker ni afficher de secrets dans le code ou les journaux ; ne pas contourner les guards ou l'intercepteur pour simplifier un appel.
- Préférer l'`async` pipe dans les templates. Pour tout `subscribe()` dans un composant, lier le cycle de vie de l'abonnement à celui du composant avec `takeUntilDestroyed` et `DestroyRef` (ou un mécanisme de désabonnement équivalent).

## Tests et commandes
- Utiliser **Jest** pour les tests unitaires et d’intégration du frontend Angular. Ajouter ou mettre à jour des tests à côté du composant ou service concerné (`*.spec.ts`) ; vérifier le comportement observable, les cas nominaux et les erreurs.
- Dans les tests Jest, utiliser les utilitaires de test Angular et mocker les dépendances externes. Pour les services HTTP, préférer les outils de test `HttpClient` d’Angular plutôt que de vrais appels réseau. Ne pas utiliser Cypress pour tester une unité isolée.
- Utiliser **Cypress** pour les tests end-to-end (e2e) qui valident les parcours utilisateur complets et les interactions entre pages. Vérifier le résultat visible et les transitions attendues ; intercepter ou préparer les réponses API lorsque le scénario n’a pas besoin de tester le backend réel.
- Garder les tests Cypress dans `front/cypress/e2e/` et les tests Jest au format `*.spec.ts`. Ne pas confondre test e2e et test d’intégration Angular : choisir l’outil adapté au niveau testé.
- Depuis `front/` : `npm run start` pour le développement, `npm test -- --runInBand` pour Jest, `npm run lint` pour le lint et `npm run cypress:run` pour Cypress.
- Cypress utilise `http://localhost:4200` comme `baseUrl` ; démarrer le frontend avec `npm run start` avant `npm run cypress:run`. Les scénarios qui appellent le backend réel nécessitent également le backend et sa base de données. `npm run cypress:open` ouvre le runner interactif.
- Exécuter les tests et contrôles adaptés au changement et signaler explicitement ceux qui n'ont pas pu être lancés.
