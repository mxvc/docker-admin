/**
 * UMI Runtime Config https://umijs.org/docs/runtime-config
 */
import {history} from 'umi';
import {setLogin} from "./utils/SysConfig";
import {message, Modal} from "antd";
import hutool from "@moon-cn/hutool";


hutool.http.init({
  errorMessageHandler: msg=>message.error(msg),
  autoReject:true
})


export function render(oldRender) {
  let path = history.location.pathname;
  if (path === '/login') {
    oldRender()
    return
  }

  hutool.http.get("api/login/check").then((rs) => {
    setLogin(rs.data)
    oldRender()
  }).catch(() => {
    history.push('/login')
    oldRender()
  })
}


export function onRouteChange({location, routes, action}) {

  Modal.destroyAll()
}

