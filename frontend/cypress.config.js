
import { defineConfig } from 'cypress'

export default defineConfig({
    e2e: {
        // baseUrl: 'http://localhost:5173',
        browser: 'chrome', // Set the browser here
        supportFile: 'cypress/support/e2e.{js,jsx,ts,tsx}',
    },
})
