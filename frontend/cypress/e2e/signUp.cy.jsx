describe("Sign Up Flow", () => {
    const frontendUrl = "http://localhost:5173";

    beforeEach(() => {
        cy.visit(`${frontendUrl}/signup`);
    });

    it("should show sign up form", () => {
        cy.contains("Sign Up");
        cy.get('input[name="email"]').should("be.visible");
        cy.get('input[name="username"]').should("be.visible");
        cy.get('input[name="birthDate"]').should("be.visible")
        cy.get('input[name="password"]').should("be.visible");
        cy.get('input[name="confirmPassword"]').should("be.visible");
    });

    it("should sign up successfully when given correct inputs", () => {

        cy.get('input[name="email"]').type("johndoe@gmail.com");
        cy.get('input[name="username"]').type("johndoe123");
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

    });


    it("should show if the email is already registered",()=>{

        cy.get('input[name="email"]').type("johndoe@gmail.com");

        cy.contains("Email is already registered in our system");
    })

    it("should show if the the age is eligible to register",()=>{

        cy.get('input[name="birthDate"]').click();
        cy.get('.mantine-Calendar-calendarHeaderLevel').click();
        cy.get('.mantine-Calendar-calendarHeaderLevel').click();

        cy.get('.mantine-Calendar-yearsListCell').contains('2025').click();
        cy.get('.mantine-Calendar-monthsListCell').contains('Nov').click();
        cy.get('.mantine-Calendar-day').contains('10').click();

        cy.contains("You must be at least 14 years old");
    })

    it("should show if passwords do not match",()=>{

        cy.get('input[name="password"]').type("helooo907T");
        cy.get('input[name="confirmPassword"]').type("heooo907T");

        cy.contains("Passwords do not match");
    })

    it("should show if the username is already taken",()=>{

        cy.get('input[name="username"]').type("johndoe123");

        cy.contains("Username already taken");

    });
});


