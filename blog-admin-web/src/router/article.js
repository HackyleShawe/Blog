import Layout from '@/layout'
/**
 * 文章管理的路由地址
 */
export default {
  path: '/article',
  component: Layout,
  redirect: '/article/list',
  name: 'Article',
  meta: { title: 'Article', icon: 'el-icon-document' },
  children: [
    {
      path: 'list',
      name: 'Article List',
      component: () => import('@/views/article/list/index.vue'),
      meta: { title: 'List', icon: 'el-icon-document-checked'}
    },
    {
      path: 'writing',
      name: 'Article Writing',
      component: () => import('@/views/article/writing/index.vue'),
      meta: { title: 'Write', icon: 'el-icon-document-add' }
    },

    {
      path: 'author',
      name: 'Author',
      component: () => import('@/views/article/author/index.vue'),
      meta: { title: 'Author', icon: 'el-icon-user-solid' }
    },

    {
      path: 'category',
      name: 'Article Category',
      component: () => import('@/views/article/category/index.vue'),
      meta: { title: 'Category', icon: 'el-icon-collection' }
    },
    {
      path: 'comment',
      name: 'Article comment',
      component: () => import('@/views/article/comment/index.vue'),
      meta: { title: 'Comment', icon: 'el-icon-collection-tag' }
    },

    {
      path: 'file',
      name: 'Article file',
      component: () => import('@/views/article/file/index.vue'),
      meta: { title: 'file', icon: 'el-icon-document-delete'}
    },
  ]
}
