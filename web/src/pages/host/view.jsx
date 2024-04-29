import {Badge, Card, Descriptions, Tabs} from 'antd';
import React from 'react';
import {get, post} from "../../utils/request";
import HostImages from "./HostImages";
import HostContainers from "./HostContainers";
import {Spin} from "antd/lib";
import {hutool} from "@moon-cn/hutool";

let api = '/api/host/';


export default class extends React.Component {

  state = {
    host: {},
    info: {},
    loading: true,

    runtimeLoading: true,
  }


  componentDidMount() {
    let {id} = this.props.location.query;

    hutool.http.get(api + "get", {id})
      .then(result => {
        this.setState({host: result, loading: false})
      })

    hutool.http.get(api + "runtime/get", {id})
      .then(result => {
        this.setState({host: result, runtimeLoading: false})
      })


  }


  render() {
    const {host, info, loading} = this.state;
    if (loading) {
      return <Spin/>
    }


    return (<>
      <Card>
        <Descriptions title={"主机信息 【" + host.name + '】'}>
          <Descriptions.Item label="主机名">{host.name} </Descriptions.Item>
          <Descriptions.Item label="Docker接口">{host.dockerHost} </Descriptions.Item>
          <Descriptions.Item label="请求头Host">{host.dockerHostHeader} </Descriptions.Item>
          <Descriptions.Item label="备注">{host.remark} </Descriptions.Item>
          <Descriptions.Item label="是否构建主机">{host.isRunner ? '是' : '否'} </Descriptions.Item>

        </Descriptions>

        <Descriptions title='运行时'>
          <Descriptions.Item label="操作系统">{info.operatingSystem}</Descriptions.Item>
          <Descriptions.Item label="id">{info.id}</Descriptions.Item>
          <Descriptions.Item label="架构">{info.architecture}</Descriptions.Item>
          <Descriptions.Item label="内存"> {(info.memTotal / 1024 / 1024 / 1024).toFixed(1)} G</Descriptions.Item>


          <Descriptions.Item label="根目录">{info.dockerRootDir}</Descriptions.Item>
          <Descriptions.Item label="版本">{info.serverVersion}</Descriptions.Item>
          <Descriptions.Item label="系统时间">{info.systemTime}</Descriptions.Item>
        </Descriptions>
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

    </>)
  }


}



