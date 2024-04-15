import {defineConfig} from 'umi';

export default defineConfig({
  hash:true,
  history:{type:'hash'},

  nodeModulesTransform: {
    type: 'none',
  },
  fastRefresh: {},


  devServer: {
    port: 3000,
  },



  proxy: {
    '/api': {
      target: 'http://127.0.0.1:7001',
      changeOrigin: true,
      pathRewrite: { '^/api': '/api' },
    },
    '/log-view': {
      target: 'http://127.0.0.1:7001',
      changeOrigin: true,
      pathRewrite: { '^/log-view': '/log-view' },
    },
    '/api/log-view': {
      target: 'http://127.0.0.1:7001',
      changeOrigin: true,
      ws: true,
      pathRewrite: { '^/api/log-view': '/api/log-view' },
    },
  },
});





