import {Tag} from "antd";
import React from "react";
import {HttpUtil} from "@tmgg/tmgg-base";
import {StrUtil} from "@tmgg/tmgg-commons-lang";


/**
 * 容器状态
 */
export default class extends React.Component {





  render() {

    let v = this.props.value;
    let color = v == null ? 'gray': ( v === 'running' ? 'green': 'red')
    let size = 6

    return <div style={{background:color, width:size, height:size,borderRadius:size/2,display:'inline-block'}}></div>
  }
}
