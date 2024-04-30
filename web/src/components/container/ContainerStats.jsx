import React from "react";
import {Spin, Statistic} from "antd";
import {hutool} from "@moon-cn/hutool";


export default class extends React.Component {

  state = {
    data: null
  }

  componentDidMount() {
    let {hostId, containerId} = this.props;
    hutool.http. get('/api/container/stats', {hostId, containerId}).then(rs => {
      this.setState({data: rs.data})
    })
  }

  render() {
    let data = this.state.data;

    if (!data) {
      return <Spin/>
    }

    let keys = Object.keys(data);
    return  <div>
      {keys.map(key=><Statistic title={key} key={key} value={data[key]} />)}
    </div>
  }


}
