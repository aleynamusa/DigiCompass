import js from "@eslint/js";
import pluginReact from "eslint-plugin-react";
import pluginCypress from "eslint-plugin-cypress";
import globals from "globals";
import { defineConfig } from "eslint/config";

export default defineConfig([
    // React / Frontend files
    {
        files: ["**/*.{js,jsx}"],
        ignores: ["dist/", "node_modules/"],
        languageOptions: {
            ecmaVersion: "latest",
            sourceType: "module",
            globals: {
                ...globals.browser,
                ...globals.node
            },
            parserOptions: {
                ecmaFeatures: { jsx: true }
            }
        },
        plugins: { react: pluginReact },
        extends: [
            js.configs.recommended,
            pluginReact.configs.flat.recommended
        ],
        settings: {
            react: { version: "detect" }
        },
        rules: {
            "react/react-in-jsx-scope": "off",
            "react/prop-types": "off"
        }
    },

    // Cypress tests
    {
        files: ["cypress/**/*.{js,jsx}"],
        languageOptions: {
            globals: {
                ...globals.browser,
                cy: "readonly",
                Cypress: "readonly",
                describe: "readonly",
                it: "readonly",
                before: "readonly",
                beforeEach: "readonly",
                after: "readonly",
                afterEach: "readonly"
            }
        },
        plugins: {
            cypress: pluginCypress
        },
        extends: [
            pluginCypress.configs.recommended
        ]
    }

]);
