import React from "react";
import {Terminal} from "xterm";
import "xterm/css/xterm.css"
import { AttachAddon } from '@xterm/addon-attach';

import { FitAddon } from 'xterm-addon-fit'
import {SysUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {



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
            fontSize:'small',
            theme: {

                foreground: "#1bd206", //字体
                background: "#000000", //背景色
                cursor: "help", //设置光标
            }
        })

        const {hostId,containerId} = this.props

        this.webSocket = new WebSocket(this.getWsUrl(hostId, containerId));

        const attachAddon = new AttachAddon(this.webSocket);
        term.loadAddon(attachAddon);
        let fitAddon = new FitAddon();
        term.loadAddon(fitAddon)

        fitAddon.fit()

        term.open(this.domRef.current)
        term.focus()


        this.term = term
    };


    getWsUrl(hostId, containerId) {
        let url = SysUtil.getServerUrl() + "ws/terminal?hostId=" + hostId + "&containerId=" + containerId;

        url = location.protocol.replace("http","ws") + "//" +  location.host + url


        console.log('ws url:', url)
        return url;
    }

    componentWillUnmount() {
        this.term?.dispose()
        this.term = null
    }
    render() {
        return <div>
            提示：由于网络原因，网页版终端速度较慢，可到服务器上执行命令
            <div ref={this.domRef} ></div>
        </div>
    }
}
