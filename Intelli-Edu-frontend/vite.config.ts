import { defineConfig, loadEnv } from 'vite'
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import vue from '@vitejs/plugin-vue'
import setupExtend from 'unplugin-vue-setup-extend-plus/vite'
import compression from 'vite-plugin-compression'

export default defineConfig(({ mode, command }) => {
  const env = loadEnv(mode, process.cwd())
  const isBuild = command === 'build'

  const plugins = [
    vue(),
    setupExtend({}),
    createSvgIconsPlugin({
      iconDirs: [path.resolve(process.cwd(), 'src/assets/icons/svg')],
      symbolId: 'icon-[dir]-[name]',
      svgoOptions: isBuild,
    }),
    AutoImport({
      imports: ['vue', 'vue-router', 'pinia'],
      resolvers: [ElementPlusResolver()],
      dts: 'src/types/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/types/components.d.ts',
    }),
  ]

  if (isBuild) {
    const { VITE_BUILD_COMPRESS } = env
    if (VITE_BUILD_COMPRESS) {
      const compressList = VITE_BUILD_COMPRESS.split(',')
      if (compressList.includes('gzip')) {
        plugins.push(compression({ ext: '.gz', deleteOriginFile: false }))
      }
      if (compressList.includes('brotli')) {
        plugins.push(compression({ ext: '.br', algorithm: 'brotliCompress', deleteOriginFile: false }))
      }
    }
  }

  return {
    base: '/',
    plugins,
    resolve: {
      alias: {
        '~': path.resolve(__dirname, './'),
        '@': path.resolve(__dirname, './src'),
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
    },
    build: {
      sourcemap: isBuild ? false : 'inline',
      outDir: 'dist',
      assetsDir: 'assets',
      chunkSizeWarningLimit: 2000,
      rollupOptions: {
        output: {
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
        },
      },
    },
    server: {
      port: 5173,
      host: true,
      open: env.VITE_OPEN_BROWSER !== 'false',
      proxy: {
        '/api': {
          target: env.VITE_PROXY_TARGET || 'http://localhost:8890',
          changeOrigin: true,
        },
      },
    },
  }
})
