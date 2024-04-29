import {PlusOutlined} from '@ant-design/icons';
import {AutoComplete, Button, Form, Input, message, Modal} from 'antd';
import React from 'react';
import {get, getPageableData, post} from "../../utils/request";
import ContainerStatus from "../../components/ContainerStatus";
import {ProTable} from "@ant-design/pro-components";
import {history} from "umi";
import {notPermitted} from "../../utils/SysConfig";
import ProjectDeploy from "./ProjectDeploy";
import ImageDeploy from "./ImageDeploy";
import RemoteSelect from "../../components/RemoteSelect";

let api = '/api/app/';


export default class extends React.Component {

  actionRef = React.createRef();

  columns = [
    {
      title: '应用名称',
      dataIndex: 'name',
      sorter: true,
      render: (name, row) => {
        return <a onClick={() => history.push('app/view?id=' + row.id)}>{name}</a>
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
    versions: []
  }
  reload = () => {
    this.actionRef.current.reload()
  }

  loadVersions = projectId => {
    get('api/project/versions', {projectId}).then(rs => {
      this.setState({versions: rs})
    })
  };

  handleSave = value => {
    post(api + 'saveByProject', value).then(rs => {
      message.success(rs.message)
      history.push('/app/view?id=' + rs.data)
    })
  }

  formRef = React.createRef()

  render() {
    return (
      <>
        <ProTable
          actionRef={this.actionRef}
          toolBarRender={(action, {selectedRows}) => [
            <Button disabled={notPermitted('app:save')} type="primary"
                    onClick={() => {
                      this.setState({deployVisible: true})
                    }}>
              新增
            </Button>,
          ]}
          request={(params, sort) => getPageableData(api + 'list', params, sort)}
          columns={this.columns}
          rowSelection={false}
          rowKey="id"
          bordered={true}
          search={false}
          options={{search: true}}
        />
        <Modal title='部署项目' open={this.state.deployVisible} destroyOnClose={true} footer={null}
               onCancel={() => this.setState({deployVisible: false})}>
          <Form
            layout='horizontal'
            labelCol={{flex:'100px'}}
            ref={this.formRef}
            onValuesChange={changedValues => {
              if (changedValues.project != null) {
                this.loadVersions(changedValues.project.id);
              }
            }}
            onFinish={this.handleSave}
          >

            <Form.Item name={['project', 'id']} label='项目' required rules={[{required: true}]}>
              <RemoteSelect url='/api/project/options' placeholder='请选择项目'></RemoteSelect>
            </Form.Item>
            <Form.Item name='imageTag' label='镜像版本' required rules={[{required: true}]}>
              <AutoComplete options={this.state.versions} placeholder='请选择或输入镜像版本'/>
            </Form.Item>


            <Form.Item name={['host', 'id']} label='部署主机' required rules={[{required: true}]}>
              <RemoteSelect showSearch url="/api/host/options"/>
            </Form.Item>


            <Form.Item name='name' label='应用名称' required rules={[{required: true}]}>
              <Input/>
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
