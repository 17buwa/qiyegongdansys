import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '工作台', icon: 'DataLine' }
      },
      {
        path: 'tickets',
        name: 'TicketList',
        component: () => import('@/views/TicketList.vue'),
        meta: { title: '工单管理', icon: 'Tickets' }
      },
      {
        path: 'tickets/create',
        name: 'TicketCreate',
        component: () => import('@/views/TicketCreate.vue'),
        meta: { title: '创建工单', icon: 'EditPen', hidden: true }
      },
      {
        path: 'tickets/:id',
        name: 'TicketDetail',
        component: () => import('@/views/TicketDetail.vue'),
        meta: { title: '工单详情', hidden: true }
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User', roles: ['ADMIN', 'AGENT'] }
      },
      {
        path: 'roles',
        name: 'RoleManage',
        component: () => import('@/views/RoleManage.vue'),
        meta: { title: '角色权限', icon: 'Key', roles: ['ADMIN'] }
      },
      {
        path: 'ticket-types',
        name: 'TicketTypeManage',
        component: () => import('@/views/TicketTypeManage.vue'),
        meta: { title: '工单类型', icon: 'Collection', roles: ['ADMIN'] }
      },
      {
        path: 'logs',
        name: 'LogList',
        component: () => import('@/views/LogList.vue'),
        meta: { title: '操作日志', icon: 'Document', roles: ['ADMIN'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', icon: 'UserFilled', hidden: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 企业工单系统` : '企业工单系统'
  const token = localStorage.getItem('token')
  if (to.meta.public) {
    if (token && to.path === '/login') {
      next('/')
    } else {
      next()
    }
    return
  }
  if (!token) {
    next('/login')
    return
  }
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  const allowedRoles = to.meta.roles
  if (allowedRoles && user && !allowedRoles.includes(user.roleCode)) {
    next('/dashboard')
    return
  }
  next()
})

export default router