import React from "react";
import {Button} from "antd";

export class LogSse extends React.Component {

    state = {
        list:[]
    }

    componentDidMount() {
        this.sse = new EventSource(this.props.url);
        this.append('准备连接服务端....')
        this.sse.onopen = (event) => {
            this.append('服务端连接成功')
        }

        this.sse.onmessage = (event) => {
            this.append(event.data);
        }
        this.sse.onerror = (event) => {
            this.append('error')
        }
    }

    componentWillUnmount() {
        this.sse.close()
    }

    append = msg => {
        this.state.list.push(msg)
        this.setState({
            list: this.state.list
        })
    };

    render() {


        return <div style={{ padding: '20px', maxHeight:500,  overflowY: 'scroll' }}>
                {this.state.list.map((item,index)=>{
                    return <div key={index} style={{whiteSpace:'nowrap' }}>{item}</div>
                })}
        </div>
    }

}
