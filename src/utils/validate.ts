const EMAIL_REGEX = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
const MOBILE_REGEX = /^1[3-9]\d{9}$/
const URL_REGEX = /^https?:\/\/.*/

export function isEmpty(value: unknown): boolean {
  if (value === null || value === undefined) return true
  if (typeof value === 'string') return value.trim().length === 0
  if (Array.isArray(value)) return value.length === 0
  if (typeof value === 'object') return Object.keys(value).length === 0
  return false
}

export function isEmail(value: string): boolean {
  return EMAIL_REGEX.test(value)
}

export function isMobile(value: string): boolean {
  return MOBILE_REGEX.test(value)
}

export function isUrl(value: string): boolean {
  return URL_REGEX.test(value)
}

export function isNumber(value: unknown): boolean {
  return typeof value === 'number' && !isNaN(value)
}

export function isExternal(path: string): boolean {
  return /^(https?:|mailto:|tel:)/.test(path)
}

export function isHttp(url: string): boolean {
  return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1
}
