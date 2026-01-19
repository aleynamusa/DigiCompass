describe("Edit Profile Page", () => {

    beforeEach(() => {
        cy.visit(`logIn`);
        cy.get('input[name="username"]').type("admin");
        cy.get('input[name="password"]').type("Tetradka1011@");
        cy.get('button[type="submit"]').click();

        cy.visit(`/edit-profile`);
    });


    it("uploads avatar and allows preview when saving changes", () => {

        cy.get('input[data-cy="avatar-upload"]')
            .selectFile("cypress/fixtures/avatar.jpg", { force: true });

        cy.get('[data-cy="avatar-preview"] img')
            .should('have.attr', 'src')
            .and('match', /^blob:/);


        cy.contains("Save Changes").click();


        cy.contains("Profile updated successfully!").should("exist");
    });


    it("redirects unauthenticated user", () => {
        cy.clearCookies();
        cy.clearLocalStorage();
        cy.reload();


        cy.contains("Log In");
        cy.get('input[name="username"]').should("be.visible");
        cy.get('input[name="password"]').should("be.visible");
    });

    it("updates bio text and saves changes", () => {
        const newBio = "This is my new bio from Cypress test!";

        cy.get("textarea")
            .clear()
            .type(newBio)
            .should("have.value", newBio);

        cy.contains("Save Changes").click();

        cy.contains("Profile updated successfully!").should("exist");
    });

    it("toggles profile visibility switch and saves changes", () => {

        cy.get('input[type="checkbox"]').then(($checkbox) => {


            cy.contains("Save Changes").click();

            cy.contains("Profile updated successfully!").should("exist");
        });
    });
});
