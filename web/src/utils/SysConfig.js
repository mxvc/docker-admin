import {PermUtil} from "@tmgg/tmgg-base";

export function isPermitted(p){
 return  PermUtil.hasPermission(p)
}
export function notPermitted(p){
  return  !isPermitted(p)
}
