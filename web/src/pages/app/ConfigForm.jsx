import {Button, Col, Divider, Form, Input, message, Radio, Row, Spin} from "antd";
import React from "react";
import EditTable from "../../components/EditTable";
import CodeMirrorEditor from "../../components/CodeMirrorEditor";
import {HttpUtil} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {
        form: {
            ports: [],
            binds: [],
            environmentYAML: ''
        },


    }

    constructor(props) {
        super(props);
        const app = this.props.app
        this.state.form = app.config
    }


    portsColumns = [
        {title: '主机端口', dataIndex: 'publicPort', dataType: 'InputNumber'},
        {title: '容器端口', dataIndex: 'privatePort', dataType: 'InputNumber'},
        {title: '协议', dataIndex: 'protocol', dataType: 'Select', valueEnum: {TCP: 'TCP', UDP: 'UDP'}},
    ]

    bindsColumns = [
        {title: '主机路径', dataIndex: 'publicVolume', dataType: 'Input'},
        {title: '容器路径', dataIndex: 'privateVolume', dataType: 'Input'},
    ]
    update = () => {
        const {form} = this.state;
        const hide = message.loading("修改配置中...", 0)
        this.setState({form: null})
        HttpUtil.post('/app/updateConfig?id=' + this.props.app.id, form).then(rs => {
            const app = rs.data;
            this.setState({form: app.config})
            hide()
            this.props.onChange(app)
        })
    }

    render() {
        const {form} = this.state;
        if (!form) {
            return <Spin/>
        }
        return <>

            <Form colon={false}>
                <Form.Item label='网络模式' name='networkMode' initialValue='bridge'>
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
                    {(fm)=>{
                       const networkMode =  fm.getFieldValue('networkMode')
                        if(networkMode === 'bridge') {
                            return       <Form.Item label='端口映射' name='portsColumns' >
                                <EditTable columns={this.portsColumns} dataSource={form.ports}></EditTable>
                            </Form.Item>
                        }

                    }}

                </Form.Item>

                <Form.Item label='文件映射' name='portsColumns' >
                    <EditTable columns={this.bindsColumns} dataSource={form.binds}></EditTable>
                </Form.Item>

                <Form.Item label='环境变量' tooltip='yml格式' name='environmentYAML'>
                    <CodeMirrorEditor />
                </Form.Item>

                <Form.Item label='启动命令' name='cmd'>
                    <Input />
                </Form.Item>

                <Form.Item label='extraHosts' name='ExtraHosts' tooltip='域名IP映射,类似dns,hosts文件'>
                    <Input placeholder='域名:IP 域名2:IP2'/>
                </Form.Item>
            </Form>


            <Row>
                <Col flex="100px">
                </Col>
                <Col flex="auto">
                    <Button type="primary" onClick={this.update} size={"large"}>保存并重启</Button>
                </Col>
            </Row>


        </>

    }
}
