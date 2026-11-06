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

> npm run e2e

Generate coverage report (you should launch e2e test before):

> npm run e2e:coverage

Report is available here:

> front/coverage/lcov-report/index.html

#### Unitary test

Launching test:

> npm run test

for following change:

> npm run test:watch

The Jest unit and integration test coverage thresholds are set to 80% for statements, branches, functions, and lines. Generate the coverage reports from `front/` with:

> npm run test:coverage

The HTML report is available at `front/coverage/jest/lcov-report/index.html`; the LCOV data is written to `front/coverage/jest/lcov.info`. Jest fails when any configured global threshold is not met.
