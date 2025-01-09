import React from 'react';
import {LazyLog, ScrollFollow} from 'react-lazylog';
import {Button, Card, Space, Switch} from "antd";
import {SysUtil} from "@tmgg/tmgg-base";


let api = 'container/';


export default class ContainerLog extends React.Component {

    state = {
        downloadFilePath: null,
        follow: true
    }

    toggleScroll = () => {
        this.setState({follow: !this.state.follow})
    };

    render() {
        const {hostId, containerId} = this.props;

        let url = SysUtil.getServerUrl() + "container/log/" + hostId + "/" + containerId;
        const downloadUrl = SysUtil.getServerUrl() + `container/downloadLog?hostId=${hostId}&containerId=${containerId}`
        return <div style={{height: 'calc(100vh - 350px)', minHeight: 400, width: '100%'}}>


            <div className='flex justify-end' style={{marginBottom: 6}}>
                <Space>
                    <div style={{display: 'flex', alignItems: 'center', gap: 6}}>
                        自动滚动 <Switch onChange={this.toggleScroll} checked={this.state.follow}></Switch>
                    </div>
                    <Button size='small' href={downloadUrl} target="_blank">下载日志</Button></Space>
            </div>

            <ScrollFollow
                startFollowing={true}
                render={({follow, onScroll}) => {
                    return (
                        <LazyLog url={url}
                                 fetchOptions={{credentials: 'include'}}
                                 selectableLines={true}
                                 stream
                                 follow={this.state.follow}
                                 onScroll={onScroll}/>
                    );
                }}
            />
        </div>
    }


}



