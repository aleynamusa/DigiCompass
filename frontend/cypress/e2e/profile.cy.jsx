describe("Profile Page (Authenticated User)", () => {

    before(() => {
        cy.visit('/signup')
        cy.get('input[name="email"]').type("johndoe1@gmail.com");
        cy.get('input[name="username"]').type("johndoe");
        cy.get('input[name="birthDate"]').click();

        cy.get('.mantine-Calendar-calendarHeaderLevel').click();
        cy.get('.mantine-Calendar-calendarHeaderLevel').click();
        cy.get('.mantine-Calendar-calendarHeaderControlIcon[data-direction="previous"]').click()
        cy.get('.mantine-Calendar-calendarHeaderControlIcon[data-direction="previous"]').click()
        cy.get('.mantine-Calendar-yearsListCell').contains('2005').click();
        cy.get('.mantine-Calendar-monthsListCell').contains('Nov').click();
        cy.get('.mantine-Calendar-day').contains('10').click();

        cy.get('input[name="password"]').type("Hello123@_");
        cy.get('input[name="confirmPassword"]').type("Hello123@_");

        cy.get('input[name="birthDate"]').should('not.have.value', '');

        cy.get('button[type="submit"]').click();

    })

    beforeEach(() => {


        cy.visit(`logIn`);
        cy.get('input[name="username"]').type("admin");
        cy.get('input[name="password"]').type("Tetradka1011@");
        cy.get('button[type="submit"]').click();

    });


    it("loads and displays own profile correctly", () => {

        cy.visit("/profile/1");

        cy.contains("admin").should("exist");
        cy.contains("musaaleyna1@gmail.com").should("exist");
        cy.contains("November 10, 2005").should("exist");
        cy.contains("Bio").should("exist");
        cy.contains("This is my new bio from Cypress test!").should("exist");
    });

    it("shows loading state before profile loads", () => {
        cy.visit("/profile/1");

        cy.contains("Loading...").should("exist");
    });

    it("does not show email on another user's profile", () => {

        cy.visit("/profile/2");

        cy.contains("johndoe").should("exist");
        cy.contains("Email").should("not.exist");
        cy.contains("other@example.com").should("not.exist");
    });

    it("redirects unauthenticated user to login", () => {
        localStorage.clear();

        cy.visit("/profile");

        cy.url().should("include", "/login");
    });
});
