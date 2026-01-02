describe("Weather Forecast Page - Error Handling", () => {
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
    });

    describe("CurrentInfo Error Handling", () => {
        it("handles city API failure gracefully", () => {
            cy.intercept("GET", "**/map/coords*", {
                statusCode: 500,
                body: { error: "Internal Server Error" },
            }).as("getCityError");

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

            cy.wait("@getCityError");
            cy.wait("@getCurrentWeather");

            cy.contains("Unknown location").should("exist");

            cy.contains("3.6°C").should("exist");
        });


        it("handles current weather API failure", () => {
            cy.intercept("GET", "**/map/coords*", {
                statusCode: 200,
                body: "Eindhoven",
            }).as("getCity");

            cy.intercept("GET", "**/weather/current*", {
                statusCode: 500,
                body: { error: "Service unavailable" }
            }).as("getCurrentWeatherError");

            cy.wait("@getCity");
            cy.wait("@getCurrentWeatherError");

            // Should show error message
            cy.contains("Failed to load weather data").should("exist");
            cy.contains("Please try again later").should("exist");
        });

    });

    describe("Hourly Weather Error Handling", () => {
        beforeEach(() => {
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

        it("handles hourly weather API failure", () => {
            cy.intercept("GET", "**/weather/hourly*", {
                statusCode: 500,
                body: { error: "Internal Server Error" }
            }).as("getHourlyError");

            cy.wait("@getHourlyError");
            cy.wait("@getDaily");

            // Should show error message
            cy.contains("Failed to load hourly weather data").should("exist");
            cy.contains("Please try again later").should("exist");
        });

        it("handles malformed hourly weather response", () => {
            cy.intercept("GET", "**/weather/hourly*", {
                statusCode: 200,
                body: {
                    // Missing required fields
                    time: ["2026-01-02T12:00"],
                    // temperature is missing
                }
            }).as("getHourlyMalformed");

            cy.wait("@getHourlyMalformed");

            // Should show error or handle gracefully
            cy.contains("Failed to load hourly weather data").should("exist");
        });

        it("handles empty hourly weather data", () => {
            cy.intercept("GET", "**/weather/hourly*", {
                statusCode: 200,
                body: {
                    time: [],
                    temperature: [],
                    weatherCode: [],
                    uvIndex: [],
                    precipitation: [],
                }
            }).as("getHourlyEmpty");

            cy.wait("@getHourlyEmpty");
            cy.wait("@getDaily");

            // Should show empty state or error
            cy.contains("Next 8 Hours").should("not.exist");
        });
    });

    describe("Daily Weather Error Handling", () => {
        beforeEach(() => {
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

            cy.clock(new Date('2026-01-02T12:00:00').getTime());

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
        });

        it("handles daily weather API failure", () => {
            cy.intercept("GET", "**/weather/daily*", {
                statusCode: 500,
                body: { error: "Service unavailable" }
            }).as("getDailyError");

            cy.wait("@getDailyError");
            cy.wait("@getHourly");

            cy.contains("16-Day").click();

            // Should show error message
            cy.contains("Failed to load daily weather data").should("exist");
            cy.contains("Please try again later").should("exist");
        });

        it("handles network error for daily weather", () => {
            cy.intercept("GET", "**/weather/daily*", {
                forceNetworkError: true
            }).as("getDailyNetworkError");

            cy.wait("@getDailyNetworkError");

            cy.contains("16-Day").click();
            cy.contains("Failed to load daily weather data").should("exist");
        });
    });

    describe("Recommended Days Error Handling", () => {
        beforeEach(() => {
            // Setup successful responses for other components
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

            cy.clock(new Date('2026-01-02T12:00:00').getTime());

            cy.intercept("GET", "**/weather/hourly*", {
                statusCode: 200,
                body: {
                    time: ["2026-01-02T12:00"],
                    temperature: [3.6],
                    weatherCode: ["Sunny"],
                    uvIndex: [1],
                    precipitation: [0],
                },
            }).as("getHourly");

            cy.intercept("GET", "**/weather/daily*", {
                statusCode: 200,
                body: {
                    time: ["2026-01-02"],
                    maxTemperature: [4.0],
                    minTemperature: [-2.0],
                    weatherCode: ["Rain"],
                    precipitation: [0],
                },
            }).as("getDaily");
        });
    });
});


