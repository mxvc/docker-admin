import {AutoComplete, Badge, Button, Card, Descriptions, Form, Input, message, Modal, Tabs} from 'antd';
import React from 'react';
import HostImages from "./HostImages";
import HostContainers from "./HostContainers";
import {Spin} from "antd/lib";


let api = '/api/host/';


export default class extends React.Component {

  state = {
    host: {},
    info: {},
    loading: true,

    runtimeLoading: true,

    pullOpen: false,
  }


  componentDidMount() {
    this.loadData()
  }

  loadData = () => {
    let {id} = this.props.location.query;

    this.setState({runtimeLoading: true})

    HttpUtil.get(api + "get", {id})
      .then(rs => {
        this.setState({host: rs, loading: false})
      })

    HttpUtil.get(api + "runtime/get", {id})
      .then(rs => {
        this.setState({info: rs.data, runtimeLoading: false})
      })
  }


  cleanImage = () => {
     message.loading('清理中...')
    let {id} = this.props.location.query;
    HttpUtil.get(api + "cleanImage", {id}).then(rs => {
    })
  }

  render() {
    const {host, info, loading} = this.state;
    if (loading) {
      return <Spin/>
    }


    return (<>
      <Card>
        <Descriptions title={host.name}>
          <Descriptions.Item label="操作系统">{info.operatingSystem}</Descriptions.Item>
          <Descriptions.Item label="内存"> {(info.memTotal / 1024 / 1024 / 1024).toFixed(2)} G</Descriptions.Item>

          <Descriptions.Item label="存储目录">{info.dockerRootDir}</Descriptions.Item>
          <Descriptions.Item label="docker版本">{info.serverVersion}</Descriptions.Item>
          <Descriptions.Item label="系统时间">{info.systemTime}</Descriptions.Item>
        </Descriptions>

        <div style={{display: "flex", justifyContent: 'end', gap: 8}}>
          <Button onClick={this.cleanImage} title='清理未使用镜像'>清理镜像</Button>
          <Button onClick={() => this.setState({pullOpen: true})}>同步镜像</Button>
        </div>

      </Card>

      <Card className='mt-2'>
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab={<Badge count={info.containers}> 容器</Badge>} key="1">
            <HostContainers id={host.id}/>
          </Tabs.TabPane>
          <Tabs.TabPane tab={<Badge count={info.images}> 镜像</Badge>} key="2">
            <HostImages id={host.id}/>
          </Tabs.TabPane>
        </Tabs>
      </Card>

      <Modal title='同步镜像到主机' open={this.state.pullOpen} destroyOnClose
             onCancel={() => this.setState({pullOpen: false})} maskClosable={false} footer={null}>
        <Form labelCol={{flex: '100px'}} onFinish={this.sync}>
          <Form.Item label='镜像' name='image'>
            <AutoComplete options={[
              {value: 'nginx:latest'},
              {value: 'node:14-alpine'},
              {value: 'node:16-alpine'},
              {value: 'node:18-alpine'},
              {value: 'openjdk:8-alpine'},
              {value: 'openjdk:17-alpine'},
              {value: 'ubuntu:latest'},
            ]}></AutoComplete>
          </Form.Item>
          <Form.Item label='仓库源' name='src' initialValue='registry.cn-hangzhou.aliyuncs.com/commons-hub'>
            <Input></Input>
          </Form.Item>

          <Button htmlType='submit' type='primary'>开始</Button>
        </Form>


      </Modal>
    </>)
  }


  sync = values => {
    let {id} = this.props.location.query;
    values.hostId = id;
    const hide = message.loading("同步中,请勿退出...", 0)
    HttpUtil.postForm('api/host/syncImageToHost', values).then((rs) => {
      hide();
      message.success(rs.message)
      this.loadData()
    })
  };
}



