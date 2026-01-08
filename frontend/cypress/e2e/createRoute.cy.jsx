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
    });

    it("should Create a new Route", () => {
        cy.intercept("POST", `${backendUrl}/map`).as("calculate");

        cy.get('#name').type("New Route");

        cy.get('#description').type("This Route is perfect for nature lovers.");

        cy.get('[data-cy="difficulty-select"]').click();
        cy.get('body').contains('Easy').click({ force: true });

        cy.get('[data-cy="type-select"]').click();
        cy.get('body').contains('Hiking').click({ force: true });

        cy.get('.leaflet-container', { timeout: 30000 }).should('be.visible').then(() => {
            cy.wait(3000);

            // Now do the clicks
            cy.get('.leaflet-container').click(100, 100);
            cy.get('.leaflet-container').click(150, 150);
        });


        cy.wait("@calculate").then(({ response }) => {
            expect(response.statusCode).to.eq(200);

            const { distanceKm, durationHour } = response.body;

            cy.contains(`${distanceKm.toFixed(2)} km`);
            cy.contains(durationHour);
        });

        cy.get('.mantine-Dropzone-root').scrollIntoView()
            .attachFile("test-image.jpg", { subjectType: "drag-n-drop" });

        cy.contains("Save Route")
            .scrollIntoView()
            .click();

    });


    it("should show the Route Form fields", () => {
        cy.contains("Create New Route").should("be.visible");

        cy.contains("Route Name").should("be.visible");
        cy.get('#name').should("be.visible");

        cy.contains("Description").should("be.visible");
        cy.get('#description').should("be.visible");

        cy.contains("Difficulty").should("be.visible");
        cy.contains("Route Type").should("be.visible");

        cy.contains("Route Points")
            .should("exist");


        cy.contains("0 points")
            .should("exist");

        cy.contains("No points added yet")
            .should("exist");

        cy.contains("Drag images here or click to select files")
            .should("exist");

        cy.contains("Attach as many files as you like")
            .scrollIntoView()
            .should("exist");

    });


    it("should not allow to pick more than 5 points", () => {


        //mapPoints selecting
        cy.get('.leaflet-container').click(100, 100);
        cy.get('.leaflet-container').click(150, 150);
        cy.get('.leaflet-container').click(160, 160);
        cy.get('.leaflet-container').click(165, 165);
        cy.get('.leaflet-container').click(157, 157);
        cy.get('.leaflet-container').click(153, 156);

        cy.contains("Maximum number of points reached (max 5)").scrollIntoView();

    });

    it("button is disabled when there is not complete requirements", () => {
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

        cy.get('.leaflet-container').click(100, 100);
        cy.get('.leaflet-container').click(150, 150);

        cy.contains("Save Route").click();

        cy.wait("@saveRouteFail");

        cy.contains("Failed to save route").should("be.visible");
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
        // Add 3 points
        cy.get('.leaflet-container').click(100, 100);
        cy.get('.leaflet-container').click(150, 150);
        cy.get('.leaflet-container').click(200, 200);

        cy.get('[data-cy="remove-point"]').eq(1).click();

        cy.get('[data-cy="point-start"]').should("exist");
        cy.get('[data-cy="point-end"]').should("exist");
    });
});
