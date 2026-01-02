describe("Weather Forecast Page", () => {
    const backendUrl = "http://localhost:8080";

    beforeEach(() => {
        cy.visit("/weather", {
            onBeforeLoad(win) {
                cy.stub(win.navigator.geolocation, "getCurrentPosition")
                    .callsFake((cb) => {
                        cb({
                            coords: {
                                latitude: 51.441642,
                                longitude: 5.46972,
                            },
                        });
                    });
            },
        });

        // City
        cy.intercept("GET", "**/map/coords*", {
            statusCode: 200,
            body: "Eindhoven",
        }).as("getCity");

        cy.intercept("GET", "**/weather/current*", {
            statusCode: 200,
            body: {
                temperature: 3.6,
                humidity: 68,
                feelsLike: -2.2,
                precipitation: 0,
                cloudCover: 100,
                windSpeed: 23.8,
                windDirection: "W",
                condition: "Cloudy",
            },
        }).as("getCurrentWeather");

        cy.intercept("GET", "**/weather/hourly*", {
            statusCode: 200,
            body: {
                time: ["2026-01-02T12:00", "2026-01-02T13:00"],
                temperature: [3.6, 3.5],
                weatherCode: ["Sunny", "Fog"],
                uvIndex: [1, 3],
                precipitation: [0, 0],
            },
        }).as("getHourly");


        cy.intercept("GET", "**/weather/daily*", {
            statusCode: 200,
            body: {
                time: ["2026-01-02", "2026-01-03"],
                maxTemperature: [4.0, 6.0],
                minTemperature: [-2.0, 0.0],
                weatherCode: ["Rain", "Snow"],
                precipitation: [0, 5],
            },
        }).as("getDaily");
    });


    it("get current city by coordinates", () => {
        cy.intercept(
            "GET",
            "**/map/coords*",
            {
                statusCode: 200,
                body: ["Eindhoven" ],
            }
        ).as("getCity");

        cy.visit("/weather");

        cy.wait("@getCity");
        cy.contains("Eindhoven").should("exist");
    });

    it("renders current city and weather info", () => {
        cy.wait("@getCity");
        cy.wait("@getCurrentWeather");

        cy.get('[data-testid="weather-icon-cloudy"]').should("exist");

        cy.contains("Eindhoven").should("exist");
        cy.contains("3.6°C").should("exist");
        cy.contains("Cloudy").should("exist");
        cy.contains("68 %").should("exist");
        cy.contains("100 %").should("exist");
        cy.contains("W").should("exist");
        cy.contains("Feels like -2.2°C").should("exist");
        cy.contains("0 mm").should("exist");
    });

    it("shows hourly weather correctly", () => {
        cy.clock(new Date('2026-01-02T12:00:00').getTime());

        cy.wait("@getHourly");

        cy.contains("Next 8 Hours").should("exist");
        cy.contains("Now").should("exist");
        cy.contains("13:00").should("exist");
        cy.contains("3.6°C").should("exist");
        cy.get('[data-testid="weather-icon-sunny"]').should("exist");
    });

    it("shows daily forecast after clicking 16-Day tab", () => {
        cy.wait("@getDaily");

        cy.contains("16-Day").click();

        cy.contains("16-Day Forecast").should("exist");

        cy.contains("Fri, Jan 2").should("exist");
        cy.contains("4°C / -2°C").should("exist");
        cy.contains("0% rain").should("exist");

        cy.get('[data-testid="weather-icon-cloudy"]').should("exist");
        cy.get('[data-testid="weather-icon-snowflake"]').should("exist");

        cy.contains("Sat, Jan 3").should("exist");
        cy.contains("6°C / 0°C").should("exist");
        cy.contains("5% rain").should("exist");
    });
});
