import {
    Alert, AutoComplete,
    Button,
    Card,
    Col,
    Descriptions,
    Divider, Form,
    Input,
    message,
    Modal,
    Row, Skeleton,
    Space,
    Spin,
    Tabs,
    Tag
} from 'antd';
import React from 'react';
import ConfigForm from "./ConfigForm";
import {history} from "umi";

import ContainerLog from "../../components/container/ContainerLog";
import ContainerFile from "../../components/container/ContainerFile";
import {FieldOrgTreeSelect, FieldRadioBoolean, FieldRemoteSelect, HasPerm, HttpUtil, PageUtil} from "@tmgg/tmgg-base";
import LogView from "../../components/LogView";
import PublishForm from "./PublishForm";
import {SyncOutlined} from "@ant-design/icons";

const Item = Descriptions.Item

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



        formValues: {},
        formOpen: false,

    }
    formRef = React.createRef()
    componentDidMount() {
        let id = PageUtil.currentLocationQuery().id
        this.id = id;
        this.loadApp();
        this.loadContainer();
    }


    loadApp() {
        HttpUtil.get('app/get', {id: this.id}).then(rs => {
            this.setState({app: rs, loading: false});
        })
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

    reload = () => {
        this.loadApp();
        this.loadContainer()
    };


    deploy = () => {
        const {container} = this.state
        container.state = 'deploying'
        this.setState({container})
        HttpUtil.post('app/deploy/' + this.state.app.id).then(rs => {
            message.success('部署指令已发送，异步执行中...')

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
    handleEdit = record => {
        this.setState({formOpen: true, formValues: record})
    }


    onFinish = values => {
        HttpUtil.post('app/updateBaseInfo', values).then(rs => {
            this.setState({formOpen: false})
            this.reload()
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
        const deploying = state === 'deploying'


        return (<>

            <Card title={app.name} extra={<Space>
                <Button disabled={state !== 'exited'} onClick={this.start} type="primary">启动</Button>
                <Button disabled={state !== 'running'} onClick={this.stop} type="primary" danger>停止</Button>
                <Button onClick={this.deploy} loading={state === 'deploying'} type="primary">重新部署</Button>
                <Button onClick={()=>this.handleEdit(this.state.app)}>修改基本信息</Button>
            </Space>}>


                <Descriptions size="small" >
                    <Item label='应用'>  {app.name} </Item>
                    <Item label='镜像' span={2}>  {app.imageUrl}:{app.imageTag} </Item>
                    <Item label='主机'>  {app.host?.name} </Item>
                    <Item label='状态'>
                        {containerLoading ? "检测中..." :
                            <Tag color={state === 'running' ? 'green' : 'red'}>
                                {container.status}</Tag>}

                    </Item>

                    <Item label='自动发布'>  {app.autoDeploy ? '是' : '否'} </Item>
                    <Item label='组织机构'>  {app.sysOrg?.name} </Item>

                </Descriptions>

            </Card>


            <Card className='mt-2'>
                {this.renderTabs()}
            </Card>


            <Modal title='应用基本信息'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnClose

                   width={600}

            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}>
                    <Form.Item name='id' noStyle></Form.Item>

                    <Form.Item name='imageUrl' label='镜像' required rules={[{required: true}]}>
                        <AutoComplete options={this.state.imageList} onSearch={this.loadImageList}></AutoComplete>
                    </Form.Item>


                    <Form.Item name='imageTag' label='版本' required rules={[{required: true}]}>
                        <AutoComplete options={this.state.imageTagList}
                                      onSearch={this.loadImageTagList}></AutoComplete>
                    </Form.Item>



                    <Form.Item label='所属组织' name={['sysOrg', 'id']} >
                        <FieldOrgTreeSelect/>
                    </Form.Item>
                </Form>
            </Modal>

        </>)
    }

    renderTabs = () => {
        const {container, containerLoading} = this.state;


        const {app} = this.state

        const notFound = container.state === 'notFound'
        const deploying = container.state === 'deploying'

        let containerId = container.id
        let hostId = app.host.id

        const items = [
            {
                key: 'deployLog',
                label: '部署日志',
                icon: deploying ? <SyncOutlined spin /> : null,
                children: <LogView url={app.logUrl}/>
            },
            {
                key: 'log',
                label: 'Console',
                disabled:notFound,
                children: containerLoading ? <Skeleton /> : <ContainerLog hostId={hostId} containerId={containerId}/>
            },
            {
                key: 'file',
                label: '文件',
                disabled:notFound,
                children: <ContainerFile hostId={hostId} containerId={containerId}/>

            },
            {
                key: 'config',
                label: '容器设置',
                children: <HasPerm code='app:config'> <ConfigForm app={app} onChange={this.reload}/></HasPerm>
            },
            {
                key: 'publish',
                label: '发布',
                children: <HasPerm code='app:deploy'> <PublishForm appId={app.id} onChange={this.reload}/></HasPerm>
            },
            {
                key: 'setting',
                label: "设置",
                children: <HasPerm code='app:config'>
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
                </HasPerm>
            }
        ]


        return <Tabs items={items} destroyInactiveTabPane></Tabs>
    }

}



