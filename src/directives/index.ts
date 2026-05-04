import type { App } from 'vue'
import { setupPermissionDirectives } from './permission'
import { setupCopyDirective } from './copy'

export function setupDirectives(app: App) {
  setupPermissionDirectives(app)
  setupCopyDirective(app)
}
