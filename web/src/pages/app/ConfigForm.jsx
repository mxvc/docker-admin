import {Button, Col, Divider, Form, Input, message, Radio, Row, Spin} from "antd";
import React from "react";
import EditTable from "../../components/EditTable";
import CodeMirrorEditor from "../../components/CodeMirrorEditor";
import {HttpUtil} from "@tmgg/tmgg-base";


export default class extends React.Component {


    portsColumns = [
        {title: '主机端口', dataIndex: 'publicPort', dataType: 'InputNumber'},
        {title: '容器端口', dataIndex: 'privatePort', dataType: 'InputNumber'},
        {title: '协议', dataIndex: 'protocol', dataType: 'Select', valueEnum: {TCP: 'TCP', UDP: 'UDP'}},
    ]

    bindsColumns = [
        {title: '主机路径', dataIndex: 'publicVolume', dataType: 'Input'},
        {title: '容器路径', dataIndex: 'privateVolume', dataType: 'Input'},
    ]
    update = (form) => {
        const hide = message.loading("修改配置中...", 0)
        HttpUtil.post('/app/updateConfig?id=' + this.props.app.id, form).then(app => {
            this.props.onChange(app)
        }).finally(hide)
    }

    formRef = React.createRef()

    render() {
        if (!this.props.app.config) {
            return <Spin/>
        }
        return <>

            <Form ref={this.formRef} colon={false} labelCol={{flex: '100px'}} onFinish={this.update} onValuesChange={console.log} initialValues={this.props.app.config}>
                <Form.Item label='网络模式' name='networkMode'>
                    <Radio.Group
                        options={[
                            {label: '桥接模式', value: 'bridge',},
                            {label: '主机模式', value: 'host',},
                            {label: '无需网络', value: 'none',},
                        ]}
                    >
                    </Radio.Group>
                </Form.Item>

                <Form.Item noStyle dependencies={['networkMode']}>
                    {(fm) => {
                        const networkMode = fm.getFieldValue('networkMode')
                        if (networkMode === 'bridge') {
                            return <Form.Item label='端口映射' name='ports'>
                                <EditTable columns={this.portsColumns} />
                            </Form.Item>
                        }
                    }}
                </Form.Item>

                <Form.Item label='文件映射' name='binds'  >
                    <EditTable columns={this.bindsColumns} />
                </Form.Item>

                <Form.Item label='环境变量' tooltip='yml格式' name='environmentYAML'>
                    <CodeMirrorEditor/>
                </Form.Item>

                <Form.Item label='启动命令' name='cmd'>
                    <Input/>
                </Form.Item>

                <Form.Item label='extraHosts' name='ExtraHosts' tooltip='域名IP映射,类似dns,hosts文件'>
                    <Input placeholder='域名:IP 域名2:IP2'/>
                </Form.Item>


                <Form.Item label=' '>
                    <Button type="primary" danger htmlType='submit' size={"large"}>保存并重启</Button>
                </Form.Item>
            </Form>



        </>

    }
}
