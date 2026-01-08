import { defineConfig } from 'cypress'

export default defineConfig({
    e2e: {
        baseUrl: 'http://localhost:5173',
        supportFile: 'cypress/support/e2e.{js,jsx,ts,tsx}',

        // Video and screenshots
        video: true,
        screenshotOnRunFailure: true,

        // Timeouts - increase these for stability
        defaultCommandTimeout: 10000,
        requestTimeout: 10000,
        responseTimeout: 10000,
        pageLoadTimeout: 60000,

        // Retries - retry failed tests automatically
        retries: {
            runMode: 2,      // retry twice when running headlessly
            openMode: 0      // don't retry in interactive mode
        },

        // Viewport
        viewportWidth: 1280,
        viewportHeight: 720,

        // Better error handling
        watchForFileChanges: true,

        // Setup node events
        setupNodeEvents(on, config) {
            // Print helpful error messages
            on('before:run', async (details) => {
                console.log('🚀 Starting Cypress tests...')
                console.log('📍 Base URL:', config.baseUrl)

                // Check if frontend is running
                try {
                    const response = await fetch(config.baseUrl)
                    if (!response.ok) {
                        console.error('⚠️  Frontend server returned status:', response.status)
                    } else {
                        console.log('✅ Frontend server is running')
                    }
                } catch (error) {
                    console.error('❌ FRONTEND SERVER IS NOT RUNNING!')
                    console.error('   Please start your dev server with: npm run dev')
                    console.error('   Error:', error.message)
                }

                // Check if backend is running
                try {
                    const backendResponse = await fetch('http://localhost:8080/actuator/health')
                    if (backendResponse.ok) {
                        console.log('✅ Backend server is running')
                    }
                } catch (error) {
                    console.error('⚠️  Backend server might not be running')
                    console.error('   Please start your backend server')
                }
            })

            on('after:run', (results) => {
                console.log('📊 Test Results:')
                console.log(`   Total: ${results.totalTests}`)
                console.log(`   Passed: ${results.totalPassed}`)
                console.log(`   Failed: ${results.totalFailed}`)
            })

            return config
        }
    },
})