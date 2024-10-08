import {DownOutlined, PlusOutlined} from '@ant-design/icons';
import {Button, Divider, Dropdown, Modal, Popconfirm, Space, Switch, Tag} from 'antd';
import React from 'react';
import {ProTable} from "@ant-design/pro-components";
import hutool from "@moon-cn/hutool";

const addTitle = "添加注册中心"
const editTitle = '编辑注册中心'
const deleteTitle = '删除注册中心'
let api = '/api/registry/';


export default class extends React.Component {

  state = {
    showAddForm: false,
    showEditForm: false,
    formValues: {},
  }
  actionRef = React.createRef();

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
    },
    {
      title: '密码',
      dataIndex: 'password',
      valueType: 'password',
      hideInTable: true
    },
    {
      title: '是否默认',
      dataIndex: 'defaultRegistry',
      formItemProps: {
        valuePropName: 'checked'
      },
      renderFormItem() {
        return <Switch/>
      },
      render(v) {
        return v === true ? <Tag color='green'>默认</Tag> : ''
      }

    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        let menu = <div>
          <a key="1" onClick={() => {
            this.state.showEditForm = true;
            this.state.formValues = record;
            this.setState(this.state)
          }}>修改</a>
          <Divider type="vertical"></Divider>
          <Popconfirm title={'是否确定' + deleteTitle} onConfirm={() => this.handleDelete(record)}>
            <a>删除</a>
          </Popconfirm>
        </div>;


        return menu

      },
    },
  ];
  handleSave = value => {
    hutool.http.post(api + 'save', value).then(rs => {
      this.state.showAddForm = false;
      this.setState(this.state)
      this.actionRef.current.reload();
    })
  }

  handleUpdate = value => {
    let params = {...this.state.formValues, ...value};
    hutool.http.post(api + 'update', params).then(rs => {
      this.state.showEditForm = false;
      this.setState(this.state)
      this.actionRef.current.reload();
    })

  }

  handleDelete = record => {

    hutool.http.post(api + 'delete', {id:record.id}).then(rs => {
      this.actionRef.current.reload();
    })
  }

  render() {
    let {showAddForm, showEditForm} = this.state
    const items = [
      {
        key: '1',
        label: <a href="https://cr.console.aliyun.com" target="_blank">阿里云镜像仓库</a>,
      },
      {
        key: '2',
        label: <a href="https://www.tencentcloud.com/zh/products/tcr" target="_blank">腾讯云镜像仓库</a>,
      },

    ];
    return (<div>
      <div className="panel">
        <ProTable
          actionRef={this.actionRef}
          toolBarRender={(action, {selectedRows}) => <Space>
            <Button type="primary" onClick={() => {
              this.state.showAddForm = true;
              this.setState(this.state)
            }}>
              <PlusOutlined/> 新建
            </Button>

            <Dropdown
              menu={{
                items,
              }}
            >
              <a onClick={(e) => e.preventDefault()}>
                <Space>
                  常见中心仓库
                  <DownOutlined/>
                </Space>
              </a>
            </Dropdown>
          </Space>}
          request={(params, sort) => hutool.http.requestAntdSpringPageData(api + 'list', params, sort)}
          columns={this.columns}
          rowSelection={false}
          search={false}
          rowKey="id"
        />
      </div>
      <Modal
        maskClosable={false}
        destroyOnClose
        title={addTitle}
        visible={showAddForm}
        onCancel={() => {
          this.state.showAddForm = false;
          this.setState(this.state)
        }}
        footer={null}
      >
        <ProTable
          onSubmit={this.handleSave}
          type="form"
          columns={this.columns}
          rowSelection={false}
        />
      </Modal>


      <Modal
        maskClosable={false}
        destroyOnClose
        title={editTitle}
        visible={showEditForm}
        onCancel={() => {
          this.state.showEditForm = false;
          this.setState(this.state)
        }}
        footer={null}
      >
        <ProTable
          onSubmit={this.handleUpdate}
          form={{initialValues: this.state.formValues}}
          type="form"
          columns={this.columns}
          rowSelection={false}
        />
      </Modal>

    </div>)
  }


}



