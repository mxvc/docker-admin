import {Button, Form, message, Modal, Popconfirm, Result, Skeleton} from 'antd';
import React from 'react';

import {ProTable} from "@ant-design/pro-components";
import {history} from "umi";
import {notPermitted} from "../../utils/SysConfig";
import {hutool} from "@moon-cn/hutool";

let api = '/api/project/';


export default class extends React.Component {

  state = {
    checkResult:null, // success, message

    formOpen: false,
    showEditForm: false,
    formValues: {},

  }
  actionRef = React.createRef();
  columns = [
    {
      title: '项目名称',
      dataIndex: 'name',
      sorter: 1,

      render: (name, row) => {
        return <a onClick={() => history.push('project/view?id=' + row.id)}>{name}</a>
      },

      formItemProps: {
        rules: [{required: true}]
      }
    },

    {
      title: 'git仓库',
      dataIndex: 'gitUrl',
      width: 250,
      sorter: true,
      formItemProps: {
        rules: [{required: true}],
      }
    },


    {
      title: '分支',
      dataIndex: 'branch',
    },

    {
      title: 'dockerfile',
      dataIndex: 'dockerfile',
    },

    {
      title: '最近更新',
      dataIndex: 'modifyTime',
      sorter: true,
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, row) => {
        return <>
          <Button
            type='link'
            disabled={notPermitted('project:save')}
            onClick={() => {
              this.setState({
                formOpen: true,
                formValues: row
              })
            }}>编辑</Button>
          &nbsp;
          <Popconfirm disabled={notPermitted('project:delete')} title="确定删除，删除后将不可恢复"
                      onConfirm={() => this.handleDelete(row)}>
            <a disabled={notPermitted('project:delete')}>删除</a>
          </Popconfirm>

        </>
      },
    },
  ];

  componentDidMount() {
    // 检查是否定义注册中心
    hutool.http.get('api/project/check').then(rs=>{
      this.setState({checkResult:rs})
    })
  }



  handleSave = value => {
    value.id = this.state.formValues.id
    hutool.http.post(api + 'save', value).then(rs => {
      this.state.formOpen = false;
      this.setState(this.state)
      this.reload();
    })
  }

  reload = () => {
    this.actionRef.current.reload();
  };
  handleDelete = (row) => {
    hutool.http.get(api + 'delete', {id: row.id}).then(rs => {
      message.info(rs.message)
      this.actionRef.current.reload();
    })
  }


  render() {
    let {formOpen, checkResult} = this.state

    if(!checkResult) {
      return  <Skeleton  />
    }

    if(!checkResult.success) {
      return  <Result title={checkResult.message}></Result>
    }

    return (<>
      <ProTable
        actionRef={this.actionRef}
        search={false}
        toolBarRender={(action, {selectedRows}) => [
          <Button disabled={notPermitted('project:save')}
                  type="primary" onClick={() => {
            this.setState({
              formOpen: true, formValues: {
                branch: 'master',
                dockerfile: 'Dockerfile'
              }
            })
          }}>
            新增
          </Button>,
        ]}
        request={(params, sort) => hutool.http.requestAntdSpringPageData(api + "list", params, sort)}
        columns={this.columns}
        rowSelection={false}
        rowKey="id"
        bordered={true}
        options={{search: true}}

      />


      <Modal
        maskClosable={false}
        destroyOnClose
        title='项目信息'
        open={formOpen}
        onCancel={() => {
          this.setState({formOpen: false})
        }}
        footer={null}
      >
        <ProTable
          type='form'
          form={{
            initialValues: this.state.formValues,
            layout: 'horizontal',
            labelCol: {flex: '100px'},
          }}
          onSubmit={this.handleSave}
          columns={this.columns}
        />
      </Modal>
    </>)
  }


}



