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
                JSON.stringify({ id: 1, username: "admin", role: 1 })
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
                birthDate: "2005-11-10",
                imageUrl: "avatar.jpg",
                bio: "hello Testing",
                isPublicProfile: false,
            }
        }).as("fetchProfile");

        cy.intercept("GET", `${backendUrl}/users/1/routes`, {
            statusCode: 200,
            body: []
        });

        cy.visit("/profile/1");
        cy.wait("@fetchProfile");

        cy.contains("admin").should("exist");
        cy.contains("admin@example.com").should("exist");
        cy.contains("November 10, 2005").should("exist");
        cy.contains("Bio").should("exist");
        cy.contains("hello Testing").should("exist");
    });

    it("shows loading state before profile loads", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, (req) => {
            req.on("response", res => res.setDelay(800));
            req.reply({
                statusCode: 200,
                body: {
                    id: 1,
                    username: "admin",
                }
            });
        }).as("fetchProfile");

        cy.visit("/profile/1");

        cy.contains("Loading...").should("exist");
    });

    it("does not show email on another user's profile", () => {
        cy.intercept("GET", `${backendUrl}/users/2`, {
            statusCode: 200,
            body: {
                id: 2,
                username: "otherUser",
                email: "other@example.com",
                birthDate: "2000-01-01",
                isPublicProfile: true
            }
        }).as("fetchProfile");

        cy.intercept("GET", `${backendUrl}/users/2/routes`, {
            statusCode: 200,
            body: []
        });

        cy.visit("/profile/2");
        cy.wait("@fetchProfile");

        cy.contains("otherUser").should("exist");
        cy.contains("Email").should("not.exist");
        cy.contains("other@example.com").should("not.exist");
    });

    it("shows birth date centered when it is the only field", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, {
            statusCode: 200,
            body: {
                id: 1,
                username: "admin",
                email: null,
                birthDate: "2005-11-10",
            }
        }).as("fetchProfile");

        cy.intercept("GET", `${backendUrl}/users/1/routes`, {
            statusCode: 200,
            body: []
        });

        cy.visit("/profile/1");
        cy.wait("@fetchProfile");

        cy.contains("Birth Date").should("exist");
        cy.contains("admin@example.com").should("not.exist");

        cy.get(".grid")
            .should("have.class", "grid-cols-1");
    });

    it("redirects unauthenticated user to login", () => {
        localStorage.clear();

        cy.visit("/profile");

        cy.url().should("include", "/login");
    });
});
