import {defineConfig} from 'umi';


let target = 'http://127.0.0.1:8080';
let proxy = {
    '/api': {
        target: target,
        changeOrigin: true,
        pathRewrite: {'^/api': '/'},
    }
};

export const configLocal = {


    define: {
        "process.env.API_BASE_URL": "/api/"
    },

    proxy
};
export default defineConfig(configLocal);
