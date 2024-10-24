import React from 'react';
import {Card, Tabs} from "antd";
import ContainerLog from "../../components/container/ContainerLog";
import ContainerFile from "../../components/container/ContainerFile";
import {PageUtil} from "@tmgg/tmgg-base";


export default class extends React.Component {

  render() {
    const {containerId, hostId} = PageUtil.currentLocationQuery();

    return <Card style={{minHeight:'calc(100vh - 100px)'}}>
      <Tabs  destroyInactiveTabPane >
        <Tabs.TabPane tab="日志" key="container-log">
          <ContainerLog hostId={hostId} containerId={containerId}/>
        </Tabs.TabPane>


        <Tabs.TabPane tab="文件" key="container-file">
          <ContainerFile hostId={hostId} containerId={containerId}/>
        </Tabs.TabPane>



      </Tabs>
    </Card>
  }


}



