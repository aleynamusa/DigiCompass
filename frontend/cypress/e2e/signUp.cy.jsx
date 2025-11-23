describe("Sign Up Flow", () => {
    const backendUrl = "http://localhost:8080";
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
        cy.intercept("POST", `${backendUrl}/auth/signUp`).as("signUpRequest");

        cy.get('input[name="email"]').type("trying@gmail.com");
        cy.get('input[name="username"]').type("hyttryest");
        cy.get('input[name="birthDate"]').click();

        //birthdate selection
        cy.get('.mantine-Calendar-calendarHeaderLevel').click();
        cy.get('.mantine-Calendar-calendarHeaderLevel').click();
        cy.get('.mantine-Calendar-calendarHeaderControlIcon[data-direction="previous"]').click()
        cy.get('.mantine-Calendar-calendarHeaderControlIcon[data-direction="previous"]').click()
        cy.get('.mantine-Calendar-yearsListCell').contains('2005').click();
        cy.get('.mantine-Calendar-monthsListCell').contains('Nov').click();
        cy.get('.mantine-Calendar-day').contains('10').click();


        cy.get('input[name="password"]').type("Hello123@_");
        cy.get('input[name="confirmPassword"]').type("Hello123@_");

        cy.get('button[type="submit"]').click();

        cy.wait("@signUpRequest").its("response.statusCode").should("eq", 200);


    });

    it("should show if the username is already taken",()=>{
        cy.intercept("POST", `${backendUrl}/auth/signUp`).as("signUpRequest");

        cy.get('input[name="username"]').type("hyttryest");

        cy.contains("Username already taken");

    });

    it("should show if the email is already registered",()=>{

        cy.get('input[name="email"]').type("trying@gmail.com");

        cy.contains("Email is already registered in our system");
    })

    it("should show if the the age is eligible to register",()=>{
        cy.intercept("POST", `${backendUrl}/auth/signUp`).as("signUpRequest");

        cy.get('input[name="birthDate"]').click();
        cy.get('.mantine-Calendar-calendarHeaderLevel').click();
        cy.get('.mantine-Calendar-calendarHeaderLevel').click();

        cy.get('.mantine-Calendar-yearsListCell').contains('2025').click();
        cy.get('.mantine-Calendar-monthsListCell').contains('Nov').click();
        cy.get('.mantine-Calendar-day').contains('10').click();

        cy.contains("You must be at least 14 years old");
    })

    it("should show if passwords do not match",()=>{
        cy.intercept("POST", `${backendUrl}/auth/signUp`).as("signUpRequest");

        cy.get('input[name="password"]').type("helooo907T");
        cy.get('input[name="confirmPassword"]').type("heooo907T");

        cy.contains("Passwords do not match");
    })
});


