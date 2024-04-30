import {Card, Col, Row, Statistic} from "antd";
import React from "react";
import {hutool} from "@moon-cn/hutool";

export default class extends React.Component {

  state = {
    info:{}
  }

  componentDidMount() {
    hutool.http.get('api/homeInfo').then(rs=>{
      this.setState({info:rs.data})
    })
  }


  render() {
    const {appCount, projectCount, hostCount} = this.state.info
    return <Card style={{margin: 16}}>
      <Row gutter={[24,32]} >
        <Col span={12}>
          <Statistic title="项目" value={projectCount} />
        </Col>

        <Col span={12}>
          <Statistic title="应用" value={appCount}  />
        </Col>
        <Col span={12}>
          <Statistic title="主机" value={hostCount}  />
        </Col>
      </Row>
    </Card>
  }
}
