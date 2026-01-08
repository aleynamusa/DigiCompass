describe("Rating and Review Flow", () => {
    const backendUrl = "http://localhost:8080";
    const frontendUrl = "http://localhost:5173";

    beforeEach(() => {
        cy.visit(`${frontendUrl}/logIn`);
        cy.get('input[name="username"]').type("admin");
        cy.get('input[name="password"]').type("Tetradka1011@");
        cy.get('button[type="submit"]').click();

        cy.visit(`${frontendUrl}/routeDiscovery`);
    });

    it("should write review", () => {
        cy.intercept("POST", `${backendUrl}/review`).as("reviewRequest");
        cy.contains("Discover Routes");

        cy.contains('View Details')
            .click();

        cy.contains("Do you want to rate and review?").click();
        cy.get('.mantine-Button-label').contains("Hide rate and review");

        cy.contains("Do you want to share your experience?").click();
        cy.get('.mantine-Textarea-input').type("what a beautiful route.");
        cy.get('.mantine-Button-label').contains('Submit').click();

        cy.contains("what a beautiful route.");

        cy.wait("@reviewRequest")
            .its("response.statusCode")
            .should("eq", 201);

    })

    it("should rate", () => {
        cy.intercept("POST", `${backendUrl}/rating`).as("rateRequest");

        cy.contains("Discover Routes");
        cy.contains("View Details").click();
        cy.contains("Do you want to rate and review?").click();

        cy.get('input[aria-label="2.5"]')
            .next('label')
            .click({ force: true });

        cy.contains('Submit').click();

        //Wait for rating request
        cy.wait("@rateRequest")
            .its("response.statusCode")
            .should("eq", 201);
    });

    it("should submit a review with a picture", () => {
        cy.intercept("POST", `${backendUrl}/review`).as("reviewRequest");

        cy.contains("View Details").click();
        cy.contains("Do you want to rate and review?").click();
        cy.contains("Do you want to share your experience?").click();

        cy.get('.mantine-Textarea-input')
            .should('be.visible')
            .type("Amazing route!");


        cy.get('.mantine-Dropzone-root')
            .attachFile("test-image.jpg", { subjectType: "drag-n-drop" });

        cy.contains("Submit").click();

        cy.wait("@reviewRequest")
            .its("response.statusCode")
            .should("eq", 201);
    });

    it("should submit a review with a picture and rating", () => {

        cy.intercept("POST", `${backendUrl}/review`).as("reviewRequest");
        cy.intercept("POST", `${backendUrl}/rating`).as("rateRequest");

        cy.contains("View Details").click();
        cy.contains("Do you want to rate and review?").click();
        cy.contains("Do you want to share your experience?").click();

        cy.get('.mantine-Textarea-input')
            .should('be.visible')
            .type("Amazing route!");


        cy.get('.mantine-Dropzone-root')
            .attachFile("test-image.jpg", { subjectType: "drag-n-drop" });

            cy.get('input[aria-label="2.5"]')
                .next('label')
                .click({ force: true });

        cy.contains("Submit").click();

        cy.wait("@reviewRequest")
            .its("response.statusCode")
            .should("eq", 201);

        cy.wait("@rateRequest")
                 .its("response.statusCode")
                 .should("eq", 201);

    })



});
