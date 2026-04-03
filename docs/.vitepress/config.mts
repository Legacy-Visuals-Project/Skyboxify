import { defineConfig } from 'vitepress'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "Skyboxify Documentation",
  description: "Documentation/Specification on the skyboxify skybox pack format & more!",

  base: "/website",

  themeConfig: {
    nav: [{ text: 'Home', link: '/' }],

    sidebar: [],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/Legacy-Visuals-Project/Skyboxify/' }
    ]
  }
})
