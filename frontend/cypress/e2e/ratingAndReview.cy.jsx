describe("Rating and Review Flow", () => {
    const frontendUrl = "http://localhost:5173";

    beforeEach(() => {
        cy.visit(`${frontendUrl}/routeDiscovery`);
    });

    it("should show review", () => {
        cy.contains("Discover Routes");

        cy.get('[data-slot="card-title"]')
            .contains('Forest Explorer Trail')
            .closest('[data-slot="card"]')
            .contains('View Details')
            .click();
        cy.contains('Reviews');
        cy.contains('Perfect for families — easy and shaded most of the way.');


    });

    it("should show rating", () => {
        cy.contains("Discover Routes");

        cy.get('[data-slot="card-title"]')
            .contains('Forest Explorer Trail')
            .closest('[data-slot="card"]')
            .contains('View Details')
            .click();
        cy.contains('Ratings').click();
        cy.get('.mantine-Rating-root').should('be.visible');


        cy.get('.mantine-Rating-symbolGroup').should('have.length', 5);


    });

    it("should show fraction rating", () => {
        cy.contains("Discover Routes");

        cy.get('[data-slot="card-title"]')
            .contains('Urban Commute Ride')
            .closest('[data-slot="card"]')
            .contains('View Details')
            .click();
        cy.contains('Ratings').click();
        cy.get('.mantine-Rating-root').should('be.visible');


        cy.get('.mantine-Rating-symbolBody[style*="50%"]').should('exist');
    });




});
