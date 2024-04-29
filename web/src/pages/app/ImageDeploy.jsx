import React from "react";
import {post} from "../../utils/request";
import RemoteSelect from "../../components/RemoteSelect";
import {Button, Form, Input, message} from "antd";
import {history} from "umi";


let api = '/api/app/';

export default class extends React.Component {


  formRef = React.createRef()

  handleSave = value => {
    post(api + 'save', value).then(rs => {
      message.success(rs.message)
      history.push('/app/view?id=' + rs.data)
    })
  }


  render() {
    let defaultUrl = this.props.url;

    return <>
      <Form
        ref={this.formRef}
        initialValues={{
          imageUrl: defaultUrl
        }}
        onFinish={this.handleSave}
      >



        <Form.Item name='imageUrl' label='镜像' required rules={[{required: true}]}>
          <Input/>
        </Form.Item>


        <Form.Item name='imageTag' label='版本' required rules={[{required: true}]}>
          <Input/>
        </Form.Item>
        <Form.Item name={['host', 'id']} label='主机' required rules={[{required: true}]}>
          <RemoteSelect url="/api/host/options"/>
        </Form.Item>

        <Form.Item name='name' label='名称' required rules={[{required: true}]}>
          <Input/>
        </Form.Item>
        <Form.Item>
          <Button htmlType='submit' type='primary'>确定</Button>
        </Form.Item>
      </Form>

    </>

  }


}
