describe("Profile Page (Authenticated User)", () => {
    const backendUrl = "http://localhost:8080";

    beforeEach(() => {
        cy.window().then(win => {
            const fakeJwt =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJpZCI6MSwidXNlcm5hbWUiOiJhZG1pbiJ9." +
                "dummy";

            win.localStorage.setItem("accessToken", fakeJwt);
            win.localStorage.setItem(
                "user",
                JSON.stringify({ id: 1, username: "admin" })
            );
        });
    });


    it("loads and displays own profile correctly", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, {
            statusCode: 200,
            body: {
                id: 1,
                username: "admin",
                email: "admin@example.com",
                birthDate: "2005-11-10"
            }
        }).as("fetchProfile");

        cy.visit("/profile/1");

        cy.wait("@fetchProfile");

        cy.contains("admin").should("exist");
        cy.contains("admin@example.com").should("exist");
        cy.contains("Your Profile").should("exist");
    });

    it("shows loading skeleton while fetching profile", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, (req) => {
            req.on("response", (res) => res.setDelay(1000));
        }).as("fetchProfile");

        cy.visit("/profile/1");

        cy.get(".mantine-Skeleton-root").should("exist");
    });

    it("loads and displays own profile correctly", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, {
            statusCode: 200,
            body: {
                id: 1,
                username: "admin",
                email: "admin@example.com",
                birthDate: "2005-11-10",
            },
        }).as("fetchProfile");

        cy.visit("/profile/1");
        cy.wait("@fetchProfile");

        cy.contains("admin").should("exist");
        cy.contains("Your Profile").should("exist");
        cy.contains("admin@example.com").should("exist");
        cy.contains("November 10, 2005").should("exist");
    });

    it("does not show email on another user's profile", () => {
        cy.intercept("GET", `${backendUrl}/users/2`, {
            statusCode: 200,
            body: {
                id: 2,
                username: "otherUser",
                email: "private@mail.com",
                birthDate: "2000-01-01",
            },
        }).as("fetchProfile");

        cy.visit("/profile/2");
        cy.wait("@fetchProfile");

        cy.contains("otherUser").should("exist");
        cy.contains("Email").should("not.exist");
    });

    it("shows birth date centered when it is the only field", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, {
            statusCode: 200,
            body: {
                id: 1,
                username: "admin",
                email: null,
                birthDate: "2005-11-10",
            },
        }).as("fetchProfile");

        cy.visit("/profile/1");
        cy.wait("@fetchProfile");

        cy.contains("Birth Date").should("exist");
        cy.contains("admin@example.com").should("not.exist");

        cy.get(".grid").should("have.class", "grid-cols-1");
    });

    it("shows 'Profile Not Found' when server returns 404", () => {
        cy.intercept("GET", `${backendUrl}/users/99`, {
            statusCode: 404,
            body: { error: "User not found" },
        }).as("fetchProfile");

        cy.visit("/profile/99");
        cy.wait("@fetchProfile");

        cy.contains("Profile Not Found").should("exist");
    });

    it("shows not authenticated alert if user is not logged in", () => {
        localStorage.clear();

        cy.visit("/profile");

        cy.contains("Please log in to view profiles.").should("exist");
    });
});