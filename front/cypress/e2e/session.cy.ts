describe('Session spec', () => {
  it('should display the session(s)', () => {
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

    cy.intercept('GET', '/api/session/1', {
      body: {
        id: 1,
        name: 'Sample Session',
        description: 'This is a sample session',
        date: new Date(),
        teacher_id: 123,
        users: [456, 789],
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('sessionDetails');

    cy.intercept('GET', '/api/teacher/123', {
      body: {
        id: 123,
        lastName: 'Doe',
        firstName: 'John',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('teacher');

    cy.get('.mat-card-actions > .mat-focus-indicator').click();
  });

  it('should participate to a session', () => {
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
          users: [],
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

    cy.intercept('GET', '/api/session/1', {
      body: {
        id: 1,
        name: 'Sample Session',
        description: 'This is a sample session',
        date: new Date(),
        teacher_id: 123,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('sessionDetails');

    cy.intercept('GET', '/api/teacher/123', {
      body: {
        id: 123,
        lastName: 'Doe',
        firstName: 'John',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('teacher');

    cy.get('.mat-card-actions > .mat-focus-indicator').click();

    cy.intercept('GET', '/api/session/1', {
      body: {
        id: 1,
        name: 'Sample Session',
        description: 'This is a sample session',
        date: new Date(),
        teacher_id: 123,
        users: [1],
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('sessionDetails');

    cy.intercept('POST', '/api/session/1/participate/1', {
      body: {},
    }).as('particpation');

    cy.get('.mat-card-content > :nth-child(1) > :nth-child(1) > .ml1')
      .invoke('text')
      .should('eq', '0 attendees');

    cy.get('div.ng-star-inserted > .mat-focus-indicator').click();

    cy.get('.mat-card-content > :nth-child(1) > :nth-child(1) > .ml1')
      .invoke('text')
      .should('eq', '1 attendees');
  });

  it('should unparticipate to a session', () => {
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
          users: [1],
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

    cy.intercept('GET', '/api/session/1', {
      body: {
        id: 1,
        name: 'Sample Session',
        description: 'This is a sample session',
        date: new Date(),
        teacher_id: 123,
        users: [1],
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('sessionDetails');

    cy.intercept('GET', '/api/teacher/123', {
      body: {
        id: 123,
        lastName: 'Doe',
        firstName: 'John',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('teacher');

    cy.get('.mat-card-actions > .mat-focus-indicator').click();
    cy.get('.mat-card-content > :nth-child(1) > :nth-child(1) > .ml1')
      .invoke('text')
      .should('eq', '1 attendees');

    cy.intercept('DELETE', '/api/session/1/participate/1', {
      body: {},
    }).as('particpation');

    cy.intercept('GET', '/api/session/1', {
      body: {
        id: 1,
        name: 'Sample Session',
        description: 'This is a sample session',
        date: new Date(),
        teacher_id: 123,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('sessionDetails');

    cy.intercept('GET', '/api/teacher/123', {
      body: {
        id: 123,
        lastName: 'Doe',
        firstName: 'John',
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    }).as('teacher');

    cy.get('div.ng-star-inserted > .mat-focus-indicator').click();

    cy.get('.mat-card-content > :nth-child(1) > :nth-child(1) > .ml1')
      .invoke('text')
      .should('eq', '0 attendees');
  });

  it('should create a session', () => {
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

    cy.intercept('GET', 'api/teacher', {
      body: [
        {
          id: 123,
          lastName: 'Doe',
          firstName: 'John',
          createdAt: new Date(),
          updatedAt: new Date(),
        },
        {
          id: 123,
          lastName: 'Doe',
          firstName: 'John',
          createdAt: new Date(),
          updatedAt: new Date(),
        },
      ],
    });

    cy.get('.mat-card-header > .mat-focus-indicator').click();

    cy.get('input[formControlName=name]').type('Nom de la session');

    cy.get('input[formControlName=date]').click();
    cy.get('input[formControlName=date]').type('2022-01-23');

    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').contains('John Doe').click();

    cy.get('textarea[formControlName=description]').type(
      'Description de la session.'
    );

    cy.intercept('POST', 'api/session', {
      body: {
        id: 1,
        name: 'Sample Session',
        description: 'This is a sample session',
        date: new Date(),
        teacher_id: 123,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    });

    cy.get('.mt2 > [fxlayout="row"] > .mat-focus-indicator').click();

    cy.url().should('include', '/sessions');
    cy.contains('Session created !');
  });
});
