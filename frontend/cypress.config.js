
import { defineConfig } from 'cypress'

export default defineConfig({
    e2e: {
        baseUrl: 'http://localhost:5173',

        supportFile: 'cypress/support/e2e.{js,jsx,ts,tsx}',
        video: true,
        screenshotOnRunFailure: true,
    },

})
