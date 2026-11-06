interface LoginResponse {
  token: string;
  id: number;
}

describe('Front and backend integration', () => {
  const enabled = Cypress.env('realApi') === true;
  const password = 'cypress-e2e-password';
  let email: string | undefined;
  let userId: number | undefined;
  let token: string | undefined;
  let accountDeleted = false;

  beforeEach(() => {
    email = undefined;
    userId = undefined;
    token = undefined;
    accountDeleted = false;
  });

  afterEach(() => {
    if (email && !accountDeleted) {
      if (userId && token) {
        cy.request({
          method: 'DELETE',
          url: `/api/user/${userId}`,
          headers: { Authorization: `Bearer ${token}` },
          failOnStatusCode: false,
        }).then(response => {
          expect([200, 404]).to.include(response.status);
        });
      } else {
        cy.request({
          method: 'POST',
          url: '/api/auth/login',
          body: { email, password },
          failOnStatusCode: false,
        }).then(response => {
          if (response.status === 200) {
            const login = response.body as LoginResponse;
            return cy.request({
              method: 'DELETE',
              url: `/api/user/${login.id}`,
              headers: { Authorization: `Bearer ${login.token}` },
              failOnStatusCode: false,
            }).then(deleteResponse => {
              expect([200, 404]).to.include(deleteResponse.status);
            });
          }
          expect(response.status).to.eq(401);
          return undefined;
        });
      }
    }
  });

  (enabled ? it : it.skip)('registers, authenticates, loads protected data and deletes the account', () => {
    const uniqueEmail = `cypress-${Date.now()}@example.com`;
    email = uniqueEmail;

    cy.intercept('POST', '/api/auth/register').as('register');
    cy.intercept('POST', '/api/auth/login').as('login');
    cy.intercept('GET', '/api/session').as('sessions');
    cy.intercept('GET', /\/api\/user\/\d+$/).as('profile');
    cy.intercept('DELETE', /\/api\/user\/\d+$/).as('deleteAccount');

    cy.visit('/register');
    cy.get('input[formControlName="firstName"]').type('Cypress');
    cy.get('input[formControlName="lastName"]').type('Tester');
    cy.get('input[formControlName="email"]').type(uniqueEmail);
    cy.get('input[formControlName="password"]').type(password);
    cy.get('button[type="submit"]').click();
    cy.wait('@register').its('response.statusCode').should('eq', 200);
    cy.location('pathname').should('eq', '/login');

    cy.get('input[formControlName="email"]').type(uniqueEmail);
    cy.get('input[formControlName="password"]').type(password);
    cy.get('button[type="submit"]').click();
    cy.wait('@login').then(({ response }) => {
      expect(response?.statusCode).to.eq(200);
      const login = response?.body as LoginResponse;
      userId = login.id;
      token = login.token;
    });
    cy.wait('@sessions').its('response.statusCode').should('eq', 200);
    cy.location('pathname').should('eq', '/sessions');

    cy.contains('span.link', 'Account').click();
    cy.wait('@profile').its('response.statusCode').should('eq', 200);
    cy.contains(uniqueEmail).should('be.visible');
    cy.contains('button', 'Detail').click();
    cy.wait('@deleteAccount').its('response.statusCode').should('eq', 200);
    accountDeleted = true;
    cy.location('pathname').should('eq', '/login');
  });
});
