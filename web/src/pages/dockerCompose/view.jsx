import React from "react";
import {
    AutoComplete,
    Avatar,
    Button,
    Card,
    Col,
    Descriptions,
    Form,
    List,
    message,
    Modal,
    Row,
    Spin,
    Splitter,
    Typography
} from "antd";
import {Gap, HttpUtil, Page, PageLoading, PageUtil} from "@tmgg/tmgg-base";
import {DeleteOutlined, PlusOutlined} from "@ant-design/icons";
import ContainerTabs from "../../components/container/ContainerTabs";
import CodeMirrorEditor from "../../components/CodeMirrorEditor";
import ActiveDot from "../../components/ActiveDot";

export default class extends React.Component {

    state = {
        info: {},
        services: [],
        servicesStatus: {},

        curService: null,

        formOpen: false,
        formValues: {},
        imageList: [],
        imageTagList: [],


        configOpen: false,
        configContent: null,


        deployOpen: false,
        deployItem: null,
        deployTagList: [],
        deployTag: null,
        deployProcessing: false

    }
    formRef = React.createRef()


    componentDidMount() {
        let id = PageUtil.currentParams().id
        this.id = id;
        HttpUtil.get('dockerCompose/get', {id}).then(rs => {
            this.setState({info: rs})
        })
        this.loadServices(id);
    }


    loadServices = () => {
        HttpUtil.get('dockerCompose/services', {id: this.id}).then(rs => {
            this.setState({services: rs})
            if (rs.length > 0 && this.state.curService == null) {
                this.setState({curService: rs[0]})
            }
        })

        HttpUtil.get('dockerCompose/servicesStatus', {id: this.id}).then(rs => {
            this.setState({servicesStatus: rs})
        })
    };

    onSelect = service => {
        this.setState({curService: null}, () => {
            this.setState({curService: service})
        })
    };

    onDeployClick(item) {
        this.setState({deployOpen: true, deployItem: item, deployTagList: []}, this.loadDeployTagList)
    }

    loadDeployTagList = (text) => {
        let {deployItem} = this.state;
        const url = deployItem.imageUrl;
        const tag = deployItem.imageTag;

        this.setState({deployTag: tag})

        HttpUtil.get('image/tagOptions', {url, searchText: text}).then(rs => {
            this.setState({deployTagList: rs})
        })

    };

    deploy = () => {
        this.setState({deployProcessing:true})
        HttpUtil.get('dockerComposeServiceItem/deploy', {id: this.state.deployItem.id, tag: this.state.deployTag}).then(rs => {
            this.setState({deployOpen:false})
            this.loadServices()
        }).finally(()=>{
            this.setState({deployProcessing:false})
        })
    };
    delete = (id) => {
        HttpUtil.get('dockerComposeServiceItem/delete', {id}).then(rs => {
            message.success('删除命令已发送')
            this.loadServices()
        })
    };

    onClickAdd = () => {
        this.setState({formOpen: true, formValues: {pid: this.id}})
        this.loadImageList()
    }
    onFinish = values => {
        HttpUtil.post('dockerComposeServiceItem/save', values).then(rs => {
            this.setState({formOpen: false})
            this.loadServices()
        })
    }

    loadImageList = (text) => {
        HttpUtil.get('image/options', {searchText: text}).then(rs => {
            this.setState({imageList: rs})
        })
    };
    loadImageTagList = (text) => {
        const url = this.formRef.current.getFieldValue('imageUrl')
        if (url) {
            HttpUtil.get('image/tagOptions', {url, searchText: text}).then(rs => {
                this.setState({imageTagList: rs})
            })
        } else {
            this.setState({imageTagList: []})
        }

    };

    onClickConfig = () => {
        this.setState({configOpen: true, configContent: null})
        HttpUtil.get('dockerCompose/configFile', {id: this.id}).then(rs => {
            this.setState({configContent: rs || ''})
        })
    }
    submitContent = () => {
        HttpUtil.post('dockerCompose/saveConfigFile', {id: this.id, content: this.state.configContent}).then(rs => {
            this.setState({configOpen: false, configContent: null})
            this.loadServices()
        })
    }

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
                                <Button onClick={this.onClickConfig}>配置文件</Button>
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
                                            onClick={() => this.onDeployClick(item)}>部署</Button>,
                                    <Button size='small'
                                            icon={<DeleteOutlined/>}
                                            onClick={() => this.delete(item.id)}></Button>
                                ]}
                                           onClick={() => this.onSelect(item)}
                                >
                                    <List.Item.Meta
                                        title={<div>
                                            <ActiveDot
                                            value={this.state.servicesStatus[item.containerName]}/> {item.name}
                                        </div>}
                                        description={<>
                                            {item.imageTag} &nbsp;
                                            {item.ports}</>}
                                    />
                                </List.Item>

                            )}></List>

                            <Gap/>

                            <Button icon={<PlusOutlined/>} color="default" variant="dashed" onClick={this.onClickAdd}>
                                添加容器
                            </Button>
                        </Splitter.Panel>
                        <Splitter.Panel>
                            {this.renderContainer()}
                        </Splitter.Panel>
                    </Splitter>
                </Card>

                <Modal title='添加容器'
                       open={this.state.formOpen}
                       onOk={() => this.formRef.current.submit()}
                       onCancel={() => this.setState({formOpen: false})}
                       destroyOnHidden
                       maskClosable={false}

                >

                    <Form ref={this.formRef} labelCol={{flex: '100px'}}
                          initialValues={this.state.formValues}
                          onFinish={this.onFinish}
                    >
                        <Form.Item name='pid' noStyle></Form.Item>

                        <Form.Item name='imageUrl' label='镜像' required rules={[{required: true}]}>
                            <AutoComplete options={this.state.imageList} onSearch={this.loadImageList}></AutoComplete>
                        </Form.Item>


                        <Form.Item name='imageTag' label='版本' required rules={[{required: true}]}>
                            <AutoComplete options={this.state.imageTagList}
                                          onSearch={this.loadImageTagList}></AutoComplete>
                        </Form.Item>


                    </Form>
                </Modal>


                <Modal title='容器编排配置文件'
                       open={this.state.configOpen}
                       onOk={this.submitContent}
                       onCancel={() => this.setState({configOpen: false})}
                       destroyOnHidden
                       maskClosable={false}
                       width={800}
                >
                    <p>
                        <Typography.Text>docker-compose 格式</Typography.Text>
                    </p>

                    {this.state.configContent == null ? <Spin/> :
                        <CodeMirrorEditor value={this.state.configContent}
                                          onChange={v => this.setState({configContent: v})}/>}

                </Modal>

                <Modal title='重新部署'
                       open={this.state.deployOpen}
                       onOk={this.deploy}
                       onCancel={() => this.setState({deployOpen: false})}
                       destroyOnHidden
                       maskClosable={false}

                       okButtonProps={{
                           loading: this.state.deployProcessing
                       }}

                >

                    部署版本：<AutoComplete
                        value={this.state.deployTag}
                        onChange={v=>this.setState({deployTag:v})}
                        options={this.state.deployTagList}
                        style={{width: 150}}
                        onSearch={this.loadDeployTagList}
                        placeholder='版本'
                    />

                </Modal>
            </Page>
        );
    }

    renderContainer = () => {
        let {info, curService} = this.state;
        if (curService == null) {
            return
        }
        let hostId = info.host?.id;

        let containerId = curService?.containerName;
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
