import {defineConfig} from 'umi';


export const config = {
    npmClient: 'pnpm',

    define: {
        "process.env.API_BASE_URL": "/",
        "process.env.LOGIN_URL": "/login",
    },


    /**
     * 配置是否让生成的文件包含 hash 后缀，通常用于增量发布和避免浏览器加载缓存。
     */
    hash: true,

    history:{type:'hash'},

    // monorepo 复杂，还得设置忽略、编译等，先关掉
    mfsu: false



};
export default defineConfig(config);
