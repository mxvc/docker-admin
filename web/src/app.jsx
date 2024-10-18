// 调整umi 默认配置

import {initBase, patchClientRoutesRegistered} from "@tmgg/tmgg-base";
import {initSystem} from "@tmgg/tmgg-system";
import {initJob} from "@tmgg/tmgg-system-job";
import {initCodeGen} from "@tmgg/tmgg-system-code-gen";


initBase()
initSystem()
initJob()
initCodeGen()

export function patchClientRoutes({ routes }) {
  patchClientRoutesRegistered(routes)
}


