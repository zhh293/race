import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/login/index.vue'
import Register from '@/views/register/index.vue'
import Home from '@/views/home.vue'
import Table from '@/views/table/index.vue'
import TableAi from '@/views/ask/tableAi.vue'
import ChatAi from '@/views/ask/chatAi.vue'
import MusicAi from '@/views/ask/musicAi.vue'
import My from '@/views/myself/my.vue'
import Used from '@/views/myself/used.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: '/register',
      name: 'register',
      component: Register,
    },
    {
      path: '/table',
      name: 'table',
      component: Table,
    },
    {
      path: '/tableAi',
      name: 'tableAi',
      component: TableAi,
    },
    {
      path: '/chatAi',
      name: 'chatAi',
      component: ChatAi,
    },
    {
      path: '/musicAi',
      name: 'musicAi',
      component: MusicAi,
    },
    {
      path: '/my',
      name: 'my',
      component: My,
    },
    {
      path: '/used',
      name: 'used',
      component: Used,
    }
  ]
})

export default router
