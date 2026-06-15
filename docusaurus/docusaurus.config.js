// @ts-check
import {themes as prismThemes} from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Future Kawa',
  tagline: 'IoT Smart Coffee Stock Management System',
  favicon: 'img/favicon.ico',

  future: {
    v4: true,
  },

  url: 'https://Altitude-0.github.io',
  baseUrl: '/Future-Kawa/',

  organizationName: 'Altitude-0',
  projectName: 'Future-Kawa',

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      ({
        docs: {
          sidebarPath: './sidebars.js',
          editUrl: 'https://github.com/Altitude-0/Future-Kawa',
        },
        blog: {
          showReadingTime: true,
          editUrl: 'https://github.com/Altitude-0/Future-Kawa',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      }),
    ],
  ],

  themeConfig:
    ({
      image: 'img/docusaurus-social-card.jpg',

      navbar: {
        title: 'Future Kawa',
        logo: {
          alt: 'Future Kawa Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'docSidebar',
            sidebarId: 'tutorialSidebar',
            position: 'left',
            label: 'Documentation',
          },
          {to: '/blog', label: 'Blog', position: 'left'},
          {
            href: 'https://github.com/Altitude-0/Future-Kawa',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },

      footer: {
        style: 'dark',
        links: [
          {
            title: 'Documentation',
            items: [
              {
                label: 'Intro',
                to: '/docs/intro',
              },
              {
                label: 'Architecture',
                to: '/docs/architecture',
              },
              {
                label: 'IoT & MQTT',
                to: '/docs/iot',
              },
            ],
          },
          {
            title: 'Project',
            items: [
              {
                label: 'GitHub Repository',
                href: 'https://github.com/Altitude-0/Future-Kawa',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Future Kawa`,
      },

      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
      },
    }),
};

export default config;