import React from 'react';
import ContainerLog from "../../components/container/ContainerLog";
import {Tabs} from "antd";
import ContainerFile from "./ContainerFile";
import ContainerCmd from "./ContainerCmd";

export default class extends React.Component {


  render() {
    const {containerId, hostId} = this.props;

    return <Tabs  destroyInactiveTabPane >
      <Tabs.TabPane tab="日志" key="container-log">
        <ContainerLog hostId={hostId} containerId={containerId}/>
      </Tabs.TabPane>


      <Tabs.TabPane tab="文件" key="container-file">
        <ContainerFile hostId={hostId} containerId={containerId}/>
      </Tabs.TabPane>

      <Tabs.TabPane tab="终端" key="container-cmd">
        <ContainerCmd hostId={hostId} containerId={containerId}/>
      </Tabs.TabPane>

    </Tabs>
  }


}



