# Yoga

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.2.16.

## Start the project

Git clone:

> git clone https://github.com/OpenClassrooms-Student-Center/P5-Full-Stack-testing

Go inside folder:

> cd yoga

Install dependencies:

> npm install

Launch Front-end:

> npm run start;


### Test

#### E2E

Launching e2e test:

> npm run cypress:run

Generate coverage report (you should launch e2e test before):

> npm run e2e:coverage

The HTML report is available at:

> front/coverage/lcov-report/index.html

The Cypress suite covers login and registration, session list/detail/create/update, account information and deletion, application navigation, route guards, and the not-found screen. API calls are checked at the frontend boundary in the deterministic UI tests.

The E2E coverage report enforces at least 80% for statements, branches, functions, and lines. Cypress runs the coverage-instrumented Angular build; run `npm run e2e:coverage` after the e2e suite to generate the report and check those thresholds.

#### Frontend/backend integration E2E

To run the integration scenario against the real Spring Boot API and MySQL database, start the backend with Docker available:

> cd back && mvn spring-boot:run

Then, from `front/`, enable the real API test while running Cypress:

> CYPRESS_realApi=true npm run cypress:run

This scenario creates a uniquely named test account through the UI, logs in, loads protected session and account data, then deletes the account. It is skipped in the normal UI-only run, which uses intercepted API responses.

#### Unitary test

Launching test:

> npm run test

for following change:

> npm run test:watch

The Jest unit and integration test coverage thresholds are set to 80% for statements, branches, functions, and lines. Generate the coverage reports from `front/` with:

> npm run test:coverage

The HTML report is available at `front/coverage/jest/lcov-report/index.html`; the LCOV data is written to `front/coverage/jest/lcov.info`. Jest fails when any configured global threshold is not met.
