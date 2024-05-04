import {Button, Divider, Skeleton, Table} from 'antd';
import React from 'react';
import {history} from "umi";
import hutool from "@moon-cn/hutool";

let api = '/api/host/';


export default class extends React.Component {

  state = {
    list: [],
    loading: true,
  }

  componentDidMount() {
    this.loadData();
  }

  loadData = () => {
    this.setState({loading: true})
    hutool.http.get(api + "containers", {id: this.props.id}).then(rs => {
      this.setState({list:rs.data})
    }).finally(() => {
      this.setState({loading: false})
    })
  }

  remove = (id) => {
    let params = this.props;
    hutool.http.get("/api/container/remove", {hostId: params.id, containerId: id}).then(this.loadData)
  }
  stop = (id) => {
    let params = this.props;
    hutool.http.get("/api/container/stop", {hostId: params.id, containerId: id}).then(this.loadData)
  }
  start = (id) => {
    let params = this.props;
    hutool.http.get("/api/container/start", {hostId: params.id, containerId: id}).then(this.loadData)
  }

  columns = [
    {
      title: '容器',
      dataIndex: 'Names',
      width: 200,
      render: (p, row) => {
        let params = this.props;
        if (!params) {
          return <span></span>
        }

        let hostId = params.id

        const name = row.Names[0].substr(1)
        const id = row.Id.substr(0, 12)
        return <div>
          <a onClick={() => history.push('containerView?containerId=' + id + '&hostId=' + hostId)}>{name}</a>
          <br/> {id}
        </div>
      }
    },
    {
      title: '镜像',
      dataIndex: 'Command',
      width: 360,
      ellipsis: true,
      render: (p, row) => {
        return <div>{row.Image} <br/>{row.Command}</div>
      }
    },
    {
      title: '端口(主机:容器)', dataIndex: 'Ports',
      width: 180,
      render: ps => {
        return ps.map(p => <div key={p.PublicPort}>{p.PublicPort}:{p.PrivatePort}/{p.Type}</div>)
      }
    },
    {title: '状态', dataIndex: 'Status', width: 180},
    {
      title: '-', dataIndex: 'action', render: (_, row) => {
        let id = row.Id;
        let state = row.State;
        let running = state === 'running';
        return <div>
          <Button onClick={() => this.start(id)} disabled={running} size="small">启动</Button>
          <Divider type={"vertical"}/>
          <Button onClick={() => this.stop(id)} disabled={!running} size="small">停止</Button>
          <Divider type={"vertical"}/>
          <Button onClick={() => this.remove(id)} disabled={running} danger size="small">删除</Button></div>
      }
    },

  ];

  render() {
    const {list, loading} = this.state
    return (<div>

      {loading ?
        <Skeleton active/> :
        <Table dataSource={list}
               columns={this.columns}
               rowKey="Id"
               pagination={false}
        />}


    </div>)
  }


}



