describe("Home User Search", () => {
    const backendUrl = "http://localhost:8080";
    beforeEach(() => {
        cy.visit("/");
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

        cy.contains("admin").click();

        cy.url().should("include", "/profile/1");
    });
});
