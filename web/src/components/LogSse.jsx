import React from "react";

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
            console.log( event.data)
            this.append(event.data);
        }
        this.sse.onerror = (event) => {
            console.log('error', event)
            this.append('error')
        }
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
