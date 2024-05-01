import {Card, Col, Row, Statistic} from "antd";
import React from "react";
import {hutool} from "@moon-cn/hutool";

export default class extends React.Component {




  render() {
    return <Card style={{margin: 16}}>


      <pre>
        项目（源码构建）——> 应用（部署）
      </pre>

      <pre>
        镜像---->应用（部署）
      </pre>



    </Card>
  }
}
