import {defineConfig} from 'umi';


let target = 'http://localhost:8002';
let proxy = {
    '/api': {
        target: target,
        changeOrigin: true,
        pathRewrite: {'^/api': '/'},
        ws: true,
    },
    '/ws-log-view': {
        target: target,
        changeOrigin: true,
        ws: true,
    },
    '/container/log': {
        target: target,
        changeOrigin: true,
        ws: true,
    },

};

export const configLocal = {


    define: {
        "process.env.API_BASE_URL": "/api/"
    },

    proxy
};
export default defineConfig(configLocal);
