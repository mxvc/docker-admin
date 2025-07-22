import {defineConfig} from 'umi';
import {defaultConfigLocal} from "@tmgg/tmgg-system/config/defaultConfig";




defaultConfigLocal.proxy["/api/ws"] = {
    target: 'http://127.0.0.1:8002',
    changeOrigin: true,
    ws: true,
}


export default defineConfig(defaultConfigLocal);
