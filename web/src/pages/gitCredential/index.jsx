import {PlusOutlined} from '@ant-design/icons'
import {Button, Card,InputNumber, Popconfirm,Modal,Form,Input,message} from 'antd'
import React from 'react'

import {ProTable} from '@tmgg/pro-table'
import {HttpUtil} from "@tmgg/tmgg-base"
import {ButtonList,FieldDictSelect,FieldRadioBoolean} from "@tmgg/tmgg-base";



export default class extends React.Component {

  state = {
    formValues: {},
    formOpen: false
  }

  formRef = React.createRef()
  tableRef = React.createRef()

  columns = [

    {
      title: '前缀',
      dataIndex: 'url',


    },

    {
      title: '账号',
      dataIndex: 'username',


    },

    {
      title: '密码',
      dataIndex: 'password',
        valueType: 'password',
        hideInSearch:true,
        hideInTable:true

    },

    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => (
          <ButtonList>
            <a perm='gitCredential:save' onClick={() => this.handleEdit(record)}> 修改 </a>
            <Popconfirm perm='gitCredential:delete' title='是否确定删除GIT凭据'  onConfirm={() => this.handleDelete(record)}>
              <a>删除</a>
            </Popconfirm>
          </ButtonList>
      ),
    },
  ]

  handleAdd = ()=>{
    this.setState({formOpen: true, formValues: {}})
  }

  handleEdit = record=>{
      this.setState({formOpen: true, formValues: record})
  }


  onFinish = values => {
    HttpUtil.post( 'gitCredential/save', values).then(rs => {
      this.setState({formOpen: false})
      this.tableRef.current.reload()
    })
  }



  handleDelete = record => {
    HttpUtil.post( 'gitCredential/delete', {id:record.id}).then(rs => {
      this.tableRef.current.reload()
    })
  }

  render() {
    return <>
      <ProTable
          actionRef={this.tableRef}
          toolBarRender={() => {
            return <ButtonList>
              <Button perm='gitCredential:save' type='primary' onClick={this.handleAdd}>
                <PlusOutlined/> 新增
              </Button>
            </ButtonList>
          }}
          request={(jobParamDescs, sort) => HttpUtil.pageData('gitCredential/page', jobParamDescs, sort)}
          columns={this.columns}
          rowKey='id'
      />

  <Modal title='GIT凭据'
    open={this.state.formOpen}
    onOk={() => this.formRef.current.submit()}
    onCancel={() => this.setState({formOpen: false})}
    destroyOnClose
    >

    <Form ref={this.formRef} labelCol={{flex: '100px'}}
        initialValues={this.state.formValues}
        onFinish={this.onFinish} >
        <Form.Item  name='id' noStyle></Form.Item>

              <Form.Item label='前缀' name='url' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='账号' name='username' rules={[{required: true}]}>
                    <Input/>
              </Form.Item>
              <Form.Item label='密码' name='password' rules={[{required: true}]}>
                    <Input.Password/>
              </Form.Item>

    </Form>
  </Modal>
    </>


  }
}



