import React from "react";
import {hutool} from "@moon-cn/hutool";
import {Terminal} from "xterm";
import "xterm/css/xterm.css"
import { AttachAddon } from '@xterm/addon-attach';


export default class extends React.Component {


  cmd = ""

  domRef = React.createRef();

  componentDidMount() {
    this.initXterm()
  }
  initXterm = () => {
    let term = new Terminal({
      rendererType: "canvas", //渲染类型
      rows: 100, //行数
      convertEol: true, //启用时，光标将设置为下一行的开头
      disableStdin: false, //是否应禁用输入。
      cursorStyle: "underline", //光标样式
      cursorBlink: true, //光标闪烁
      theme: {
        foreground: "#ECECEC", //字体
        background: "#000000", //背景色
        cursor: "help", //设置光标
        lineHeight: 16
      }
    })

    const {hostId,containerId} = this.props

    this.webSocket = new WebSocket("ws://"+location.hostname+":7001/api/ws/terminal?hostId=" + hostId + "&containerId="+containerId);

    const attachAddon = new AttachAddon(this.webSocket);
    term.loadAddon(attachAddon);

    term.open(this.domRef.current)
    term.focus()


    this.term = term
  };


  componentWillUnmount() {
    this.term.dispose()
    this.term = null
  }
  render() {
    return <div>
      <div ref={this.domRef}></div>

    </div>
  }
}
