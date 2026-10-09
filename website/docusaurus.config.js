// @ts-check
const {themes: prismThemes} = require('prism-react-renderer');

const REPO = 'https://github.com/ElectricPaperInformationssystemeGmbH/ows-smartlink-beispiele';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'uniWAHL OWS SmartLink',
  tagline: 'Externe Authentifizierung für die uniWAHL OWS-Wahlkabine',
  url: 'https://electricpaperinformationssystemegmbh.github.io',
  baseUrl: '/ows-smartlink-beispiele/',
  organizationName: 'ElectricPaperInformationssystemeGmbH',
  projectName: 'ows-smartlink-beispiele',
  trailingSlash: false,
  onBrokenLinks: 'throw',
  markdown: {
    format: 'detect',
    hooks: {onBrokenMarkdownLinks: 'throw'},
  },
  i18n: {defaultLocale: 'de', locales: ['de']},
  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          routeBasePath: '/',
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: `${REPO}/blob/main/website/`,
        },
        blog: false,
        theme: {customCss: require.resolve('./src/css/custom.css')},
      }),
    ],
  ],
  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'uniWAHL OWS SmartLink',
        items: [
          {type: 'docSidebar', sidebarId: 'doku', position: 'left', label: 'Dokumentation'},
          {href: REPO, label: 'GitHub', position: 'right'},
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'SmartLink',
            items: [
              {label: 'Zweite Generation (GEN2)', to: '/gen2'},
              {label: 'Referenzimplementierungen', to: '/gen2/referenzimplementierungen'},
            ],
          },
          {
            title: 'Quelltext',
            items: [{label: 'GitHub', href: REPO}],
          },
        ],
        copyright: `© ${new Date().getFullYear()} Electric Paper Informationssysteme GmbH. Beispielcode unter MIT No Attribution.`,
      },
      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
        additionalLanguages: ['java', 'rust', 'php', 'go', 'bash'],
      },
    }),
};

module.exports = config;
