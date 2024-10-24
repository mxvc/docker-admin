import {AutoComplete, Badge, Button, Card, Descriptions, Form, Input, message, Modal, Tabs} from 'antd';
import React from 'react';
import HostImages from "./HostImages";
import HostContainers from "./HostContainers";
import {Spin} from "antd/lib";
import {HttpUtil, PageUtil} from "@tmgg/tmgg-base";


let api = 'host/';


export default class extends React.Component {

    state = {
        host: {},
        info: {},
        loading: true,

        runtimeLoading: true,
    }


    componentDidMount() {
        this.loadData()
    }

    loadData = () => {
        let {id} = PageUtil.currentLocationQuery()

        this.setState({runtimeLoading: true})

        HttpUtil.get(api + "get", {id})
            .then(rs => {
                this.setState({host: rs, loading: false})
            })

        // 实时状态
        HttpUtil.get(api + "runtime/get", {id})
            .then(rs => {
                this.setState({info: rs, runtimeLoading: false})
            })
    }


    cleanImage = () => {
        message.loading('清理中...')
        let {id} = this.props.location.query;
        HttpUtil.get(api + "cleanImage", {id}).then(rs => {
        })
    }

    render() {
        const {host, info, loading} = this.state;
        if (loading) {
            return <Spin/>
        }


        return (<>
            <Card>
                <Descriptions title={host.name}>
                    <Descriptions.Item label="操作系统">{info.operatingSystem}</Descriptions.Item>
                    <Descriptions.Item
                        label="内存"> {(info.memTotal / 1024 / 1024 / 1024).toFixed(2)} G</Descriptions.Item>

                    <Descriptions.Item label="存储目录">{info.dockerRootDir}</Descriptions.Item>
                    <Descriptions.Item label="docker版本">{info.serverVersion}</Descriptions.Item>
                    <Descriptions.Item label="系统时间">{info.systemTime}</Descriptions.Item>
                </Descriptions>

                <div style={{display: "flex", justifyContent: 'end', gap: 8}}>
                    <Button onClick={this.cleanImage} title='清理未使用镜像'>清理镜像</Button>
                </div>

            </Card>

            <Card className='mt-2'>
                <Tabs defaultActiveKey="1">
                    <Tabs.TabPane tab={<Badge count={info.containers}> 容器</Badge>} key="1">
                        <HostContainers id={host.id}/>
                    </Tabs.TabPane>
                    <Tabs.TabPane tab={<Badge count={info.images}> 镜像</Badge>} key="2">
                        <HostImages id={host.id}/>
                    </Tabs.TabPane>
                </Tabs>
            </Card>


        </>)
    }



}



