import {PlusOutlined} from '@ant-design/icons';
import {Button, Divider, message, Modal, Popconfirm, Radio} from 'antd';
import React from 'react';

import {getPageableData, post} from "../../utils/request";
import {ProTable} from "@ant-design/pro-components";
import {history} from "umi";

const addTitle = "添加主机"
let api = '/api/host/';


export default class extends React.Component {

  state = {
    formOpen: false,
    showEditForm: false,
    formValues: {},
  }
  actionRef = React.createRef();
  editFormRef = React.createRef()

  columns = [
    {
      title: '主机名称',
      dataIndex: 'name',
      rules: [
        {
          required: true,
          message: '主机名称为必填项',
        },
      ],
      render(name, row) {
        return <a onClick={() => history.push('host/view?id=' + row.id)}>{name}</a>
      }
    },
    {
      title: 'Docker接口',
      dataIndex: 'dockerHost',
      tooltip: <div style={{width: 500}}>本机：unix:///var/run/docker.sock <br/>IP方式：tcp://192.168.1.2:2375</div>,
      rules: [
        {
          required: true,
          message: '唯一标识',
        },
      ],

    },
    {
      title: '请求头Host',
      dataIndex: 'dockerHostHeader',
    },
    {
      title: '备注',
      dataIndex: 'remark',
    },
    {
      title: '是否构建主机',
      dataIndex: 'isRunner',
      renderFormItem() {
        return <Radio.Group>
          <Radio value={true}>是</Radio>
          <Radio value={false}>否</Radio>
        </Radio.Group>
      },
      render(v, row) {
        return v ? '是' : '否';
      }
    },

    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        return <div>

          <a onClick={() => this.handleEdit(record)}>修改</a>

          <Divider type='vertical'/>
          <Popconfirm title='确定删除' onConfirm={() => this.handleDelete(record)}>
            <a>删除</a>
          </Popconfirm>

        </div>
      },
    },
  ];


  clickAddBtn = () => {
    this.setState({formOpen: true, formValues: {}})
  }
  handleDelete = row => {
    post(api + 'delete', row).then(rs => {
      message.success(rs.message)
      this.actionRef.current.reload();
    }).catch(rs => {
      message.error(rs.message)
    })
  }

  handleSave = value => {

    post(api + 'save', value).then(rs => {
      this.state.formOpen = false;
      this.setState(this.state)
      this.actionRef.current.reload();
    })
  }

  handleEdit = row => {
    this.setState({
      showEditForm: true,
      formValues: row
    }, () => {
      this.editFormRef.current.setFieldsValue(row)
    })


  }

  handleUpdate = value => {
    value.id = this.state.formValues.id
    post(api + 'save', value).then(rs => {
      this.state.showEditForm = false;
      this.setState(this.state)
      this.actionRef.current.reload();
    })
  }

  render() {
    let {formOpen, showEditForm} = this.state
    return (<>
      <ProTable
        actionRef={this.actionRef}

        toolBarRender={(action, {selectedRows}) => [
          <Button type="primary"
                  onClick={() => this.setState({formOpen: true, formValues: {}})}>
            <PlusOutlined/> 新增
          </Button>,
        ]}
        request={(params, sort) => getPageableData(api + "list", params, sort)}

        columns={this.columns}
        rowSelection={false}
        rowKey="id"
        bordered={true}
        search={false}
      />


      <Modal
        maskClosable={false}
        title='主机信息'
        width={800}
        open={formOpen}
        onCancel={() => {
          this.state.formOpen = false;
          this.setState(this.state)
        }}
        footer={null}
      >
        <ProTable
          form={{initialValues: this.state.formValues}}
          type='form'
          onSubmit={this.handleSave}
          columns={this.columns}
        />
      </Modal>


    </>)
  }


}



