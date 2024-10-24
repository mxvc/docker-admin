import React from "react";
import {AutoComplete, Button, Divider, Form, Skeleton, Switch} from "antd";
import {HttpUtil} from "@tmgg/tmgg-base";

export default class extends React.Component {

    state = {
        app: {},
        appLoading: true,

        tagOptions:[]
    }

    componentDidMount() {
        this.id = this.props.appId

        this.setState({appLoading: true})
        HttpUtil.get('/app/get', {id: this.id}).then(rs => {
            this.setState({app: rs})

            HttpUtil.get('image/tagOptions', {url: rs.imageUrl}).then(tagOptions=>{
                this.setState({tagOptions})
            })

        }).finally(() => {
            this.setState({appLoading: false})
        })
    }

    setAutoDeploy = ( autoDeploy) => {
        HttpUtil.get("app/autoDeploy", {id: this.id, autoDeploy}).then(rs=>{
            this.props.onChange()
        })
    }


    updateVersion = (values) => {
        HttpUtil.get("app/updateVersion", {id:this.id, version: values.imageTag}).then(rs => {
            this.props.onChange()
        })
    }


    render() {
        const {app, appLoading} = this.state;

        if (appLoading) {
            return <Skeleton active={true}/>
        }


        return <>
            <Form labelCol={{flex:'100px'}}
                  onValuesChange={changedValues => this.setAutoDeploy(changedValues.autoDeploy)}
            >
                <Form.Item label='自动发布'
                           name='autoDeploy'
                           help='当有镜像构建成功后，自动更新应用到最新构建的版本'
                           valuePropName="checked"
                           initialValue={app.autoDeploy} >
                    <Switch/>
                </Form.Item>
            </Form>
            <Divider/>
            <Form labelCol={{flex:'100px'}} onFinish={this.updateVersion}>
                <Form.Item label='手动发布' name='imageTag'  help='用指定的镜像版本' rules={[{required:true}]}>
                    <AutoComplete options={this.state.tagOptions}
                                  style={{width: 150}}
                                  placeholder={app.imageTag}
                                />
                </Form.Item>
                <Form.Item label=' '>
                    <Button type="primary" danger htmlType='submit' >更新应用</Button>
                </Form.Item>
            </Form>

        </>
    }
}
