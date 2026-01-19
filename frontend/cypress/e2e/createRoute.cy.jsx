describe("Route Creation", () => {
    const frontendUrl = "http://localhost:5173";

    beforeEach(() => {
        cy.visit(`${frontendUrl}/logIn`);
        cy.get('input[name="username"]').type("admin");
        cy.get('input[name="password"]').type("Tetradka1011@");
        cy.get('button[type="submit"]').click();

        cy.visit(`${frontendUrl}/routeDiscovery`);

        cy.contains("Create Route").click();
        cy.contains("Create New Route").should("be.visible");
    });

    it("should Create a new Route", () => {

        cy.get('#name').type("New Route");
        cy.get('#description').type("This Route is perfect for nature lovers.");

        cy.get('[data-cy="difficulty-select"]').click();
        cy.contains('Easy').click({ force: true });

        cy.get('[data-cy="type-select"]').click();
        cy.contains('Walking').click({ force: true });

        cy.contains("Route Points").scrollIntoView();

        cy.wait(500);

        cy.get('.leaflet-container', { timeout: 30000 })
            .should('exist')
            .scrollIntoView()
            .should('be.visible');

        cy.get('.leaflet-tile-loaded', { timeout: 10000 })
            .should('have.length.at.least', 1);

        cy.wait(500);

        cy.get('.leaflet-container')
            .first()
            .click(100, 100, { force: true });

        cy.wait(500);

        cy.get('.leaflet-container')
            .first()
            .click(150, 150, { force: true });

        cy.contains('2 points').scrollIntoView().should('be.visible');

        cy.get('.mantine-Dropzone-root')
            .scrollIntoView()
            .should('be.visible')
            .attachFile("test-image.jpg", { subjectType: "drag-n-drop" });

        cy.contains("Save Route")
            .scrollIntoView()
            .should('be.visible')
            .click();

        cy.contains("New Route").scrollIntoView().should("be.visible");
    });

    it("should show the Route Form fields", () => {
        cy.contains("Create New Route").should("be.visible");

        cy.contains("Route Name").should("be.visible");
        cy.get('#name').should("be.visible");

        cy.contains("Description").should("be.visible");
        cy.get('#description').should("be.visible");

        cy.contains("Difficulty").should("be.visible");
        cy.contains("Route Type").should("be.visible");

        cy.contains("Route Points").should("exist");
        cy.contains("0 points").should("exist");
        cy.contains("No points added yet").should("exist");

        cy.contains("Drag images here or click to select files").should("exist");
        cy.contains("Attach as many files as you like")
            .scrollIntoView()
            .should("exist");
    });

    it("should not allow to pick more than 5 points", () => {
        cy.contains("Route Points").scrollIntoView();
        cy.wait(500);

        cy.get('.leaflet-container', { timeout: 30000 })
            .should('exist')
            .scrollIntoView()
            .should('be.visible');

        cy.get('.leaflet-tile-loaded', { timeout: 10000 })
            .should('have.length.at.least', 1);

        cy.wait(500);

        const points = [
            [100, 100],
            [150, 150],
            [160, 160],
            [165, 165],
            [157, 157],
            [153, 156]
        ];

        points.forEach(([x, y]) => {
            cy.get('.leaflet-container')
                .first()
                .scrollIntoView()
                .click(x, y, { force: true });
            cy.wait(300);
        });

        cy.contains("Maximum number of points reached", { timeout: 10000 })
            .should('exist');

        cy.get('.mantine-Alert-message')
            .should('exist')
            .and('contain', 'Maximum number of points reached');

        cy.contains("5 points").should('exist');
    });

    it("button is disabled when there are not complete requirements", () => {
        cy.contains("Save Route")
            .scrollIntoView()
            .should('be.disabled');
    });

    it("shows error when backend save fails", () => {
        cy.get('#name').type("Fail Route");
        cy.get('#description').type("This is a.");

        cy.get('[data-cy="difficulty-select"]').click();
        cy.get('body').contains('Hard').click({ force: true });

        cy.get('[data-cy="type-select"]').click();
        cy.get('body').contains('Walking').click({ force: true });

        cy.contains("Route Points").scrollIntoView();
        cy.wait(500);

        cy.get('.leaflet-container', { timeout: 30000 })
            .should('exist')
            .scrollIntoView()
            .should('be.visible');

        cy.wait(500);

        cy.get('.leaflet-container').first().scrollIntoView().click(100, 100, { force: true });
        cy.wait(500);
        cy.get('.leaflet-container').first().scrollIntoView().click(150, 150, { force: true });

        cy.contains("Save Route").scrollIntoView().click();

        cy.contains("Failed to save route").scrollIntoView().should("be.visible");
    });

    it("shows log in pop up when not logged in", () => {
        cy.clearCookies();
        cy.clearLocalStorage();
        cy.reload();

        cy.contains("Create Route").click();

        cy.contains("Log In");
        cy.get('input[name="username"]').should("be.visible");
        cy.get('input[name="password"]').should("be.visible");

    });

    it("updates start/end points correctly when removing points", () => {
        cy.contains("Route Points").scrollIntoView();
        cy.wait(500);

        cy.get('.leaflet-container', { timeout: 30000 })
            .should('exist')
            .scrollIntoView()
            .should('be.visible');

        cy.wait(500);

        cy.get('.leaflet-container').first().scrollIntoView().click(100, 100, { force: true });
        cy.wait(500);
        cy.get('.leaflet-container').first().scrollIntoView().click(150, 150, { force: true });
        cy.wait(500);
        cy.get('.leaflet-container').first().scrollIntoView().click(200, 200, { force: true });
        cy.wait(500);

        cy.contains("3 points").scrollIntoView();

        cy.get('[data-cy="remove-point"]').eq(1).scrollIntoView().click();

        cy.get('[data-cy="point-start"]').scrollIntoView().should("exist");
        cy.get('[data-cy="point-end"]').scrollIntoView().should("exist");
    });
});