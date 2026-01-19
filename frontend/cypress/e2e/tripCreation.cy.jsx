describe('TripPlanning', () => {
    before(() => {
        cy.visit('/login');
        cy.get('input[name="username"]').type("admin");
        cy.get('input[name="password"]').type("Tetradka1011@");
        cy.get('button[type="submit"]').click();

    });

    it('creates a new trip end-to-end', () => {
        cy.visit('/tripPlanning');

        cy.contains('Create New Trip').click();

        cy.get('input[placeholder="Enter trip title..."]').type('My new Test Trip');

        cy.get('input[placeholder="Type at least 2 characters..."]').type('Co');
        cy.contains('Coastal Breeze Path').click();


        cy.get('.mantine-DateTimePicker-input').click();
        cy.get('.mantine-DateTimePicker-calendarHeader').click();
        cy.get('.mantine-DateTimePicker-calendarHeader').click();
        cy.get('.mantine-DateTimePicker-calendarHeaderControl[data-direction="next"]').click()
        cy.get('.mantine-DateTimePicker-calendarHeaderControl[data-direction="next"]').click()
        cy.get('.mantine-DateTimePicker-calendarHeaderControl[data-direction="next"]').click()
        cy.get('.mantine-DateTimePicker-calendarHeaderControl[data-direction="next"]').click()
        cy.get('.mantine-DateTimePicker-yearsListCell').contains('2069').click();
        cy.get('.mantine-DateTimePicker-monthsListCell').contains('Dec').click();
        cy.get('.mantine-DateTimePicker-day').contains('31').click();
        cy.get('.mantine-DateTimePicker-input').click();

        cy.get('input[type="checkbox"]').check();

        cy.get('textarea[placeholder="Describe your trip..."]').type('Test description description again');

        cy.contains('Create Trip').click();


    });

    it('opens TripCreation with selected route and creates trip', () => {
        cy.visit('/routeDiscovery');

        const routeNameToAdd = 'Scenic Mountain Trail';

        cy.contains(routeNameToAdd)
            .parents('[data-slot="card"]')
            .within(() => {
                cy.contains('Add to Trip').click();
            });

        cy.get('.mantine-Modal-root').should('be.visible');

        cy.get('input[placeholder="Type at least 2 characters..."]')
            .should('have.value', routeNameToAdd)
            .and('be.disabled');

        cy.get('input[placeholder="Enter trip title..."]').type('Trip with Selected Route');

        cy.get('.mantine-DateTimePicker-input').click();
        cy.get('.mantine-DateTimePicker-calendarHeader').click();
        cy.get('.mantine-DateTimePicker-calendarHeader').click();
        cy.get('.mantine-DateTimePicker-calendarHeaderControl[data-direction="next"]').click()
        cy.get('.mantine-DateTimePicker-calendarHeaderControl[data-direction="next"]').click()
        cy.get('.mantine-DateTimePicker-calendarHeaderControl[data-direction="next"]').click()
        cy.get('.mantine-DateTimePicker-calendarHeaderControl[data-direction="next"]').click()
        cy.get('.mantine-DateTimePicker-yearsListCell').contains('2069').click();
        cy.get('.mantine-DateTimePicker-monthsListCell').contains('Dec').click();
        cy.get('.mantine-DateTimePicker-day').contains('31').click();
        cy.get('.mantine-DateTimePicker-input').click();

        cy.get('input[type="checkbox"]').check();

        cy.get('textarea[placeholder="Describe your trip..."]').type('Description for trip with selected route.');

        cy.contains('Create Trip').click();

    });
});
