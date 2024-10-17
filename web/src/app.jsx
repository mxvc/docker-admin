// 调整umi 默认配置

import {initBase, patchClientRoutesRegistered} from "@tmgg/tmgg-base";
import {initSystem} from "@tmgg/tmgg-system";
import {initJob} from "@tmgg/tmgg-system-job";


initBase()
initSystem()
initJob()

export function patchClientRoutes({ routes }) {
  patchClientRoutesRegistered(routes)
}


