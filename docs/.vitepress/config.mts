import { defineConfig } from "vitepress";

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "Lifestealer",
  description: "A documentation website for the Lifestealer plugin.",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    logo: "/logo.png",
    nav: [
      { text: "Home", link: "/" },
      { text: "Documentation", link: "/intro/welcome" },
      { text: "Purchase", link: "/intro/purchasing" },
      { text: "API", link: "/developer/reference"}
    ],

    search: {
      provider: 'local'
    },

    lastUpdated: {
      text: 'Updated at',
      formatOptions: {
        dateStyle: 'short',
      }
    },

    sidebar: [
      {
        text: "Introduction",
        items: [
          { text: "Welcome to Lifestealer Docs", link: "/intro/welcome" },
          { text: "Purchasing", link: "/intro/purchasing" },
          { text: "Getting Started", link: "/intro/getting-started" },
          { text: "Changelog", link: "/intro/changelog" },
        ],
      },
      {
        text: "Usage",
        items: [
          { text: "Overview", link: "/usage/overview" },
          { text: "Rules", link: "/usage/rules" },
          { text: "Commands", link: "/usage/commands" },
          { text: "Terminology", link: "/usage/terminology" },
          { text: "Adding custom textures", link: "/usage/texturing" },
          { text: "Migrating Storage Type", link: "/usage/migrating-to-a-new-storage-type" },
          { text: "Moving from Similar Plugins", link: "/usage/moving-from-similar-plugins" },
          { text: "Backups", link: "/usage/backups" },
        ],
      },
      {
        text: "Configuration",
        items: [
          { text: "Overview", link: "/configuration/overview" },
          { text: "Storage Options", link: "/configuration/storage" },
          { text: "Heart Items", link: "/configuration/items" },
          { text: "User Rules", link: "/configuration/rules" },
          { text: "Drop Restrictions", link: "/configuration/restrictions" },
          { text: "Duration Format", link: "/configuration/duration" },
          { text: "Ban System", link: "/configuration/ban" },
          { text: "Messages", link: "/configuration/messages" },
        ],
      },
      {
        text: "Integrations",
        items: [{ text: "PlaceholderAPI", link: "/integrations/papi" }],
      },
      {
        text: "Developer Reference",
        items: [
          { text: "API Reference", link: "/developer/reference" },
          { text: "Hooking", link: "/developer/hooking" },
          { text: "Events", link: "/developer/events" },
        ],
      },
    ],

    socialLinks: [
      { icon: "github", link: "https://github.com/chicoferreira/lifestealer" },
    ],

    footer: {
      copyright: 'Copyright © 2025-present <a href="https://github.com/chicoferreira">chicoferreira</a>'
    }
  },
});
