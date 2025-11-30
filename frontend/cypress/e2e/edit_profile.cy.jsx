describe("Edit Profile Page", () => {
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

    it("loads current profile and displays avatar preview", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, {
            statusCode: 200,
            body: {
                id: 1,
                username: "admin",
                email: "admin@example.com",
                birthDate: "2005-11-10",
                imageUrl: "https://example.com/avatar.jpg"
            }
        }).as("fetchProfile");

        cy.visit("/edit-profile");
        cy.wait("@fetchProfile");

        cy.get("img")                  // Avatar component renders as img tag
            .should("have.attr", "src", "https://example.com/avatar.jpg");

        cy.contains("Edit Profile").should("exist");
    });

    it("allows uploading a new profile picture and previews it", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, {
            statusCode: 200,
            body: { id: 1, imageUrl: null }
        }).as("fetchProfile");

        cy.visit("/edit-profile");
        cy.wait("@fetchProfile");

        // Upload file
        cy.get("input[type='file']").selectFile("cypress/fixtures/avatar.jpg", {
            force: true,
        });

        // Avatar preview updated
        cy.get("img").should("have.attr", "src").and("include", "blob:");
    });

    it("uploads avatar when saving changes", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, {
            statusCode: 200,
            body: { id: 1, imageUrl: null }
        }).as("fetchProfile");

        cy.intercept(
            "POST",
            `${backendUrl}/users/profilePictureUpdate/1`,
            {
                statusCode: 200,
                body: { message: "Upload OK" },
            }
        ).as("uploadAvatar");


        cy.visit("/edit-profile");
        cy.wait("@fetchProfile");

        // Upload new file
        cy.get("input[type='file']").selectFile("cypress/fixtures/avatar.jpg", {
            force: true,
        });

        cy.contains("Save Changes").click();


        cy.contains("Profile picture updated successfully!").should("exist");
    });

    it("shows an error if the upload fails", () => {
        cy.intercept("GET", `${backendUrl}/users/1`, {
            statusCode: 200,
            body: { id: 1 },
        }).as("fetchProfile");

        cy.intercept("POST", `${backendUrl}/users/1/avatar`, {
            statusCode: 500,
            body: { error: "Upload failed" }
        }).as("uploadAvatar");

        cy.visit("/edit-profile");
        cy.wait("@fetchProfile");

        cy.get("input[type='file']").selectFile(
            "cypress/fixtures/avatar.jpg",
            { force: true }
        );

        cy.contains("Save Changes").click();

        cy.contains("Could not update profile.").should("exist");
    });

    it("redirects unauthenticated user", () => {
        cy.window().then(win => {
            win.localStorage.clear();
        });

        cy.visit("/edit-profile");

        cy.contains("Not Authenticated").should("exist");
    });
});
