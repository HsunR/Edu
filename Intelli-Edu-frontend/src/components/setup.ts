import type { App } from 'vue'
import Pagination from './Pagination/index.vue'
import RightToolbar from './RightToolbar/index.vue'
import Editor from './Editor/index.vue'
import FileUpload from './FileUpload/index.vue'
import ImageUpload from './ImageUpload/index.vue'
import ImagePreview from './ImagePreview/index.vue'
import SvgIcon from './SvgIcon/index.vue'

export function setupGlobalComponents(app: App) {
  app.component('Pagination', Pagination)
  app.component('RightToolbar', RightToolbar)
  app.component('Editor', Editor)
  app.component('FileUpload', FileUpload)
  app.component('ImageUpload', ImageUpload)
  app.component('ImagePreview', ImagePreview)
  app.component('svg-icon', SvgIcon)
}
