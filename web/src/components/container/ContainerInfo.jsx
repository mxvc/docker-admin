import React from "react";
import {Spin} from "antd";
import {hutool} from "@moon-cn/hutool";


export default class extends React.Component {

  state = {
    data: null
  }

  componentDidMount() {
    let {hostId, containerId} = this.props;
    hutool.http. get('/api/container/get', {hostId, containerId}).then(rs => {
      this.setState({data: rs.data})
    })
  }

  render() {
    let data = this.state.data;

    if (!data) {
      return <Spin/>
    }

    return  <pre className='bg-black text-green-500'>{this.state.data}</pre>
  }


}
