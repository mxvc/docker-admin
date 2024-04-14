import {PlusOutlined} from '@ant-design/icons';
import {Button, Modal} from 'antd';
import React from 'react';
import {getPageableData} from "../../utils/request";
import ContainerStatus from "../../components/ContainerStatus";
import {ProTable} from "@ant-design/pro-components";
import {history} from "umi";
import {notPermitted} from "../../utils/SysConfig";
import ProjectDeploy from "./ProjectDeploy";
import DockerhubDeploy from "./DockerhubDeploy";

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
    deployVisible: false
  }
  reload = () => {
    this.actionRef.current.reload()
  }

  render() {
    return (
      <>
        <ProTable
          actionRef={this.actionRef}
          toolBarRender={(action, {selectedRows}) => [
            <Button disabled={notPermitted('app:save')} type="primary" onClick={() => {
              this.setState({deployVisible: true})
            }}>
              <PlusOutlined/> 部署项目
            </Button>,
            <Button disabled={notPermitted('app:save')}  onClick={() => {
              this.setState({deployVisible: true})
            }}>
            部署公共镜像
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
        <Modal title='部署项目应用' open={this.state.deployVisible} destroyOnClose={true} footer={null}
               onCancel={() => this.setState({deployVisible: false})}>
          <ProjectDeploy/>
        </Modal>

        <Modal title='部署公共镜像' open={this.state.deployVisible} destroyOnClose={true} footer={null}
               onCancel={() => this.setState({deployVisible: false})}>
          <DockerhubDeploy/>
        </Modal>
      </>

    )
  }

}
