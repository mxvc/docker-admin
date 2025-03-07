import {defineConfig} from 'umi';
import {defaultConfigLocal} from "@tmgg/tmgg-system/config/defaultConfig";


// let target = 'http://localhost:8002';
// let proxy = {
//     '/api': {
//         target: target,
//         changeOrigin: true,
//         pathRewrite: {'^/api': '/'},
//         ws: true,
//     },
//     '/ws-log-view': {
//         target: target,
//         changeOrigin: true,
//         ws: true,
//     },
//     '/container/log': {
//         target: target,
//         changeOrigin: true,
//         ws: true,
//     },
//
// };


export default defineConfig(defaultConfigLocal);
