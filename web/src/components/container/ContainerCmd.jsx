import React from "react";
import {hutool} from "@moon-cn/hutool";
import {Terminal} from "xterm";
import "xterm/css/xterm.css"


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
    // 创建terminal实例
    term.open(this.domRef.current)
    // 换行并输入起始符 $


    this.term = term
    this.runFakeTerminal()
  };
  runFakeTerminal = () => {
    let term = this.term
    if (term._initialized) return
    // 初始化
    term._initialized = true
    term.writeln("Docker Terminal  \x1b[1;32m容器终端\x1b[0m.")
    term.writeln('请输入Linux命令， 如 ls')
    term.write('> ')

    term.onKey(e => {
      const printable = !e.domEvent.altKey && !e.domEvent.altGraphKey && !e.domEvent.ctrlKey && !e.domEvent.metaKey
      if (e.domEvent.keyCode === 13) { // 回车

        term.writeln("\n")
        this.handleSend(this.cmd)
        this.cmd = ""

      } else if (e.domEvent.keyCode === 8) { // back 删除的情况
        if (term._core.buffer.x > 2) {
          term.write('\b \b')
        }
      }else {
        if (printable) {
          term.write(e.key)
          this.cmd += e.key
        }

      }
    })
    term.onData(key => {  // 粘贴的情况
      if(key.length > 1) {
        term.write(key)
      }
      console.log('key', key)
    })
  };

  componentWillUnmount() {
    this.term.dispose()
    this.term._initialized = false
  }


  handleSend = (cmd) => {
    console.log(cmd)
    let {hostId, containerId} = this.props;

    hutool.http.get('/api/container/cmd', {hostId, containerId, cmd}).then(rs => {
      if(rs.data){
        const lines = rs.data.split("\n")
        for (let line of lines) {
          this.term.writeln(line)
        }
      }else {
        this.term.writeln("命令执行错误")
      }
      this.term.write('> ')
    })
  }

  render() {
    return <div>
      <div ref={this.domRef}></div>

    </div>
  }
}
