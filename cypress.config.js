const { defineConfig } = require('cypress');

module.exports = defineConfig({
    e2e: {
        setupNodeEvents(on, config) {
            on('before:browser:launch', (browser = {}, launchOptions) => {
                if (browser.family === 'chromium') {
                    launchOptions.preferences = launchOptions.preferences || {};
                    launchOptions.preferences.profile = launchOptions.preferences.profile || {};
                    launchOptions.preferences.profile.default_content_setting_values =
                        launchOptions.preferences.profile.default_content_setting_values || {};
                    // 1 = allow, 2 = block
                    launchOptions.preferences.profile.default_content_setting_values.geolocation = 1;
                    return launchOptions;
                }
            });
        },
    },
});
