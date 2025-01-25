import {defineConfig} from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "Lifestealer",
  description: "A documentation website for the Lifestealer plugin.",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      {text: 'Home', link: '/'},
      {text: 'Examples', link: '/markdown-examples'}
    ],

    sidebar: [
      {
        text: 'Introduction',
        items: [
          {text: 'Welcome to Lifestealer Docs', link: '/intro/welcome'},
          {text: 'Getting Started', link: '/intro/getting-started'}
        ]
      },
      {
        text: 'Configuration',
        items: [
          {text: 'Overview', link: '/configuration/overview'},
          {text: 'Heart Items', link: '/configuration/items'},
          {text: 'Storage Options', link: '/configuration/storage'},
          {text: 'User Rules', link: '/configuration/rules'},
          {text: 'Drop Restrictions', link: '/configuration/restrictions'},
          {text: 'Duration Format', link: '/configuration/duration'},
          {text: 'Ban System', link: '/configuration/ban'},
          {text: 'Messages', link: '/configuration/messages'},
        ]
      },
      {
        text: 'Usage',
        items: [
          {text: 'Overview', link: '/usage/overview'},
          {text: 'Commands', link: '/usage/commands'},
          {text: 'Permissions', link: '/usage/permissions'},
        ]
      },
      {
        text: 'Examples',
        items: [
          {text: 'Runtime API Examples', link: '/examples/api-examples'},
          {text: 'Markdown Examples', link: '/examples/markdown-examples'}
        ]
      },
      {
        text: 'Integrations',
        items: [
          {text: 'PlaceholderAPI', link: '/integrations/papi'},
        ]
      },
      {
        text: 'Developer Reference',
        items: [
          {text: 'API Reference', link: '/developer/api-reference'},
          {text: 'Hooking', link: '/developer/hooking'},
          {text: 'Events', link: '/developer/events'}
        ]
      }
    ],

    socialLinks: [
      {icon: 'github', link: 'https://github.com/chicoferreira/lifestealer'}
    ]
  }
})
