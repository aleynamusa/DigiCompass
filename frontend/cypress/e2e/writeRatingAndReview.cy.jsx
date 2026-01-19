describe("Rating and Review Flow", () => {
    const frontendUrl = "http://localhost:5173";

    beforeEach(() => {
        cy.viewport(1280, 2000);

        cy.visit(`${frontendUrl}/logIn`);
        cy.get('input[name="username"]').type("admin");
        cy.get('input[name="password"]').type("Tetradka1011@");
        cy.get('button[type="submit"]').click();

        cy.visit(`${frontendUrl}/routeDiscovery`);
    });

    it("should write review", () => {
        cy.contains("Discover Routes").should('be.visible');

        cy.contains('View Details').click();
        cy.contains("Do you want to rate and review?").click();
        cy.get('.mantine-Button-label').contains("Hide rate and review").should('be.visible');

        cy.contains("Do you want to share your experience?").click();
        cy.get('.mantine-Textarea-input').type("what a beautiful route.");
        cy.get('.mantine-Button-label').contains('Submit').click();

        cy.contains("what a beautiful route.").should('be.visible');
    });

    it("should rate", () => {
        cy.contains("Discover Routes").should('be.visible');
        cy.contains("View Details").click();
        cy.contains("Do you want to rate and review?").click();

        cy.get('input[aria-label="2.5"]')
            .next('label')
            .click({ force: true });

        cy.get('input[aria-label="2.5"]').should('be.checked');

        cy.contains('Submit').click();
    });

    it("should submit a review with a picture", () => {
        cy.contains("View Details").click();
        cy.contains("Do you want to rate and review?").click();
        cy.contains("Do you want to share your experience?").click();

        cy.get('.mantine-Textarea-input')
            .should('be.visible')
            .type("Amazing route!");

        cy.get('.mantine-Dropzone-root')
            .attachFile("test-image.jpg", { subjectType: "drag-n-drop" });

        cy.contains("Submit").click();

        cy.contains("Amazing route!").should('be.visible');
    });

    it("should submit a review with a picture and rating", () => {
        cy.contains("View Details").click();
        cy.contains("Do you want to rate and review?").click();
        cy.contains("Do you want to share your experience?").click();

        cy.get('.mantine-Textarea-input')
            .should('be.visible')
            .type("Amazing route!");

        cy.get('.mantine-Dropzone-root')
            .attachFile("test-image.jpg", { subjectType: "drag-n-drop" });

        cy.window().then(win => win.scrollTo(0, win.document.body.scrollHeight));
        cy.wait(200);

        cy.get('input[aria-label="3.5"]')
                 .next('label')
                 .click({ force: true });

        cy.get('input[aria-label="3.5"]', { timeout: 5000 }).should('be.checked');

        cy.contains("Submit").click();

        cy.contains("Amazing route!").should('be.visible');

        cy.visit(`${frontendUrl}/routeDiscovery`);
        cy.contains('View Details').click();
        cy.contains("Do you want to rate and review?").click();

        cy.window().then(win => win.scrollTo(0, win.document.body.scrollHeight));
        cy.wait(200);

    });
});