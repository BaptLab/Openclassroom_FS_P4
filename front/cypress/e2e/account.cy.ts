describe('Account', () => {
  it('should display the account infos', () => {
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
    cy.get('input[formControlName=email]').type('john.doe@gmail.com');
    cy.get('input[formControlName=password]').type(
      `${'password'}{enter}{enter}`
    );

    // Intercept '/api/me' and provide user data
    cy.intercept(
      {
        method: 'GET',
        url: '/api/user/1',
      },
      {
        body: {
          id: 1,
          email: 'john.doe@gmail.com',
          lastName: 'DOE',
          firstName: 'john',
          admin: false,
          password: 'password',
          createdAt: new Date(),
        },
      }
    ).as('/me');

    // Visit user profile page
    cy.get('[routerlink="me"]').click();

    cy.contains('john.doe@gmail.com');
    cy.contains('john DOE');
    cy.contains('Create at');
    cy.contains('Last update');
    cy.contains('User information');
  });

  it('should delete the account', () => {
    // Register user
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
    cy.get('input[formControlName=email]').type('john.doe@gmail.com');
    cy.get('input[formControlName=password]').type(
      `${'password'}{enter}{enter}`
    );

    // Intercept '/api/me' and provide user data
    cy.intercept(
      {
        method: 'GET',
        url: '/api/user/1',
      },
      {
        body: {
          id: 1,
          email: 'john.doe@gmail.com',
          lastName: 'DOE',
          firstName: 'john',
          admin: false,
          password: 'password',
          createdAt: new Date(),
        },
      }
    ).as('/me');

    // Visit user profile page
    cy.get('[routerlink="me"]').click();

    cy.intercept(
      {
        method: 'DELETE',
        url: '/api/user/1',
      },
      {
        statusCode: 200,
      }
    ).as('deleteUser');

    cy.get('.my2 > .mat-focus-indicator').click();
    cy.wait('@deleteUser');

    cy.contains('Login');
    cy.contains('Register');
    cy.contains('Your account has been deleted !');
    cy.url().should('include', '');
  });
});
