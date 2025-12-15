// const { defineConfig } = require('@vue/cli-service')

//从配置文件中读取项目使用的端口
let port = process.env.VUE_APP_PORT

module.exports = {
  // transpileDependencies: true,

  lintOnSave: process.env.NODE_ENV === 'development', //如果是开发环境，就是true

  devServer: {
    host: 'localhost', //本机ip
    port: port, //本项目使用的端口
    open: true,
    //proxy: {
    //    '/': {  //代理别名
    //        target: backendAPI,   //代理目标值
    //        changeOrigin: true,
    //        secure: true,
    //        pathRewrite:{  //替换路径中的/api
    //            '^/':'/'
    //        }
    //        //pathRequiresRewrite: {
    //        //  '^/api': ''
    //        //}
    //    }
    //},
    overlay: {
      warning: false,
      errors: true
    }
  },

  //生产打包配置
  outputDir: "dist", //打包输出目录
  //assetsDir: "static", //静态资源输出目录，会让几乎所有的打包文件都放在该文件夹下
  publicPath: "./", //这个很重要，设置相对目录

}
