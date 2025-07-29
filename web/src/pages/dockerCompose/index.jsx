import {PlusOutlined} from '@ant-design/icons'
import {Button, Form, Input, Modal, Popconfirm} from 'antd'
import React from 'react'
import {ButtonList, FieldOrgTreeSelect, FieldSelect, HttpUtil, Page, PageUtil, ProTable} from "@tmgg/tmgg-base";
import CodeMirrorEditor from "../../components/CodeMirrorEditor";


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false
    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [

        {
            title: '名称',
            dataIndex: 'name',
            render: (name, row) => {
                return <a onClick={() => PageUtil.open('/dockerCompose/view?id=' + row.id,'容器编排-'+name)}>{name}</a>
            }

        },

        {
            title: '所属机构',
            dataIndex: ['sysOrg','name'],


        },

        {
            title: '运行主机',
            dataIndex: ['host','name'],
        },


        {
            title: '操作',
            dataIndex: 'option',
            render: (_, record) => (
                <ButtonList>

                    <Button size='small' perm='dockerCompose:save' onClick={() => this.handleEdit(record)}>编辑</Button>
                    <Popconfirm perm='dockerCompose:delete' title='是否确定删除容器编排'
                                onConfirm={() => this.handleDelete(record)}>
                        <Button size='small'>删除</Button>
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
        HttpUtil.post('dockerCompose/save', values).then(rs => {
            this.setState({formOpen: false})
            this.tableRef.current.reload()
        })
    }


    handleDelete = record => {
        HttpUtil.postForm('dockerCompose/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <Page>
            <ProTable
                actionRef={this.tableRef}
                toolBarRender={(params, {selectedRows, selectedRowKeys}) => {
                    return <ButtonList>
                        <Button perm='dockerCompose:save' type='primary' onClick={this.handleAdd}>
                            <PlusOutlined/> 新增
                        </Button>
                    </ButtonList>
                }}
                request={(params) => HttpUtil.pageData('dockerCompose/page', params)}
                columns={this.columns}

            />

            <Modal title='容器编排'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnHidden
                   maskClosable={false}
                   width='80vw'
            >

                <Form ref={this.formRef} labelCol={{flex: '100px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}
                >
                    <Form.Item name='id' noStyle></Form.Item>

                    <Form.Item label='名称' name='name' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>

                    <Form.Item name={['host', 'id']} label='部署主机' required rules={[{required: true}]}>
                        <FieldSelect showSearch url="host/options"/>
                    </Form.Item>

                    <Form.Item label='所属组织' name={['sysOrg', 'id']} rules={[{required: true}]}>
                        <FieldOrgTreeSelect/>
                    </Form.Item>


                    <Form.Item label='内容' name='content' rules={[{required: true}]} >
                        <CodeMirrorEditor/>
                    </Form.Item>
                </Form>
            </Modal>
        </Page>


    }
}


