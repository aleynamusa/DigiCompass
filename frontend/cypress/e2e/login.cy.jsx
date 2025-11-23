describe("Login Flow", () => {
    const backendUrl = "http://localhost:8080";
    const frontendUrl = "http://localhost:5173";

    beforeEach(() => {
        cy.visit(`${frontendUrl}/login`);
    });

    it("should show login form", () => {
        cy.contains("Log In");
        cy.get('input[name="username"]').should("be.visible");
        cy.get('input[name="password"]').should("be.visible");
    });

    it("should log in successfully with valid credentials", () => {
        cy.intercept("POST", `${backendUrl}/users/logIn`).as("loginRequest");

        cy.get('input[name="username"]').type("admin");
        cy.get('input[name="password"]').type("Tetradka1011@");
        cy.get('button[type="submit"]').click();

        cy.wait("@loginRequest").its("response.statusCode").should("eq", 200);

        cy.url().should("eq", `${frontendUrl}/`);
        cy.contains("Dashboard");
        cy.contains("Weather")
    });

    it("should show an error for invalid credentials", () => {
        cy.get('input[name="username"]').type("wronguser");
        cy.get('input[name="password"]').type("wrongpass");
        cy.get('button[type="submit"]').click();

        cy.contains("Invalid username or password");
    });
});
