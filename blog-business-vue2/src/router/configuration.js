import Layout from '@/layout'

/**
 * 配置中心的路由地址
 */
export default {
  path: '/config',
  component: Layout,
  redirect: '/config/friend-link',
  name: 'Configuration',
  meta: { title: 'Configuration', icon: 'el-icon-setting' },
  children: [
    {
      path: 'admin-info',
      name: 'Admin Info',
      component: () => import('@/views/configuration/admin-info'),
      meta: { title: 'Admin Info', icon: 'el-icon-user' }
    },
    {
      path: 'friend-link',
      name: 'Friend Link',
      component: () => import('@/views/configuration/friend-link'),
      meta: { title: 'Friend Link', icon: 'el-icon-link' }
    },

  ]
}