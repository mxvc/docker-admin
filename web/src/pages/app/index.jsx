import {AutoComplete, Button, Form, Input, message, Modal, Radio} from 'antd';
import React from 'react';
import ContainerStatus from "../../components/ContainerStatus";
import {history} from "umi";
import {notPermitted} from "../../utils/SysConfig";
import {FieldOrgTreeSelect, FieldRemoteSelect, HttpUtil, ProTable} from "@tmgg/tmgg-base";

let api = 'app/';


export default class extends React.Component {

    actionRef = React.createRef();

    columns = [
        {
            title: '应用名称',
            dataIndex: 'name',
            sorter: true,
            render: (name, row) => {
                return <a onClick={() => history.push('/app/view?id=' + row.id)}>{name}</a>
            }
        },
        {
            title: '主机',
            dataIndex: 'host',
            sorter: true,
            hideInForm: true,
            render(v) {
                return v.name
            },
        },
        {
            title: '主机',
            dataIndex: 'hostId',
            hideInTable: true,
        },
        {
            title: '镜像',
            dataIndex: 'imageUrl',
            sorter: true,
            render: (_, record) => {
                if (record.imageUrl) {
                    return record.imageUrl
                }
                return <a onClick={() => history.push('project/view?id=' + record.project.id)}>{record.project.name}</a>
            }
        },
        {
            title: '版本',
            dataIndex: 'imageTag',
        },
        {
            title: '状态',
            dataIndex: 'containerStatus',
            hideInForm: true,
            render: (_, row) => {
                return <ContainerStatus hostId={row.host?.id} appName={row.name}></ContainerStatus>
            }
        },
        {
            title: '最近更新',
            dataIndex: 'modifyTime',
        },



    ];
    state = {

        deployVisible: false,
        deployImageVisible: false,


        imageList:[],
        imageTagList:[]
    }


    reload = () => {
        this.actionRef.current.reload()
    }



    handleSave = value => {
        HttpUtil.post(api + 'save', value).then(rs => {
            this.reload()
            this.setState({deployVisible: false})
        })
    }

    formRef = React.createRef()

    handleAdd =()=>{
        this.setState({deployVisible: true})
        this.loadImageList();
    }

    loadImageList = (text) => {
        HttpUtil.get('image/options',{keyword:text}).then(rs => {
            this.setState({imageList: rs})
        })
    };
    loadImageTagList = (text) => {
        const url = this.formRef.current.getFieldValue('imageUrl')
        if(url){
            HttpUtil.get('image/tagOptions',{url,keyword:text}).then(rs => {
                this.setState({imageTagList: rs})
            })
        }else {
            this.setState({imageTagList:[]})
        }

    };
    render() {
        return (
            <>
                <ProTable
                    actionRef={this.actionRef}
                    toolBarRender={(action, {selectedRows}) => [
                        <Button disabled={notPermitted('app:save')} type="primary"
                                onClick={this.handleAdd}>
                            新增
                        </Button>,
                    ]}
                    request={(params, sort) => HttpUtil.pageData(api + 'list', params, sort)}
                    columns={this.columns}
                    rowSelection={false}
                    rowKey="id"
                    bordered={true}
                    search={false}
                    options={{search: true}}
                />
                <Modal title='新增应用' open={this.state.deployVisible} destroyOnClose={true} footer={null}
                       onCancel={() => this.setState({deployVisible: false})}>
                    <Form
                        layout='horizontal'
                        labelCol={{flex: '100px'}}
                        ref={this.formRef}
                        onValuesChange={changedValues => {
                            if (changedValues.imageUrl != null) {
                                this.loadImageTagList();
                            }
                        }}
                        onFinish={this.handleSave}
                    >


                        <Form.Item name='imageUrl' label='镜像' required rules={[{required: true}]}>
                            <AutoComplete options={this.state.imageList} onSearch={this.loadImageList}></AutoComplete>
                        </Form.Item>


                        <Form.Item name='imageTag' label='版本' required rules={[{required: true}]}>
                            <AutoComplete options={this.state.imageTagList} onSearch={this.loadImageTagList}></AutoComplete>
                        </Form.Item>


                        <Form.Item name={['host', 'id']} label='部署主机' required rules={[{required: true}]}>
                            <FieldRemoteSelect showSearch url="host/options"/>
                        </Form.Item>


                        <Form.Item name='name' label='应用名称' required rules={[{required: true}]}>
                            <Input/>
                        </Form.Item>
                        <Form.Item label='组织' name={['sysOrg','id']} rules={[{required: true}]}>
                            <FieldOrgTreeSelect />
                        </Form.Item>
                        <Form.Item>
                            <Button htmlType='submit' type='primary'>确定</Button>
                        </Form.Item>
                    </Form>
                </Modal>


            </>

        )
    }

}
