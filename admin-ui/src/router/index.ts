import type { App } from "vue";
import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import StudentActivityHistory from '../views/studenthistory/StudentActivityHistory.vue'

export const Layout = () => import("../layout/index.vue");

// 静态路由
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: "/redirect",
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("../views/redirect/index.vue"),
      },
    ],
  },

  {
    path: "/login",
    component: () => import("../views/login/index.vue"),
    meta: { hidden: true },
  },
  {
    path: "/",
    name: "Layout",
    component: Layout,
    redirect: "/dashboard",
    meta: {
      title: "dashboard",
      icon: "homepage",
      affix: true,
      keepAlive: true,
    },
    children: [
      {
        path: "dashboard",
        component: () => import("../views/dashboard/index.vue"),
        name: "Dashboard",
        meta: {
          title: "首页",
          icon: "homepage",
          affix: true,
          keepAlive: true,
        },
      },
      {
        path: "user",
        component: () => import("../views/user/index.vue"),
        meta: {
          title: "用户管理",
          affix: true,
          keepAlive: true,
        },
      },

      {
        path: "activity/list",
        component: () => import("../views/activity/ActivityList.vue"),
        name: "ActivityList",
        meta: {
          title: "活动列表",
          affix: true,
          keepAlive: true,
        }
      },
      {
        path: "student",
        component: () => import("../views/student/index.vue"),
        meta: {
          title: "学生管理",
          affix: true,
          keepAlive: true,
        },
      },
      {
        path: "admin/activity/history",
        name: "StudentActivityHistory",
        component: () => import("../views/studenthistory/StudentActivityHistory.vue"),
        meta: {
          title: "学生活动记录",
          icon: "document", // 图标可选，使用 Element Plus 内置图标名
          affix: true,
          keepAlive: true
        }
      },
      {
        path: "admin/student/Score",
        name: "StudentScore",
        component: () => import("../views/StudentScore/StudentScore.vue"),
        meta: {
          title: "学生积分",
          icon: "document", // 图标可选，使用 Element Plus 内置图标名
          affix: true,
          keepAlive: true
        }
      },
      {
        path: "admin/student/Gift",
        name: "Gift",
        component: () => import("../views/gift/Gift.vue"),
        meta: {
          title: "礼物",
          icon: "document", // 图标可选，使用 Element Plus 内置图标名
          affix: true,
          keepAlive: true
        }
      },
      {
        path: "admin/student/Tgift",
        name: "TGift",
        component: () => import("../views/Tgift/Tgift.vue"),
        meta: {
          title: "学生礼物兑换记录",
          icon: "document", // 图标可选，使用 Element Plus 内置图标名
          affix: true,
          keepAlive: true
        }
      },
      {
        path: "401",
        component: () => import("../views/error-page/401.vue"),
        meta: { hidden: true },
      },
      {
        path: "404",
        component: () => import("../views/error-page/404.vue"),
        meta: { hidden: true },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

export function setupRouter(app: App<Element>) {
  app.use(router);
}

export function resetRouter() {
  router.replace({ path: "/login" });
}

export default router;
