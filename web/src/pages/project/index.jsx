import {PlusOutlined} from '@ant-design/icons'
import {Button, Divider, Form, Input, Modal, Popconfirm} from 'antd'
import React from 'react'

import {ProTable} from '@tmgg/pro-table'
import {ButtonList, FieldRadioBoolean, FieldRemoteSelect, HttpUtil} from "@tmgg/tmgg-base"


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [

        {
            title: '组织',
            dataIndex: 'sysOrg',


        },

        {
            title: '名称',
            dataIndex: 'name',


        },

        {
            title: 'gitUrl',
            dataIndex: 'gitUrl',


        },

        {
            title: 'dockerfile',
            dataIndex: 'dockerfile',


        },

        {
            title: '默认分支',
            dataIndex: 'branch',


        },

        {
            title: '注册中心',
            dataIndex: 'registry',


        },

        {
            title: '维护latest',
            dataIndex: 'autoPushLatest',

            valueType: 'boolean',

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

            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}>
                    <Form.Item name='id' noStyle></Form.Item>

                    <Form.Item label='组织' name='sysOrg' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='名称' name='name' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='gitUrl' name='gitUrl' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>

                    <Form.Item label='备注' name='registry' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>

                    <Divider >高级设置</Divider>
                    <Form.Item label='默认分支' name='branch' rules={[{required: true}]} initialValue='master'>
                        <Input/>
                    </Form.Item>

                    <Form.Item label='dockerfile' name='dockerfile' rules={[{required: true}]}
                               initialValue='Dockerfile'>
                        <Input/>
                    </Form.Item>


                    <Form.Item label='注册中心' name='registry' rules={[{required: true}]}>
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



