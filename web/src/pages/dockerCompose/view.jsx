import React from "react";
import {Avatar, Button, Card, Col, Descriptions, List, message, Row, Splitter} from "antd";
import {Gap, HttpUtil, Page, PageLoading, PageUtil} from "@tmgg/tmgg-base";
import {PlusOutlined} from "@ant-design/icons";
import ContainerTabs from "../../components/container/ContainerTabs";
import ContainerStatus from "../../components/ContainerStatus";

export default class extends React.Component {

    state = {
        info: {},
        services: [],
        curService: null
    }


    componentDidMount() {
        let id = PageUtil.currentParams().id
        this.id = id;
        HttpUtil.get('dockerCompose/get', {id}).then(rs => {
            this.setState({info: rs})
        })

        HttpUtil.get('dockerCompose/services', {id}).then(rs => {
            this.setState({services: rs})
            if (rs.length > 0) {
                this.setState({curService: rs[0]})
            }
        })
    }


    onSelect = service => {
        this.setState({curService: null}, () => {
            this.setState({curService: service})
        })
    };


    deploy = (name) => {
        const id = this.id
        HttpUtil.get('dockerCompose/deploy', {id, name}).then(rs => {
            message.success('部署命令已发送')
        })
    };
    delete = (name) => {
        const id = this.id
        HttpUtil.get('dockerCompose/delete', {id, name}).then(rs => {
            message.success('部署命令已发送')
        })
    };


    render() {
        let {info} = this.state;
        if (info == null) {
            return <PageLoading/>
        }
        let hostId = info.host?.id;
        return (
            <Page padding backgroundGray>
                <Card>
                    <Row>
                        <Col span={12}>
                            <Descriptions>
                                <Descriptions.Item label='名称'>
                                    {info.name}
                                </Descriptions.Item>
                                <Descriptions.Item label='服务器'>
                                    {info.host?.name}
                                </Descriptions.Item>
                            </Descriptions>
                        </Col>
                        <Col span={12}>
                            <div style={{display: 'flex', justifyContent: 'right', gap: 8}}>
                                <Button variant='outlined' color='default'>配置</Button>


                            </div>
                        </Col>
                    </Row>
                </Card>
                <Gap/>

                <Card>
                    <Splitter>
                        <Splitter.Panel defaultSize={300}>

                            <List dataSource={this.state.services} renderItem={item => (
                                <List.Item actions={[
                                    <Button type='primary' size='small'
                                            onClick={() => this.deploy(item.name)}>部署</Button>,
                                    <Button type='primary' size='small'
                                            onClick={() => this.delete(item.name)}>删除</Button>
                                ]}
                                           onClick={() => this.onSelect(item)}
                                >
                                    <List.Item.Meta
                                        avatar={<Avatar/>}
                                        title={<div>{item.name} <ContainerStatus hostId={hostId}
                                                                                 containerId={this.id + '_' + item.name}/>
                                        </div>}
                                        description={<> {item.ports}</>}
                                    />
                                </List.Item>

                            )}></List>

                            <Gap/>

                            <Button icon={<PlusOutlined/>} color="default" variant="dashed">
                                添加容器
                            </Button>
                        </Splitter.Panel>
                        <Splitter.Panel>
                            {this.renderContainer()}
                        </Splitter.Panel>
                    </Splitter>
                </Card>
            </Page>
        );
    }

    renderContainer = () => {
        let {info, curService} = this.state;
        if (curService == null) {
            return
        }
        let hostId = info.host?.id;

        let containerId = info.id + '_' + curService?.name;
        if (!hostId) {
            return "主机基本信息未获得"
        }
        if (!containerId) {
            return "容器基本信息未获得"
        }

        return <>
            <ContainerTabs hostId={hostId} containerId={containerId}></ContainerTabs>
        </>
    };
}
