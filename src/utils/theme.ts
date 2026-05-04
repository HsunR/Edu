export function hexToRgb(hex: string): { r: number; g: number; b: number } | null {
  const match = hex.replace('#', '').match(/.{2}/g)
  if (!match || match.length < 3) return null
  return {
    r: parseInt(match[0], 16),
    g: parseInt(match[1], 16),
    b: parseInt(match[2], 16),
  }
}

export function rgbToHex(r: number, g: number, b: number): string {
  return '#' + [r, g, b].map(x => x.toString(16).padStart(2, '0')).join('')
}

export function lighten(hex: string, amount: number): string {
  const rgb = hexToRgb(hex)
  if (!rgb) return hex
  return rgbToHex(
    Math.min(255, rgb.r + amount),
    Math.min(255, rgb.g + amount),
    Math.min(255, rgb.b + amount)
  )
}

export function darken(hex: string, amount: number): string {
  const rgb = hexToRgb(hex)
  if (!rgb) return hex
  return rgbToHex(
    Math.max(0, rgb.r - amount),
    Math.max(0, rgb.g - amount),
    Math.max(0, rgb.b - amount)
  )
}

export function handleThemeStyle(theme: string): void {
  document.documentElement.style.setProperty('--el-color-primary', theme)
  for (let i = 1; i <= 9; i++) {
    const rgb = hexToRgb(theme)
    if (!rgb) continue
    const lightLevel = i / 10
    const lightR = Math.floor((255 - rgb.r) * lightLevel + rgb.r)
    const lightG = Math.floor((255 - rgb.g) * lightLevel + rgb.g)
    const lightB = Math.floor((255 - rgb.b) * lightLevel + rgb.b)
    document.documentElement.style.setProperty(
      `--el-color-primary-light-${i}`,
      rgbToHex(lightR, lightG, lightB)
    )
    const darkR = Math.floor(rgb.r * (1 - lightLevel))
    const darkG = Math.floor(rgb.g * (1 - lightLevel))
    const darkB = Math.floor(rgb.b * (1 - lightLevel))
    document.documentElement.style.setProperty(
      `--el-color-primary-dark-${i}`,
      rgbToHex(darkR, darkG, darkB)
    )
  }
}
