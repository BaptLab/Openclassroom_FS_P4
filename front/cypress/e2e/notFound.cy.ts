describe('Not Found spec', () => {
  it('should log and access the dashboard', () => {
    cy.visit('/wrongurl');

    cy.url().should('include', '/404');
    cy.contains('Page not found !');
  });
});
