describe("Route Creation", () => {
    const backendUrl = "http://localhost:8080";
    const frontendUrl = "http://localhost:5173";

    beforeEach(() => {
        cy.visit(`${frontendUrl}/logIn`, {
            onBeforeLoad(win) {
                cy.stub(win.navigator.geolocation, "getCurrentPosition")
                    .callsFake((success) => {
                        success({
                            coords: {
                                latitude: 48.1486,
                                longitude: 17.1077,
                                accuracy: 100,
                            },
                        });
                    });
            },
        });

        cy.get('input[name="username"]').type("admin");
        cy.get('input[name="password"]').type("Tetradka1011@");
        cy.get('button[type="submit"]').click();

        // Wait for navigation to complete
        cy.url().should('not.include', '/logIn');

        cy.visit(`${frontendUrl}/routeDiscovery`, {
            onBeforeLoad(win) {
                cy.stub(win.navigator.geolocation, "getCurrentPosition")
                    .callsFake((success) => {
                        success({
                            coords: {
                                latitude: 48.1486,
                                longitude: 17.1077,
                                accuracy: 100,
                            },
                        });
                    });
            },
        });

        cy.contains("Create Route").click();

        // Wait for the modal/dialog to be visible
        cy.contains("Create New Route").should("be.visible");
    });

    it("should Create a new Route", () => {
        cy.intercept("POST", `${backendUrl}/map`).as("calculate");

        cy.get('#name').type("New Route");
        cy.get('#description').type("This Route is perfect for nature lovers.");

        cy.get('[data-cy="difficulty-select"]').click();
        cy.contains('Easy').click({ force: true });

        cy.get('[data-cy="type-select"]').click();
        cy.contains('Hiking').click({ force: true });

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

        cy.wait("@calculate", { timeout: 10000 }).its('response').then((response) => {
            expect(response.statusCode).to.eq(200);

            const { distanceKm, durationHour } = response.body;

            cy.contains(`${distanceKm.toFixed(2)} km`).scrollIntoView();
            cy.contains(durationHour).scrollIntoView();
        });

        cy.get('.mantine-Dropzone-root')
            .scrollIntoView()
            .should('be.visible')
            .attachFile("test-image.jpg", { subjectType: "drag-n-drop" });

        cy.contains("Save Route")
            .scrollIntoView()
            .should('be.visible')
            .click();

        cy.url({ timeout: 10000 }).should('not.include', '/create');
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
        // Scroll to map section in modal
        cy.contains("Route Points").scrollIntoView();
        cy.wait(500);

        // Wait for map to be ready
        cy.get('.leaflet-container', { timeout: 30000 })
            .should('exist')
            .scrollIntoView()
            .should('be.visible');

        cy.get('.leaflet-tile-loaded', { timeout: 10000 })
            .should('have.length.at.least', 1);

        cy.wait(500);

        // Map points selecting
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

        // The error message appears in a Mantine Alert that might be position:fixed
        // We need to check it exists and contains the right text, even if covered
        cy.contains("Maximum number of points reached", { timeout: 10000 })
            .should('exist');

        // Alternative: Check the alert exists in DOM
        cy.get('.mantine-Alert-message')
            .should('exist')
            .and('contain', 'Maximum number of points reached');

        // Verify we can't add more than 5 points by checking the points count
        cy.contains("5 points").should('exist');
    });

    it("button is disabled when there are not complete requirements", () => {
        cy.contains("Save Route")
            .scrollIntoView()
            .should('be.disabled');
    });

    it("shows error when backend save fails", () => {
        cy.intercept("POST", `${backendUrl}/route/create`, {
            statusCode: 500,
            body: { message: "Internal Server Error" }
        }).as("saveRouteFail");

        cy.get('#name').type("Fail Route");
        cy.get('#description').type("This is a Fail Route.");

        cy.get('[data-cy="difficulty-select"]').click();
        cy.get('body').contains('Medium').click({ force: true });

        cy.get('[data-cy="type-select"]').click();
        cy.get('body').contains('Walking').click({ force: true });

        // Scroll to map
        cy.contains("Route Points").scrollIntoView();
        cy.wait(500);

        // Wait for map
        cy.get('.leaflet-container', { timeout: 30000 })
            .should('exist')
            .scrollIntoView()
            .should('be.visible');

        cy.wait(500);

        cy.get('.leaflet-container').first().scrollIntoView().click(100, 100, { force: true });
        cy.wait(500);
        cy.get('.leaflet-container').first().scrollIntoView().click(150, 150, { force: true });

        cy.contains("Save Route").scrollIntoView().click();

        cy.wait("@saveRouteFail");

        cy.contains("Failed to save route").scrollIntoView().should("be.visible");
    });

    it("prevents submission if user is not logged in", () => {
        cy.clearCookies();
        cy.clearLocalStorage();
        cy.reload();

        cy.contains("Create Route").click();

        cy.contains("You must be logged in to create routes").should("be.visible");

        cy.get('button[type="submit"]').should("be.disabled");
    });

    it("updates start/end points correctly when removing points", () => {
        // Scroll to map section in modal
        cy.contains("Route Points").scrollIntoView();
        cy.wait(500);

        // Wait for map
        cy.get('.leaflet-container', { timeout: 30000 })
            .should('exist')
            .scrollIntoView()
            .should('be.visible');

        cy.wait(500);

        // Add 3 points
        cy.get('.leaflet-container').first().scrollIntoView().click(100, 100, { force: true });
        cy.wait(500);
        cy.get('.leaflet-container').first().scrollIntoView().click(150, 150, { force: true });
        cy.wait(500);
        cy.get('.leaflet-container').first().scrollIntoView().click(200, 200, { force: true });
        cy.wait(500);

        // Scroll to see the points list
        cy.contains("3 points").scrollIntoView();

        // Remove middle point
        cy.get('[data-cy="remove-point"]').eq(1).scrollIntoView().click();

        cy.get('[data-cy="point-start"]').scrollIntoView().should("exist");
        cy.get('[data-cy="point-end"]').scrollIntoView().should("exist");
    });
});