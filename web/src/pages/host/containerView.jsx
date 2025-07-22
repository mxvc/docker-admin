import React from 'react';
import {Card, Tabs} from "antd";
import ContainerLog from "../../components/container/ContainerLog";
import ContainerFile from "../../components/container/ContainerFile";
import {PageUtil} from "@tmgg/tmgg-base";
import ContainerTerminal from "../../components/container/ContainerTerminal";


export default class extends React.Component {

  render() {
    const {containerId, hostId} = PageUtil.currentLocationQuery();

    return <Card style={{minHeight:'calc(100vh - 100px)'}}>
      <Tabs  destroyOnHidden >
        <Tabs.TabPane tab="日志" key="container-log">
          <ContainerLog hostId={hostId} containerId={containerId}/>
        </Tabs.TabPane>



        <Tabs.TabPane tab="文件" key="container-file">
          <ContainerFile hostId={hostId} containerId={containerId}/>
        </Tabs.TabPane>

        <Tabs.TabPane tab="终端 (BETA)" key="container-term">
          <ContainerTerminal hostId={hostId} containerId={containerId}/>
        </Tabs.TabPane>

      </Tabs>
    </Card>
  }


}



