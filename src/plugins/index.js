import auth from './auth'
import download from './download'

export default function installPlugins(app){
  // 认证对象
  app.config.globalProperties.$auth = auth
  // 下载文件
  app.config.globalProperties.$download = download
}
