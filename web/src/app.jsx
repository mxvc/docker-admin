// 调整umi 默认配置

import {initBase, patchClientRoutesRegistered} from "@tmgg/tmgg-base";
import {initSystem} from "@tmgg/tmgg-system";


initBase()
initSystem()

export function patchClientRoutes({ routes }) {
  patchClientRoutesRegistered(routes)
}


