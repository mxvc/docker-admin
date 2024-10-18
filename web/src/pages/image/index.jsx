import {Button, Card} from 'antd';
import React from 'react';


import {ProTable} from "@tmgg/pro-table";
import {HttpUtil} from "@tmgg/tmgg-base";



export default class extends React.Component {

  state = {
    configList: [],
    index: null
  }




  actionRef = React.createRef();

  columns = [
    {
      title: '名称',
      dataIndex: 'name',
    },
    {
      title: '镜像',
      dataIndex: 'url',
    },
    {
      title: '类型',
      dataIndex: 'type',
    },

    {
      title: '版本数量',
      dataIndex: 'tagCount',
    },

    {
      title: '最近更新',
      dataIndex: 'modifyTime',
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, row) => {
        return <Button onClick={() => showModal(<AppDeploy url={row.url} disableSelectImage/>)}>部署应用</Button>
      },
    },
  ];


  render() {
    return <>
      <ProTable
        actionRef={this.actionRef}
        request={(params, sort) => {
          params.pageSize = 100;
          return HttpUtil.pageData('image/page', params, sort);
        }}
        columns={this.columns}
        rowSelection={false}
        bordered={true}
        search={false}
        options={{search: true}}
      />


    </>
  }


}



