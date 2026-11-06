interface TestUser {
  id: number;
  token: string;
  type: string;
  username: string;
  firstName: string;
  lastName: string;
  admin: boolean;
}

const admin: TestUser = {
  id: 1,
  token: 'test-admin-token',
  type: 'Bearer',
  username: 'admin@example.com',
  firstName: 'Yoga',
  lastName: 'Admin',
  admin: true,
};

const member: TestUser = {
  id: 2,
  token: 'test-member-token',
  type: 'Bearer',
  username: 'member@example.com',
  firstName: 'Alex',
  lastName: 'Member',
  admin: false,
};

const session = {
  id: 10,
  name: 'Morning yoga',
  description: 'A gentle yoga session',
  date: '2025-03-01T10:00:00.000+00:00',
  teacher_id: 3,
  users: [member.id],
  createdAt: '2025-02-01T10:00:00',
  updatedAt: '2025-02-02T10:00:00',
};

const teacher = {
  id: 3,
  firstName: 'Taylor',
  lastName: 'Smith',
  createdAt: '2024-01-01T10:00:00',
  updatedAt: '2024-01-02T10:00:00',
};

const profile = {
  id: member.id,
  email: member.username,
  firstName: member.firstName,
  lastName: member.lastName,
  admin: false,
  createdAt: '2024-01-01T10:00:00',
  updatedAt: '2024-01-02T10:00:00',
};

function stubLogin(user: TestUser = admin): void {
  cy.intercept('POST', '/api/auth/login', {
    statusCode: 200,
    body: user,
  }).as('login');
}

function signIn(user: TestUser = admin): void {
  stubLogin(user);
  cy.intercept('GET', '/api/session', {
    statusCode: 200,
    body: [session],
  }).as('sessions');

  cy.visit('/login');
  cy.get('input[formControlName="email"]').type(user.username);
  cy.get('input[formControlName="password"]').type('test-password');
  cy.get('button[type="submit"]').click();
  cy.wait('@login').its('request.body').should('deep.equal', {
    email: user.username,
    password: 'test-password',
  });
  cy.wait('@sessions').its('request.headers').should('have.property', 'authorization');
}

function stubTeacherList(): void {
  cy.intercept('GET', '/api/teacher', {
    statusCode: 200,
    body: [teacher],
  }).as('teachers');
}

describe('Yoga App screens and API flows', () => {
  it('renders the login screen, validates inputs and toggles password visibility', () => {
    cy.visit('/login');

    cy.contains('mat-card-title', 'Login').should('be.visible');
    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('input[formControlName="email"]').type('invalid-email');
    cy.get('input[formControlName="password"]').type('test-password');
    cy.get('button[type="submit"]').should('be.disabled');
    cy.get('input[formControlName="password"]').should('have.attr', 'type', 'password');
    cy.get('button[aria-label="Hide password"]').click();
    cy.get('input[formControlName="password"]').should('have.attr', 'type', 'text');
  });

  it('logs in, loads sessions from the API and shows authenticated navigation', () => {
    signIn();

    cy.location('pathname').should('eq', '/sessions');
    cy.contains('mat-card-title', 'Rentals available').should('be.visible');
    cy.contains('mat-card-title', session.name).should('be.visible');
    cy.contains(session.description).should('be.visible');
    cy.contains('span.link', 'Sessions').should('be.visible');
    cy.contains('span.link', 'Account').should('be.visible');
    cy.contains('span.link', 'Logout').should('be.visible');
  });

  it('shows an error when the API rejects login credentials', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Bad credentials' },
    }).as('login');
    cy.visit('/login');
    cy.get('input[formControlName="email"]').type(member.username);
    cy.get('input[formControlName="password"]').type('wrong-password');
    cy.get('button[type="submit"]').click();
    cy.wait('@login');

    cy.contains('.error', 'An error occurred').should('be.visible');
    cy.location('pathname').should('eq', '/login');
  });

  it('registers through the API and displays duplicate-email errors', () => {
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: { message: 'User registered successfully!' },
    }).as('register');
    cy.visit('/register');
    cy.get('input[formControlName="firstName"]').type('Alex');
    cy.get('input[formControlName="lastName"]').type('Member');
    cy.get('input[formControlName="email"]').type('new-member@example.com');
    cy.get('input[formControlName="password"]').type('test-password');
    cy.get('button[type="submit"]').click();
    cy.wait('@register').its('request.body').should('deep.equal', {
      firstName: 'Alex',
      lastName: 'Member',
      email: 'new-member@example.com',
      password: 'test-password',
    });
    cy.location('pathname').should('eq', '/login');

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Error: Email is already taken!' },
    }).as('duplicateRegistration');
    cy.visit('/register');
    cy.get('input[formControlName="firstName"]').type('Alex');
    cy.get('input[formControlName="lastName"]').type('Member');
    cy.get('input[formControlName="email"]').type('new-member@example.com');
    cy.get('input[formControlName="password"]').type('test-password');
    cy.get('button[type="submit"]').click();
    cy.wait('@duplicateRegistration');
    cy.contains('.error', 'An error occurred').should('be.visible');
  });

  it('shows session list actions according to the logged-in user role', () => {
    signIn();
    cy.contains('button', 'Create').should('be.visible');
    cy.contains('button', 'Edit').should('be.visible');

    signIn(member);
    cy.contains(session.name).should('be.visible');
    cy.contains('button', 'Create').should('not.exist');
    cy.contains('button', 'Edit').should('not.exist');
    cy.contains('button', 'Detail').should('be.visible');
  });

  it('creates a session with a teacher selected from the API', () => {
    stubTeacherList();
    cy.intercept('POST', '/api/session', {
      statusCode: 200,
      body: { ...session, name: 'New evening session' },
    }).as('createSession');
    signIn();
    cy.contains('button', 'Create').click();
    cy.wait('@teachers');

    cy.contains('h1', 'Create session').should('be.visible');
    cy.get('input[formControlName="name"]').type('New evening session');
    cy.get('input[formControlName="date"]').type('2025-03-05');
    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.contains('mat-option', `${teacher.firstName} ${teacher.lastName}`).click();
    cy.get('textarea[formControlName="description"]').type('A new session for everyone');
    cy.get('button[type="submit"]').click();

    cy.wait('@createSession').its('request.body').should('deep.equal', {
      name: 'New evening session',
      date: '2025-03-05',
      teacher_id: teacher.id,
      description: 'A new session for everyone',
    });
    cy.location('pathname').should('eq', '/sessions');
  });

  it('loads session details and updates member participation through the API', () => {
    let attendees = [...session.users];
    cy.intercept('GET', '/api/session/10', request => {
      request.reply({ statusCode: 200, body: { ...session, users: attendees } });
    }).as('sessionDetail');
    cy.intercept('GET', '/api/teacher/3', { statusCode: 200, body: teacher }).as('teacherDetail');
    cy.intercept('POST', '/api/session/10/participate/2', request => {
      attendees = [...attendees, member.id];
      request.reply({ statusCode: 200 });
    }).as('participate');
    cy.intercept('DELETE', '/api/session/10/participate/2', request => {
      attendees = attendees.filter(id => id !== member.id);
      request.reply({ statusCode: 200 });
    }).as('unparticipate');
    signIn(member);

    cy.contains('button', 'Detail').click();
    cy.wait('@sessionDetail');
    cy.wait('@teacherDetail');
    cy.contains('h1', 'Morning Yoga').should('be.visible');
    cy.contains('Taylor SMITH').should('be.visible');
    cy.contains('1 attendees').should('be.visible');
    cy.contains('button', 'Do not participate').click();
    cy.wait('@unparticipate');
    cy.wait('@sessionDetail');
    cy.contains('button', 'Participate').should('be.visible');
    cy.contains('button', 'Participate').click();
    cy.wait('@participate');
    cy.wait('@sessionDetail');
    cy.contains('button', 'Do not participate').should('be.visible');
  });

  it('deletes a session as an administrator', () => {
    cy.intercept('GET', '/api/session/10', { statusCode: 200, body: session }).as('sessionDetail');
    cy.intercept('GET', '/api/teacher/3', { statusCode: 200, body: teacher }).as('teacherDetail');
    cy.intercept('DELETE', '/api/session/10', { statusCode: 200, body: null }).as('deleteSession');
    signIn();
    cy.contains('button', 'Detail').click();
    cy.wait('@sessionDetail');
    cy.wait('@teacherDetail');
    cy.contains('h1', 'Morning Yoga').should('be.visible');
    cy.contains('button', 'Delete').click();
    cy.wait('@deleteSession');

    cy.location('pathname').should('eq', '/sessions');
  });

  it('loads account information and deletes the member account through the API', () => {
    cy.intercept('GET', '/api/user/2', { statusCode: 200, body: profile }).as('profile');
    cy.intercept('DELETE', '/api/user/2', { statusCode: 200, body: null }).as('deleteAccount');
    signIn(member);
    cy.contains('span.link', 'Account').click();
    cy.wait('@profile').its('request.headers').should('have.property', 'authorization');

    cy.contains('h1', 'User information').should('be.visible');
    cy.contains('Alex MEMBER').should('be.visible');
    cy.contains(member.username).should('be.visible');
    cy.contains('button', 'Detail').click();
    cy.wait('@deleteAccount');
    cy.location('pathname').should('eq', '/login');
  });

  it('shows administrator account details without the self-delete action', () => {
    const adminProfile = { ...profile, ...admin, email: admin.username, admin: true };
    cy.intercept('GET', '/api/user/1', { statusCode: 200, body: adminProfile }).as('profile');
    signIn();
    cy.contains('span.link', 'Account').click();
    cy.wait('@profile');

    cy.contains('You are admin').should('be.visible');
    cy.contains('Delete my account').should('not.exist');
  });

  it('redirects protected and public routes correctly and renders the not-found screen', () => {
    cy.visit('/sessions');
    cy.location('pathname').should('eq', '/login');

    signIn();
    cy.go('back');
    cy.location('pathname').should('eq', '/sessions');

    cy.visit('/path-that-does-not-exist');
    cy.location('pathname').should('eq', '/404');
    cy.contains('Page not found !').should('be.visible');
  });

  it('logs out from the application header', () => {
    signIn();
    cy.contains('span.link', 'Logout').click();

    cy.location('pathname').should('eq', '/login');
    cy.contains('a.link', 'Login').should('be.visible');
    cy.contains('span.link', 'Logout').should('not.exist');
  });
});
