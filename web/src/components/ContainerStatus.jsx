import {Tag} from "antd";
import React from "react";
import {HttpUtil} from "@tmgg/tmgg-base";


/**
 * 容器状态
 */
export default class extends React.Component {

  state = {
    status: '-'
  }

  componentDidMount() {
    const {hostId, appName} = this.props
    HttpUtil.get("api/container/status", {hostId, appName}).then(rs => {
      this.setState({status: rs.message})
    })
  }

  render() {
    const s = this.state.status;
    if (s && s.indexOf('Up') >= 0) {
      return <Tag color={"green"}>{s} </Tag>
    }
    return <Tag color={"red"}>{s}</Tag>
  }
}
