import {AutoComplete, Button, Form, Input, message, Modal, Radio} from 'antd';
import React from 'react';
import ContainerStatus from "../../components/ContainerStatus";
import {ProTable} from "@ant-design/pro-components";
import {history} from "umi";
import {notPermitted} from "../../utils/SysConfig";
import RemoteSelect from "../../components/RemoteSelect";
import hutool from "@moon-cn/hutool";

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
      title: '镜像 / 项目',
      dataIndex: 'imageUrl',
      sorter: true,
      render: (_, record) => {
        if (record.imageUrl){
          return record.imageUrl
        }
        return <a onClick={() => history.push('project/view?id=' + record.project.id)}>{record.project.name}</a>
    }
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

    appType: 'project', // project ,image

    versions: []
  }
  reload = () => {
    this.actionRef.current.reload()
  }

  loadVersions = projectId => {
    hutool.http. get('api/project/versions', {projectId}).then(rs => {
      this.setState({versions: rs})
    })
  };

  handleSave = value => {
    hutool.http.post(api + 'save', value).then(rs => {
      message.success(rs.message)
      this.reload()
      this.setState({deployVisible:false})
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
          request={(params, sort) => hutool.http.requestAntdSpringPageData(api + 'list', params, sort)}
          columns={this.columns}
          rowSelection={false}
          rowKey="id"
          bordered={true}
          search={false}
          options={{search: true}}
        />
        <Modal title='新增应用' open={this.state.deployVisible} destroyOnClose={true} footer={null}
               onCancel={() => this.setState({deployVisible: false})}>
          <Form
            layout='horizontal'
            labelCol={{flex: '100px'}}
            ref={this.formRef}
            onValuesChange={changedValues => {
              if (changedValues.project != null) {
                this.loadVersions(changedValues.project.id);
              }
            }}
            onFinish={this.handleSave}
          >

            <Radio.Group style={{marginLeft:120,marginBottom:16}} value={this.state.appType} onChange={(e) => this.setState({appType: e.target.value})}>
              <Radio.Button value="project">项目</Radio.Button>
              <Radio.Button value="image">镜像</Radio.Button>
            </Radio.Group>

            {this.state.appType === 'project' ?
              <>
                <Form.Item name={['project', 'id']}
                           label='项目' required rules={[{required: true}]}>
                  <RemoteSelect url='/api/project/options'
                                placeholder='请选择项目'
                                optionFilterProp="label"
                  ></RemoteSelect>
                </Form.Item>
                <Form.Item name='imageTag' label='版本' required rules={[{required: true}]}>
                  <AutoComplete options={this.state.versions} placeholder='请选择版本'/>
                </Form.Item>
              </> :

              <>
                <Form.Item name='imageUrl' label='镜像' required rules={[{required: true}]}>
                  <Input/>
                </Form.Item>


                <Form.Item name='imageTag' label='版本' required rules={[{required: true}]}>
                  <Input/>
                </Form.Item>
              </>}


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
