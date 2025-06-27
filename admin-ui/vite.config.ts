import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";
import { UserConfig, ConfigEnv, loadEnv, defineConfig } from "vite";

import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import Icons from "unplugin-icons/vite";
import IconsResolver from "unplugin-icons/resolver";

import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import mockDevServerPlugin from "vite-plugin-mock-dev-server";

import UnoCSS from "unocss/vite";
import { resolve } from "path";
import {
  name,
  version,
  engines,
  dependencies,
  devDependencies,
} from "./package.json";

/** @see https://devtools-next.vuejs.org */
import VueDevTools from "vite-plugin-vue-devtools";

/** 平台信息用于打包时注入环境变量 */
const __APP_INFO__ = {
  pkg: { name, version, engines, dependencies, devDependencies },
  buildTimestamp: Date.now(),
};

const pathSrc = resolve(__dirname, "src");

export default defineConfig(({ mode }: ConfigEnv): UserConfig => {
  const env = loadEnv(mode, process.cwd());

  return {
    resolve: {
      alias: {
        "@": pathSrc,
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          javascriptEnabled: true,
          additionalData: `
            @use "@/styles/variables.scss" as *;
          `,
        },
      },
    },
    server: {
      host: "0.0.0.0",
      port: Number(env.VITE_APP_PORT || 3000),
      open: true,
      proxy: {
        [env.VITE_APP_BASE_API]: {
          target: env.VITE_APP_API_URL,
          changeOrigin: true,
          rewrite: (path) =>
            path.replace(new RegExp("^" + env.VITE_APP_BASE_API), ""),
        },
        // 新增代理 /api 到后端服务
        "/api": {
          target: "http://localhost:8080",
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ""),
        },
      },
    },
    plugins: [
      vue(),
      vueJsx(),
      env.VITE_MOCK_DEV_SERVER === "true" ? mockDevServerPlugin() : null,
      UnoCSS({
        hmrTopLevelAwait: false,
      }),
      AutoImport({
        imports: ["vue", "@vueuse/core", "pinia", "vue-router", "vue-i18n"],
        resolvers: [
          ElementPlusResolver({ importStyle: "sass" }),
          IconsResolver({}),
        ],
        eslintrc: {
          enabled: false,
          filepath: "./.eslintrc-auto-import.json",
          globalsPropValue: true,
        },
        vueTemplate: true,
        dts: false,
      }),
      Components({
        resolvers: [
          ElementPlusResolver({ importStyle: "sass" }),
          IconsResolver({
            enabledCollections: ["ep"],
          }),
        ],
        dirs: ["src/components", "src/**/components"],
        dts: false,
      }),
      Icons({
        autoInstall: true,
      }),
      createSvgIconsPlugin({
        iconDirs: [resolve(pathSrc, "assets/icons")],
        symbolId: "icon-[dir]-[name]",
      }),
      VueDevTools({
        openInEditorHost: `http://localhost:${env.VITE_APP_PORT || 3000}`,
      }),
    ],
    optimizeDeps: {
      include: [
        "vue",
        "vue-router",
        "pinia",
        "axios",
        "@vueuse/core",
        "path-to-regexp",
        "vue-i18n",
        "path-browserify",
        "element-plus/es/components/form/style/index",
        "element-plus/es/components/form-item/style/index",
        "element-plus/es/components/button/style/index",
        "element-plus/es/components/input/style/index",
        "element-plus/es/components/input-number/style/index",
        "element-plus/es/components/switch/style/index",
        "element-plus/es/components/upload/style/index",
        "element-plus/es/components/menu/style/index",
        "element-plus/es/components/col/style/index",
        "element-plus/es/components/icon/style/index",
        "element-plus/es/components/row/style/index",
        "element-plus/es/components/tag/style/index",
        "element-plus/es/components/dialog/style/index",
        "element-plus/es/components/loading/style/index",
        "element-plus/es/components/radio/style/index",
        "element-plus/es/components/radio-group/style/index",
        "element-plus/es/components/popover/style/index",
        "element-plus/es/components/scrollbar/style/index",
        "element-plus/es/components/tooltip/style/index",
        "element-plus/es/components/dropdown/style/index",
        "element-plus/es/components/dropdown-menu/style/index",
        "element-plus/es/components/dropdown-item/style/index",
        "element-plus/es/components/sub-menu/style/index",
        "element-plus/es/components/menu-item/style/index",
        "element-plus/es/components/divider/style/index",
        "element-plus/es/components/card/style/index",
        "element-plus/es/components/link/style/index",
        "element-plus/es/components/breadcrumb/style/index",
        "element-plus/es/components/breadcrumb-item/style/index",
        "element-plus/es/components/table/style/index",
        "element-plus/es/components/tree-select/style/index",
        "element-plus/es/components/table-column/style/index",
        "element-plus/es/components/select/style/index",
        "element-plus/es/components/option/style/index",
        "element-plus/es/components/pagination/style/index",
        "element-plus/es/components/tree/style/index",
        "element-plus/es/components/alert/style/index",
        "element-plus/es/components/radio-button/style/index",
        "element-plus/es/components/checkbox-group/style/index",
        "element-plus/es/components/checkbox/style/index",
        "element-plus/es/components/tabs/style/index",
        "element-plus/es/components/tab-pane/style/index",
        "element-plus/es/components/rate/style/index",
        "element-plus/es/components/date-picker/style/index",
        "element-plus/es/components/notification/style/index",
        "element-plus/es/components/image/style/index",
        "element-plus/es/components/statistic/style/index",
        "element-plus/es/components/watermark/style/index",
        "element-plus/es/components/config-provider/style/index",
        "element-plus/es/components/text/style/index",
        "element-plus/es/components/drawer/style/index",
        "element-plus/es/components/color-picker/style/index",
        "element-plus/es/components/backtop/style/index",
        "element-plus/es/components/message-box/style/index",
        "element-plus/es/components/badge/style/index",
      ],
    },
    build: {
      chunkSizeWarningLimit: 2000,
      minify: "terser",
      terserOptions: {
        compress: {
          keep_infinity: true,
          drop_console: true,
          drop_debugger: true,
        },
        format: {
          comments: false,
        },
      },
      rollupOptions: {
        output: {
          entryFileNames: "js/[name].[hash].js",
          chunkFileNames: "js/[name].[hash].js",
          assetFileNames: (assetInfo: any) => {
            const info = assetInfo.name.split(".");
            let extType = info[info.length - 1];
            if (
              /\.(mp4|webm|ogg|mp3|wav|flac|aac)(\?.*)?$/i.test(assetInfo.name)
            ) {
              extType = "media";
            } else if (/\.(png|jpe?g|gif|svg)(\?.*)?$/.test(assetInfo.name)) {
              extType = "img";
            } else if (/\.(woff2?|eot|ttf|otf)(\?.*)?$/i.test(assetInfo.name)) {
              extType = "fonts";
            }
            return `${extType}/[name].[hash].[ext]`;
          },
        },
      },
    },
    define: {
      __APP_INFO__: JSON.stringify(__APP_INFO__),
    },
  };
});
