import React from "react";
import {get} from "../../utils/request";
import {Spin} from "antd";


export default class extends React.Component {

  state = {
    data: null
  }

  componentDidMount() {
    let {hostId, containerId} = this.props;
    get('/api/container/get', {hostId, containerId}).then(rs => {
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
