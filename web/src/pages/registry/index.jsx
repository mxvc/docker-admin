import {PlusOutlined} from '@ant-design/icons'
import {Button, Divider, Form, Input, Modal, Popconfirm} from 'antd'
import React from 'react'

import {ButtonList, FieldRadioBoolean, HttpUtil, ProTable} from "@tmgg/tmgg-base"


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [

        {
            title: '仓库地址',
            dataIndex: 'url',


        },

        {
            title: '命名空间',
            dataIndex: 'namespace',


        },

        {
            title: '账号',
            dataIndex: 'username',

            hideInSearch: true
        },


        {
            title: '是否默认',
            dataIndex: 'defaultRegistry',
            render(v) {
                return v ? '是' : '否'
            }
        },

        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, record) => (
                <ButtonList>
                    <a perm='registry:save' onClick={() => this.handleEdit(record)}> 修改 </a>
                    <Popconfirm perm='registry:delete' title='是否确定删除Registry'
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
        HttpUtil.post('registry/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = record => {
        HttpUtil.post('registry/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={() => {
                    return <ButtonList>
                        <Button perm='registry:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(jobParamDescs, sort) => HttpUtil.pageData('registry/page', jobParamDescs, sort)}
                columns={this.columns}
                rowKey='id'
                search={false}
            />

            <Modal title='Registry'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnClose
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}>
                    <Form.Item name='id' noStyle></Form.Item>

                    <Form.Item label='仓库地址' name='url' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='命名空间' name='namespace' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='区域' name='region' rules={[{required: true}]}
                               tooltip={'阿里云:例如cn-hangzhou，通常在仓库地址中有。腾讯云：如ap-guangzhou'}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='是否默认' name='defaultRegistry' rules={[{required: true}]}>
                        <FieldRadioBoolean/>
                    </Form.Item>


                    <Form.Item label='账号' name='username' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='密码' name='password' rules={[{required: true}]}>
                        <Input.Password/>
                    </Form.Item>


                    <Divider>接口信息</Divider>
                    <Form.Item label='ak' name='ak' rules={[{required: true}]}>
                        <Input.Password/>
                    </Form.Item>
                    <Form.Item label='sk' name='sk' rules={[{required: true}]}>
                        <Input.Password/>
                    </Form.Item>


                </Form>
            </Modal>
        </>


    }
}



