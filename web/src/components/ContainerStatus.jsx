import {Tag} from "antd";
import React from "react";
import {HttpUtil} from "@tmgg/tmgg-base";
import {StrUtil} from "@tmgg/tmgg-commons-lang";


/**
 * 容器状态
 */
export default class extends React.Component {

  state = {
    status: '-'
  }

  componentDidMount() {
    const {hostId, appName,containerId} = this.props
    HttpUtil.get("container/status", {hostId, appName,containerId}).then(rs => {
      this.setState({status: rs})
    }).catch(()=>{
      this.setState({status:'未知'})
    })
  }

  render() {
    const s = this.state.status;
    if (s && (StrUtil.contains(s,'Up') || StrUtil.contains(s, "running"))) {
      return <Tag color={"green"}>{s} </Tag>
    }
    return <Tag color={"red"}>{s}</Tag>
  }
}
