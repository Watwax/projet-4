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
- Conserver la séparation entre template, styles et composant utilisée dans le dossier concerné.
- Respecter les conventions d'injection déjà présentes à proximité (`inject()` ou injection par constructeur) ; éviter de mélanger les deux styles dans un même composant.
- Décrire les réponses et requêtes avec les interfaces de `core/models/`. Éviter `any` et garder des types HTTP cohérents avec l'API.
- Utiliser les APIs Angular Router pour les navigations et paramètres d'URL, et centraliser les appels HTTP dans les services existants plutôt que dans les composants.
- Pour les nouvelles fonctionnalités, réutiliser Angular Material et les composants partagés existants avant d'ajouter un nouveau composant ou module.

## API, authentification et navigation
- Les services communiquent avec l'API via `HttpClient` et RxJS. Réutiliser les services existants pour les sessions, enseignants, utilisateurs et l'authentification.
- Préserver les routes et le format des endpoints consommés par le backend. Vérifier également l'impact sur `customJwtInterceptorFn`, les guards et l'état de session avant de modifier le flux d'authentification.
- Ne jamais stocker ni afficher de secrets dans le code ou les journaux ; ne pas contourner les guards ou l'intercepteur pour simplifier un appel.

## Tests et commandes
- Ajouter ou mettre à jour des tests Jest à côté du composant ou service concerné (`*.spec.ts`) ; mocker les dépendances HTTP et Angular selon les tests existants.
- Utiliser Cypress pour les scénarios de bout en bout lorsque le comportement traverse plusieurs pages ou interactions.
- Depuis `front/` : `npm run start` pour le développement, `npm test -- --runInBand` pour Jest, `npm run lint` pour le lint et `npm run cypress:run` pour Cypress.
- Exécuter les tests et contrôles adaptés au changement et signaler explicitement ceux qui n'ont pas pu être lancés.
