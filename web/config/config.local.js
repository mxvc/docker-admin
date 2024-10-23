import {defineConfig} from 'umi';


let proxy = {
    '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {'^/api': '/'},
    },
    '/ws-log-view': {
        target: "http://localhost:8080",
        changeOrigin: true,
        ws: true,
    },
    '/container/log': {
        target: "http://localhost:8080",
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
