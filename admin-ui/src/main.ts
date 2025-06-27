import { createApp } from "vue";
import App from "./App.vue";
import setupPlugins from "@/plugins";

// 引入 Element Plus 及其样式
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

// 本地SVG图标
import "virtual:svg-icons-register";


// 样式
import "element-plus/theme-chalk/dark/css-vars.css";
import "@/styles/index.scss";
import "uno.css";
import "animate.css";

const app = createApp(App);

// 使用插件
app.use(setupPlugins);
app.use(ElementPlus); // ✅ 添加这一行

app.mount("#app");
