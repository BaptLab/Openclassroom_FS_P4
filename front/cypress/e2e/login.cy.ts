describe('Login spec', () => {
  it('should log and access the dashboard', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'test!1234'}{enter}{enter}`
    );

    cy.url().should('include', '/sessions');
  });

  it('should disconnect', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'test!1234'}{enter}{enter}`
    );

    cy.url().should('include', '/sessions');

    cy.get('.mat-toolbar > .ng-star-inserted > :nth-child(3)').click();
    cy.url().should('include', '/');
  });

  it('should log and display the session', () => {
    cy.visit('/login');

    // Login user
    cy.intercept('POST', '/api/auth/login', {
      body: {
        token:
          'eyJhbGcidzOiJIUzUxMiJ9.eyJzdWIiOiJqZWFuLmR1cG9udEBnbWFpbC5jb20iLCJpYXQiOjE3MDQ4OTg0OTcsImV4cCI6MTcwNDk4NDg5N30.t8dIPdQICflZnN_iFwzbSn_KeDsaxmma_eK6dGDUtb0DQz0YEUe5WLXoNuDU2WXaZ5709Hx5Gb5ONlNIpCwNAg',
        type: 'Bearer',
        id: 1,
        username: 'john.doe@gmail.com',
        firstName: 'John',
        lastName: 'DOE',
        admin: false,
      },
    });

    cy.intercept('GET', '/api/session', {
      body: [
        {
          id: 1,
          name: 'Sample Session',
          description: 'This is a sample session',
          date: new Date(),
          teacher_id: 123,
          users: [456, 789],
          createdAt: new Date(),
          updatedAt: new Date(),
        },
      ],
    }).as('session');

    cy.get('input[formControlName=email]').type('john.doe@gmail.com');
    cy.get('input[formControlName=password]').type(
      `${'password'}{enter}{enter}`
    );
    cy.contains('Sample Session');
    cy.contains('This is a sample session');
    cy.get('.picture').should('exist');
  });
});
