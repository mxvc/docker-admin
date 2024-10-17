import React from "react";
import {Card, Tabs} from "antd";
import GitCredential from "./gitCredential";
import Registry from "./registry";


export default class extends React.Component{

  render() {


    const items = [
      {
        key: 'registry',
        label: <>注册中心</>,
        children: <Registry/>
      },
      {
        key: 'gitCredential',
        label: <>GIT凭据</>,
        children: <GitCredential/>
      },


    ];
    return <Card> <Tabs items={items} /></Card>;

  }
}
