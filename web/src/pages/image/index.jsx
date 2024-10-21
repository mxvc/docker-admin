import {Button, Card, Form} from 'antd';
import React from 'react';


import {ProTable} from "@tmgg/pro-table";
import {FieldRemoteSelect, HttpUtil, PageUtil} from "@tmgg/tmgg-base";



export default class extends React.Component {

  state = {
    configList: [],
    index: null,

    searchParams: {}
  }




  actionRef = React.createRef();

  columns = [
    {
      title: '名称',
      dataIndex: 'name',
    },
    {
      title: '镜像地址',
      dataIndex: 'url',
    },
    {
      title: '版本数',
      dataIndex: 'tagCount',
      render(v){
        return <a onClick={()=>PageUtil.open('/')}> {v}</a>
      }
    },
    {
      title: '类型',
      dataIndex: 'type',
    },


    {
      title: '下载量',
      dataIndex: 'pullCount',
    },

    {
      title: '最近更新',
      dataIndex: 'time',
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
      <Card>


      <Form layout='inline' onValuesChange={(changedValues,values)=>{
        this.setState({searchParams:values})
        this.actionRef.current.reload();
      }}>
        <Form.Item label='注册中心' name='registryId'>
          <FieldRemoteSelect url='registry/options' style={{width:400}} />
        </Form.Item>

      </Form>
      </Card>


      <div style={{margin:12}}></div>

      <ProTable
        actionRef={this.actionRef}
        request={(params, sort) => {
          params.pageSize = 100;

          return HttpUtil.pageData('image/page', {...params, ... this.state.searchParams} , sort);
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



