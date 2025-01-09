import {PlusOutlined} from '@ant-design/icons'
import {Button, Divider, Form, Input, Modal, Popconfirm} from 'antd'
import React from 'react'

import {ButtonList, FieldOrgTreeSelect, FieldRadioBoolean, FieldRemoteSelect, HttpUtil, ProTable} from "@tmgg/tmgg-base"
import {history} from "umi";


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false,
        registryOptions:[],
        defaultRegistryId:null
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [



        {
            title: '名称',
            dataIndex: 'name',
            render: (name, row) => {
                return <a onClick={() => history.push('/project/view?id=' + row.id)}>{name}</a>
            },

        },

        {
            title: 'gitUrl',
            dataIndex: 'gitUrl',            hideInSearch: true
        },
        {
            title: '备注',
            dataIndex: 'remark',
        },

        {
            title: '默认分支',
            dataIndex: 'branch',
            hideInSearch: true
        },
        {
            title: 'dockerfile',
            dataIndex: 'dockerfile',            hideInSearch: true
        },



        {
            title: '注册中心',
            dataIndex: ['registry','fullUrl'],
            hideInSearch:true
        },

        {
            title: '维护latest',
            dataIndex: 'autoPushLatest',

            valueType: 'boolean',
            hideInSearch:true
        },
        {
            title: '组织',
            dataIndex: ['sysOrg','name'],
            renderFormItem(){
                return <FieldOrgTreeSelect />
            }
        },
        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, record) => (
                <ButtonList>
                    <a perm='project:save' onClick={() => this.handleEdit(record)}> 修改 </a>
                    <Popconfirm perm='project:delete' title='是否确定删除项目'
                                onConfirm={() => this.handleDelete(record)}>
                        <a>删除</a>
                    </Popconfirm>
                </ButtonList>
            ),
        },
    ]

    componentDidMount() {
        HttpUtil.get('registry/options').then(rs=>{
            this.setState({registryOptions: rs})
            if(rs.length){
                this.setState({defaultRegistryId: rs[0].value})
            }
        })
    }

    handleAdd = () => {
        this.setState({formOpen: true, formValues: {}})
    }

    handleEdit = record => {
        this.setState({formOpen: true, formValues: record})
    }


    onFinish = values => {
        HttpUtil.post('project/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = record => {
        HttpUtil.post('project/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='project:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(jobParamDescs, sort) => HttpUtil.pageData('project/page', jobParamDescs, sort)}
                columns={this.columns}
                rowKey='id'
            />

            <Modal title='项目'
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

                    <Form.Item label='gitUrl' name='gitUrl' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='名称' name='name' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='备注' name='remark'>
                        <Input/>
                    </Form.Item>

                    <Divider >其他</Divider>
                    <Form.Item label='组织' name={['sysOrg','id']} rules={[{required: true}]}>
                       <FieldOrgTreeSelect />
                    </Form.Item>
                    <Form.Item label='默认分支' name='branch' rules={[{required: true}]} initialValue='master'>
                        <Input/>
                    </Form.Item>

                    <Form.Item label='dockerfile' name='dockerfile' rules={[{required: true}]}
                               initialValue='Dockerfile'>
                        <Input/>
                    </Form.Item>




                    <Form.Item label='注册中心' name={['registry','id']} rules={[{required: true}]} initialValue={this.state.defaultRegistryId}>
                        <FieldRemoteSelect url='registry/options' />
                    </Form.Item>
                    <Form.Item label='维护latest' name='autoPushLatest' rules={[{required: true}]} initialValue={false}>
                        <FieldRadioBoolean/>
                    </Form.Item>

                </Form>
            </Modal>
        </>


    }
}



