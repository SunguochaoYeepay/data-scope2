import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/datasource',
    name: 'DataSource',
    component: () => import('../views/datasource/DataSourceView.vue')
  },
  {
    path: '/query',
    name: 'QueryModule',
    children: [
      {
        path: '',
        name: 'QueryList',
        component: () => import('../views/query/QueryList.vue')
      },
      {
        path: 'editor',
        name: 'QueryEditor',
        component: () => import('../views/query/QueryEditor.vue')
      },
      {
        path: 'history',
        name: 'QueryHistory',
        component: () => import('../views/query/QueryHistory.vue')
      },
      {
        path: 'analytics/:id',
        name: 'QueryAnalytics',
        component: () => import('../views/query/QueryAnalytics.vue')
      },
      {
        path: 'detail/:id',
        name: 'QueryDetail',
        component: () => import('../views/query/QueryDetail.vue')
      }
    ]
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('../views/settings/SettingsView.vue')
  },
  {
    path: '/examples',
    name: 'Examples',
    children: [
      {
        path: '',
        name: 'ExamplesIndex',
        component: () => import('../views/examples/ExamplesIndex.vue')
      },
      {
        path: 'table',
        name: 'TableExample',
        component: () => import('../views/examples/TableExample.vue')
      },
      {
        path: 'form',
        name: 'FormExample',
        component: () => import('../views/examples/FormExample.vue')
      },
      {
        path: 'message',
        name: 'MessageExample',
        component: () => import('../views/examples/MessageExample.vue')
      },
      {
        path: 'loading',
        name: 'LoadingExample',
        component: () => import('../views/examples/LoadingExample.vue')
      },
      {
        path: 'modal',
        name: 'ModalExample',
        component: () => import('../views/examples/ModalExample.vue')
      }
    ]
  },
  // 捕获所有未匹配的路由并重定向到首页
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router