import {
    Alert,
    AutoComplete,
    Button,
    Card,
    Col,
    Descriptions,
    Divider,
    Input,
    message,
    Modal,
    Row,
    Space,
    Spin,
    Switch,
    Tabs,
    Tag,
    Typography
} from 'antd';
import React from 'react';
import ConfigForm from "./ConfigForm";
import {history} from "umi";
import {isPermitted} from "../../utils/SysConfig";

import ContainerLog from "../../components/container/ContainerLog";
import ContainerFile from "../../components/container/ContainerFile";
import {HttpUtil, PageUtil} from "@tmgg/tmgg-base";
import LogView from "../../components/LogView";

let api = 'app/';


export default class extends React.Component {

    state = {
        loading: true,
        app: {},

        containerLoading: true,
        container: {},


        tagOptions: [],


        publishApp: {
            targetVersion: null
        },
        showEditName: false,
        newName: '',


        deployLogOpen: false

    }


    componentDidMount() {
        let id = PageUtil.currentLocationQuery().id
        this.id = id;

        HttpUtil.get('app/get', {id: id}).then(rs => {
            this.setState({app: rs, loading: false});
            this.loadTagOptions(rs)
        })

        this.loadContainer();
    }


    loadContainer = () => {
        console.log('loadContainer')
        this.setState({containerLoading: true})
        HttpUtil.get("app/container", {id: this.id}).then(container => {
            this.setState({container})

            if (container.state === 'deploying') {
                setTimeout(() => this.loadContainer(), 1000)
            }

        }).catch(() => {
        }).finally(() => {
            this.setState({containerLoading: false})
        })
    }

    loadTagOptions(app) {
        if (app.project) {
            HttpUtil.get('project/versions', {projectId: app.project.id}).then(rs => {
                this.setState({tagOptions: rs})
            })
        }

    }

    deploy = () => {
        const {container} = this.state
        container.state = 'deploying'
        this.setState({container})
        HttpUtil.post('app/deploy/' + this.state.app.id).then(rs => {
            message.success('部署指令已发送，异步执行中...')
            this.setState({deployLogOpen: true})
            this.loadContainer()
        })
    }
    start = () => {
        HttpUtil.post('app/start/' + this.state.app.id).then(() => {
            this.loadContainer()
        })
    }
    stop = () => {
        HttpUtil.post('app/stop/' + this.state.app.id).then(() => {
            this.loadContainer()
        })
    }

    setAutoDeploy = (id, autoDeploy) => {
        HttpUtil.get("app/autoDeploy", {id, autoDeploy})
    }
    setAutoRestart = (id, autoRestart) => {
        HttpUtil.get("app/autoRestart", {id, autoRestart})
    }


    updateVersion = () => {
        const id = this.state.app.id;
        const tag = this.state.publishApp.targetVersion;
        HttpUtil.get("app/updateVersion", {id, version: tag}).then(rs => {
            window.location.reload(true)
        })
    }


    handleDelete = () => {
        const id = this.state.app.id
        const hide = message.loading('删除中...')
        HttpUtil.get(api + 'delete', {id}).then(rs => {
            hide();

            history.push('/app')

        }).catch(rs => {
            hide();
            Modal.confirm({
                title: '删除失败',
                content: '是否强制删除数据',
                okText: '强制删除数据',
                cancelText: '取消',
                onOk: () => {
                    HttpUtil.get(api + 'delete', {id, force: true}).then(rs => {
                        history.push('/app')
                    })
                }
            })

        })
    }

    rename = () => {
        let appId = this.state.app.id;
        let {newName} = this.state;
        const hide = message.loading('指令发送中...')
        HttpUtil.post(api + 'rename', {appId, newName}).then(rs => {

            message.success(rs.message)
            this.setState({app: rs, showEditName: false})
        }).finally(hide)
    }

    render() {
        const {container, app, loading, containerLoading} = this.state;

        if (loading) {
            return <Spin/>
        }
        const {state} = container;

        return (<>

            <Card title={app.name} extra={<Space>
                <Button disabled={state !== 'exited'} onClick={this.start} type="primary">启动</Button>
                <Button disabled={state !== 'running'} onClick={this.stop} type="primary" danger>停止</Button>
                <Button onClick={this.deploy} loading={state === 'deploying'} type="primary">部署</Button>
            </Space>}>


                <Descriptions size="small">
                    <Descriptions.Item label='应用'>  {app.name} </Descriptions.Item>
                    <Descriptions.Item label='主机'>  {app.host?.name} </Descriptions.Item>
                    <Descriptions.Item label='状态'>
                        {containerLoading ?
                            "检测中..." : <Tag color={state === 'running' ? 'green' : 'red'}>
                                {container.status}</Tag>}

                    </Descriptions.Item>
                    <Descriptions.Item label='镜像' span={2}>  {app.imageUrl}:{app.imageTag} </Descriptions.Item>


                </Descriptions>

            </Card>


            <Card className='mt-2'>
                {this.renderTabs()}
            </Card>

            <Modal title='部署日志' open={this.state.deployLogOpen} destroyOnClose width={800} footer={null}
                   onCancel={() => this.setState({deployLogOpen: false})}>
                <LogView url={app.logUrl}/>
            </Modal>
        </>)
    }

    renderTabs = () => {
        const {container, containerLoading} = this.state;


        const {app} = this.state

        const notFound = container.state === 'notFound'


        let containerId = container.id
        let hostId = app.host.id

        const items = []

// 容器信息
        if (!notFound) {
            items.push({
                key: 'log',
                label: '日志',
                children: <ContainerLog hostId={hostId} containerId={containerId}/>
            })


            items.push({
                key: 'file',
                label: '文件',
                children: <ContainerFile hostId={hostId} containerId={containerId}/>

            })
        }


        if (isPermitted('app:config')) {
            items.push({
                key: 'config',
                label: '配置',
                children: <ConfigForm app={app}
                                      onChange={app => {
                                          window.location.reload(true)
                                      }}/>
            })
        }

        items.push({
            key: 'publish',
            label: '发布', children: <>
                <Row wrap={false}>
                    <Col flex="100px">自动发布</Col>
                    <Col flex="auto">
                        <Switch checked={app.autoDeploy}
                                onChange={checked => {
                                    app.autoDeploy = checked
                                    this.setState({app: this.state.app})
                                    this.setAutoDeploy(app.id, checked)
                                }}
                        ></Switch>
                        <div>
                            <Typography.Text
                                type="secondary">当有镜像构建成功后，自动更新应用到最新构建的版本</Typography.Text>
                        </div>
                    </Col>
                </Row>
                <Divider></Divider>

                <Row wrap={false}>
                    <Col flex="100px">手动发布</Col>
                    <Col flex="auto">
                        <AutoComplete options={this.state.tagOptions}
                                      style={{width: 150}}
                                      value={this.state.publishApp.targetVersion}
                                      onChange={targetVersion => {
                                          this.setState({publishApp: {targetVersion}})
                                      }}/>

                        &nbsp;&nbsp;
                        <Button type={"primary"} onClick={this.updateVersion}>更新应用</Button>

                        <div>
                            <Typography.Text type="secondary">用指定的镜像版本</Typography.Text>
                        </div>
                    </Col>
                </Row>
            </>
        })

        if (isPermitted('app:config')) {
            items.push({
                key: 'setting',
                label: "设置",
                children: <>
                    <Row wrap={false}>
                        <Col flex="100px">名称</Col>
                        <Col flex="auto">

                            {!this.state.showEditName ? <div>
                                {this.state.app.name} <a onClick={() => this.setState({
                                newName: this.state.app.name,
                                showEditName: true
                            })}>修改名称</a>
                            </div> : <div>

                                <Input value={this.state.newName} style={{width: 200}}
                                       onChange={e => this.setState({newName: e.target.value})}></Input>

                                <Button type={"primary"} onClick={this.rename}>确定</Button>
                            </div>}

                        </Col>

                    </Row>


                    <Divider></Divider>
                    <Row wrap={false}>
                        <Col flex="100px">自动重启</Col>
                        <Col flex="auto">
                            <Switch checked={app.autoRestart}
                                    onChange={checked => {
                                        app.autoRestart = checked
                                        this.setState({app: this.state.app})
                                        this.setAutoRestart(app.id, checked)
                                    }}
                            ></Switch>
                        </Col>

                    </Row>


                    <Divider></Divider>
                    <Row wrap={false}>
                        <Col flex="100px">删除应用</Col>
                        <Col flex="auto">
                            <Space direction={"vertical"}>
                                <Alert
                                    message="请注意，删除应用将清除该应用的所有数据，且该操作不能被恢复，您确定要删除吗?"
                                    type="warning"
                                ></Alert>
                                <Button danger type="primary" onClick={this.handleDelete}>删除应用</Button>
                            </Space>
                        </Col>
                    </Row>
                </>
            })
        }


        return <>
            <Tabs items={items} destroyInactiveTabPane></Tabs>
        </>
    }

}



