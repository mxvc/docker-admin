import {Badge, Card, Descriptions, Skeleton, Tabs} from 'antd';
import React from 'react';
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
      .then(rs => {
        this.setState({host: rs, loading: false})
      })

    hutool.http.get(api + "runtime/get", {id})
      .then(rs => {
        this.setState({info: rs.data, runtimeLoading: false})
      })


  }


  render() {
    const {host, info, loading} = this.state;
    if (loading) {
      return <Spin/>
    }


    return (<>
      <Card >
        <Descriptions title={host.name } >
          <Descriptions.Item label="操作系统">{info.operatingSystem}</Descriptions.Item>
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



