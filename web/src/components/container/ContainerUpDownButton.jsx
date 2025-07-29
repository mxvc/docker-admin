import {Button, message, Tag} from "antd";
import React from "react";
import {HttpUtil} from "@tmgg/tmgg-base";


/**
 * 容器状态
 */
export default class extends React.Component {

  state = {
    status: '-', // running
  }

  componentDidMount() {
    const {hostId,containerId} = this.props
    HttpUtil.get("container/status", {hostId, containerId}).then(rs => {
      this.setState({status: rs})
    }).catch(()=>{
      this.setState({status:'未知'})
    })
  }

  start = () => {
    const {hostId,containerId} = this.props
    HttpUtil.get("container/start", {hostId, containerId}).then(rs => {
        message.success("启动命令已执行")
    })
  };

  stop = () => {
    const {hostId,containerId} = this.props
    HttpUtil.get("container/stop", {hostId, containerId}).then(rs => {
      message.success("停止命令已执行")
    })
  };
  render() {
    const s = this.state.status;
    if(s == null){
      return
    }
    const running = s === 'running';

    return  <Button.Group>
      <Button type='primary' disabled={running} onClick={this.start}>启动</Button>
      <Button type='primary' danger disabled={!running} onClick={this.stop}>停止</Button>
    </Button.Group>

  }
}
