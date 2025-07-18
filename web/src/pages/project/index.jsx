import {PlusOutlined} from '@ant-design/icons'
import {Button, Form, Input, Modal, Popconfirm, Splitter} from 'antd'
import React from 'react'

import {
    ButtonList,
    FieldOrgTreeSelect,
    FieldRadioBoolean,
    FieldSelect,
    HttpUtil,
    OrgTree,
    PageUtil,
    ProTable
} from "@tmgg/tmgg-base"


export default class extends React.Component {

    state = {
        formValues: {},
        formOpen: false,
        registryOptions: [],
        defaultRegistryId: null,


        selectedOrgId: null

    }

    formRef = React.createRef()
    tableRef = React.createRef()

    columns = [


        {
            title: '名称',
            dataIndex: 'name',
            render: (name, row) => {
                return <a onClick={() => PageUtil.open('/project/view?id=' + row.id, "项目-" + name)}>{name}</a>
            },

        },

        {
            title: 'git仓库',
            dataIndex: 'gitUrl',
        },
        {
            title: '分支',
            dataIndex: 'branch',
            hideInSearch: true
        },
        {
            title: '备注',
            dataIndex: 'remark',
        },


        {
            title: 'dockerfile',
            dataIndex: 'dockerfile',
        },


        {
            title: '注册中心',
            dataIndex: ['registry', 'namespace'],
        },

        {
            title: '推送latest',
            dataIndex: 'autoPushLatest',
            render(v) {
                return v ? '是' : '否'
            }

        },
        {
            title: '组织机构',
            dataIndex: ['sysOrg', 'name'],

        },

        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_, record) => (
                <ButtonList>
                    <Button size='small' perm='project:save' onClick={() => this.handleEdit(record)}> 修改 </Button>
                    <Popconfirm perm='project:delete' title='是否确定删除项目'
                                onConfirm={() => this.handleDelete(record)}>
                        <Button size='small'>删除</Button>
                    </Popconfirm>
                </ButtonList>
            ),
        },
    ]

    componentDidMount() {
        HttpUtil.get('registry/options').then(rs => {
            this.setState({registryOptions: rs})
            if (rs.length) {
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
        HttpUtil.postForm('project/delete', {id: record.id}).then(rs => {
            this.tableRef.current.reload()
        })
    }

    render() {
        return <>
            <Splitter>
                <Splitter.Panel size={250}>
                    <OrgTree onChange={(v) => {
                        this.setState({selectedOrgId: v}, () => {
                            this.tableRef.current.reload()
                        })

                    }}/>

                </Splitter.Panel>
                <Splitter.Panel style={{paddingLeft: 16}}>
                    <ProTable
                        actionRef={this.tableRef}
                        toolBarRender={() => {
                            return <ButtonList>
                                <Button perm='project:save' type='primary' onClick={this.handleAdd}>
                                    <PlusOutlined/> 新增
                                </Button>
                            </ButtonList>
                        }}
                        request={(params) => {
                            params.orgId = this.state.selectedOrgId
                            return HttpUtil.pageData('project/page', params);
                        }}
                        columns={this.columns}
                        rowKey='id'
                    />
                </Splitter.Panel>
            </Splitter>


            <Modal title='项目信息'
                   open={this.state.formOpen}
                   onOk={() => this.formRef.current.submit()}
                   onCancel={() => this.setState({formOpen: false})}
                   destroyOnClose

                   width={600}

            >

                <Form ref={this.formRef} labelCol={{flex: '120px'}}
                      initialValues={this.state.formValues}
                      onFinish={this.onFinish}>
                    <Form.Item name='id' noStyle></Form.Item>
                    <Form.Item label='名称' name='name' rules={[{required: true}]} help='不能包含中文，小写字母开头'>
                        <Input/>
                    </Form.Item>

                    <Form.Item label='gi仓库' name='gitUrl' rules={[{required: true}]}>
                        <Input/>
                    </Form.Item>

                    <Form.Item label='分支' name='branch' rules={[{required: true}]} initialValue='master'>
                        <Input/>
                    </Form.Item>

                    <Form.Item label='dockerfile' name='dockerfile' rules={[{required: true}]}
                               initialValue='Dockerfile'>
                        <Input/>
                    </Form.Item>
                    <Form.Item label='构建参数' name='buildArg' help='格式: key=value&key2=value2'>
                        <Input/>
                    </Form.Item>


                    <Form.Item label='推送注册中心' name={['registry', 'id']} rules={[{required: true}]}
                               initialValue={this.state.defaultRegistryId}>
                        <FieldSelect url='registry/options'/>
                    </Form.Item>


                    <Form.Item label='推送latest版本' name='autoPushLatest' rules={[{required: true}]}
                               initialValue={false}>
                        <FieldRadioBoolean/>
                    </Form.Item>
                    <Form.Item label='所属组织' name={['sysOrg', 'id']}>
                        <FieldOrgTreeSelect/>
                    </Form.Item>

                    <Form.Item label='备注' name='remark'>
                        <Input/>
                    </Form.Item>
                </Form>
            </Modal>
        </>


    }
}



