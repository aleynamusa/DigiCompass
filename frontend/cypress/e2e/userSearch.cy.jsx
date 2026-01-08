describe("Home User Search", () => {
    const backendUrl = "http://localhost:8080";
    const frontendUrl = "http://localhost:5173";

    beforeEach(() => {
        cy.visit(`${frontendUrl}/logIn`);
        cy.get('input[name="username"]').type("admin");
        cy.get('input[name="password"]').type("Tetradka1011@");
        cy.get('button[type="submit"]').click();

    });

    it("searches users and shows suggestions", () => {
        cy.intercept("GET", `${backendUrl}/users/search*username=jo*`, {
            statusCode: 200,
            body: [
                {
                    id: 1,
                    username: "john",
                    imageUrl: "images/john.jpg"
                }
            ]
        }).as("searchUsers");

        cy.get("input[placeholder='Search...']").type("jo");

        cy.wait("@searchUsers");

        cy.contains("john").should("exist");
    });

    it("search users shows if there are no matches", () => {

        cy.get("input[placeholder='Search...']").type("aaaa");

        cy.contains("No users match your search.");
    });

    it("navigates to user profile when selecting a user", () => {
        cy.get("input[placeholder='Search...']").type("admin");

        cy.get('div[role="listbox"]')
            .contains("admin")
            .should('be.visible')
            .click();

        cy.url().should("include", "/profile/1");
    });


});
