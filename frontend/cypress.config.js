import { defineConfig } from 'cypress'

export default defineConfig({
    e2e: {
        baseUrl: 'http://localhost:5173',

        supportFile: 'cypress/support/e2e.{js,jsx,ts,tsx}',
        video: true,
        screenshotOnRunFailure: true,

        setupNodeEvents(on, config) {
            on('before:browser:launch', (browser = {}, launchOptions) => {
                if (browser.name === 'chrome' && browser.isHeadless) {
                    launchOptions.args.push('--disable-gpu')
                    launchOptions.args.push('--no-sandbox')
                    launchOptions.args.push('--disable-dev-shm-usage')
                }
                return launchOptions
            })
        },
    },
})
