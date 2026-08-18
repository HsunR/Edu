export interface AppSettings {
  title: string
  sideTheme: string
  showSettings: boolean
  topNav: boolean
  tagsView: boolean
  tagsIcon: boolean
  fixedHeader: boolean
  sidebarLogo: boolean
  dynamicTitle: boolean
  footerVisible: boolean
  footerContent: string
}

const settings: AppSettings = {
  title: import.meta.env.VITE_APP_TITLE || '智慧教育平台',
  sideTheme: 'theme-dark',
  showSettings: true,
  topNav: false,
  tagsView: true,
  tagsIcon: false,
  fixedHeader: false,
  sidebarLogo: true,
  dynamicTitle: false,
  footerVisible: false,
  footerContent: 'Copyright © 2024-2026 Intelli-Edu. All Rights Reserved.',
}

export default settings
