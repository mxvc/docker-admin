import React from "react";
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

export function formatTotalTime(time) {
  if (time == null || time == '-') {
    return null
  }
  let seconds = time / 1000;

  seconds = Math.floor(seconds)

  if (seconds < 60) {
    return seconds + '秒';
  }

  let min = seconds / 60;
  seconds = seconds % 60;

  min = Math.floor(min);
  seconds = Math.floor(seconds)

  return min + '分' + seconds + '秒'
}


