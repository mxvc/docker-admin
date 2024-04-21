import {message} from "antd";


export function showResult(ajaxResult){
  if(ajaxResult.message == null){
    return
  }
  if(ajaxResult.success){
    message.success(ajaxResult.message)
  }else {
    message.error(ajaxResult.message)
  }
}




