/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
module.exports = {
  doku: [
    'index',
    {
      type: 'category',
      label: 'Zweite Generation (GEN2)',
      collapsed: false,
      link: {type: 'doc', id: 'gen2/einleitung'},
      items: [
        'gen2/funktionsweise',
        'gen2/smartlink-url',
        'gen2/auth-token',
        'gen2/bereitstellung',
        'gen2/integration',
        'gen2/sicherheit',
        'gen2/test-fehlersuche',
        'gen2/umstieg-gen1',
        'gen2/referenzimplementierungen',
      ],
    },
  ],
};
