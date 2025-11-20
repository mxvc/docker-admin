import {defineConfig} from 'umi';

import config from "@jiangood/springboot-admin-starter/config/dist/config.local";



/*config.proxy["/api/ws"] = {
    target: 'http://127.0.0.1:8002',
    changeOrigin: true,
    ws: true,
}*/


export default defineConfig(config);
