import React from "react";
import {get, post} from "../../utils/request";
import RemoteSelect from "../../components/RemoteSelect";
import {AutoComplete, Button, Form, Input, message} from "antd";
import {history} from "umi";


let api = '/api/app/';

export default class extends React.Component {


  state = {
    versions: []
  }


  formRef = React.createRef()

  handleSave = value => {
    post(api + 'saveByProject', value).then(rs => {
      message.success(rs.message)
      history.push('/app/view?id='+rs.data)
    })
  }



  loadversions = projectId => {

      get('api/project/versions', {projectId}).then(rs => {
        this.setState({versions: rs})
      })
  };

  render() {
    let defaultUrl = this.props.url;

    return <>
      <Form
        ref={this.formRef}
        initialValues={{
          imageUrl: defaultUrl
        }}
        onValuesChange={changedValues => {
          if (changedValues.project != null) {
            this.loadversions(changedValues.project.id);
          }
        }}
        onFinish={this.handleSave}
      >

        <Form.Item name={['project','id']} label='项目' required rules={[{required: true}]}>
          <RemoteSelect url='/api/project/options' disabled={defaultUrl}></RemoteSelect>
        </Form.Item>
        <Form.Item name='imageTag' label='版本' required rules={[{required: true}]}>
          <AutoComplete options={this.state.versions} />
        </Form.Item>


        <Form.Item name={['host','id']} label='主机' required rules={[{required: true}]}>
          <RemoteSelect showSearch url="/api/host/options"/>
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
