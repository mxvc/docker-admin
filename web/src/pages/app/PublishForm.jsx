import React from "react";
import {
    AutoComplete,
    Button,
    Divider,
    Form,
    Row,
    Skeleton,
    Switch,
    Collapse,
    message,
    Typography,
    Modal
} from "antd";
import {FieldRemoteSelect, Gap, HttpUtil, PageUtil} from "@tmgg/tmgg-base";

/**
 * 发布标签页
 */
export default class extends React.Component {

    state = {
        app: {},
        appLoading: true,

        tagOptions: []
    }

    componentDidMount() {
        this.id = this.props.appId

        this.setState({appLoading: true})
        HttpUtil.get('/app/get', {id: this.id}).then(rs => {
            this.setState({app: rs})

            HttpUtil.get('image/tagOptions', {url: rs.imageUrl}).then(tagOptions => {
                this.setState({tagOptions})
            })

        }).finally(() => {
            this.setState({appLoading: false})
        })
    }

    setAutoDeploy = (autoDeploy) => {
        HttpUtil.get("app/autoDeploy", {id: this.id, autoDeploy}).then(rs => {
            this.props.onChange()
        })
    }


    updateVersion = (values) => {
        HttpUtil.get("app/updateVersion", {id: this.id, version: values.imageTag}).then(rs => {
            this.props.onChange()
        })
    }
    copyApp = (values) => {
        const hide = message.loading('复制中..', 0)
        HttpUtil.post("app/copyApp", {appId: this.id, hostId: values.hostId}).then(rs => {
            const newAppId = rs.id;
            Modal.confirm({
                icon: null,
                title:'复制完成',
                content:'是否打开新的应用？',
                onOk(){
                    PageUtil.open('/app/view?id='+newAppId,'应用-' + rs.name)
                }
            })
        }).finally(hide)
    }


    render() {
        const {app, appLoading} = this.state;

        if (appLoading) {
            return <Skeleton active={true}/>
        }


        return <>
            <Collapse defaultActiveKey={['1']} accordion   items={[
                {
                    key:'1',
                    label:'手动发布',
                    children:<Form onFinish={this.updateVersion}>
                        <Form.Item  name='imageTag' help='用指定的镜像版本' rules={[{required: true}]}>
                            <AutoComplete options={this.state.tagOptions}
                                          style={{width: 150}}
                                          placeholder={app.imageTag}
                            />
                        </Form.Item>
                        <Form.Item label=' '>
                            <Button type="primary" danger htmlType='submit'>更新应用</Button>
                        </Form.Item>
                    </Form>
                },
                {
                    key: '2',
                    label: '自动发布',
                    children: <Form
                                    onValuesChange={changedValues => this.setAutoDeploy(changedValues.autoDeploy)}
                    >
                        <Form.Item
                                   name='autoDeploy'
                                   help='当有镜像构建成功后，自动更新应用到最新构建的版本'
                                   valuePropName="checked"
                                   initialValue={app.autoDeploy}>
                            <Switch/>
                        </Form.Item>
                    </Form>,
                },

                {
                    key:'3',
                    label:'复制应用',
                    children:<>
                            <Form onFinish={this.copyApp}>
                                <Form.Item name='hostId' rules={[{required:true}]} >
                                    <FieldRemoteSelect url='/host/options' placeholder='请选择新主机' style={{width:300}} />
                                </Form.Item>
                                <div>
                                <Typography.Text italic>注意：复制应用不会自动部署，也不会复制主机上的文件</Typography.Text>
                                </div>
                                <Gap />
                                <Button type="primary" danger htmlType='submit'>确定复制</Button>
                            </Form>
                    </>
                }
            ]}>

            </Collapse>




        </>
    }
}
