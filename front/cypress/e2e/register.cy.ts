describe('Register spec', () => {
  it('register successfull', () => {
    cy.visit('/register');

    cy.intercept('POST', '/api/auth/register', {
      body: {
        username: 'john.doe@gmail.com',
        firstName: 'john',
        lastName: 'DOE',
        password: 'password',
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/login',
      },
      []
    ).as('session');

    cy.get('input[formControlName=firstName]').type('john');
    cy.get('input[formControlName=lastName]').type('DOE');
    cy.get('input[formControlName=email]').type('john.doe@gmail.com');
    cy.get('input[formControlName=password]').type(
      `${'password'}{enter}{enter}`
    );

    cy.url().should('include', '/login');
  });
});
