import {Input, InputNumber, Select} from "antd";
import React from "react";
import {DeleteOutlined, PlusCircleFilled} from '@ant-design/icons';

export default class extends React.Component {


  columns = []


  add = () => {
    let {value} = this.props;
    value.push({})
    this.props.onChange(value)
  }

  remove = (i) => {
    let {value} = this.props;
    value.splice(i, 1)
    this.props.onChange(value)
  }

  edit = (k, v, i) => {
    let {value} = this.props;
    value[i][k] = v;
    this.props.onChange(value)
  }


  render() {
    const {columns,value} = this.props;
    return <div>
      <table>
        <thead>
        <tr>
          {columns.map(c => <th key={c.dataIndex}>{c.title}</th>)}
          <th></th>
        </tr>
        </thead>
        <tbody>
        {this.renderTbody(value, columns)}

        </tbody>
      </table>

      <div style={{marginTop: 16, marginBottom: 16}}>
        <PlusCircleFilled style={{color: '#1890ff'}}/><a onClick={this.add}>添加</a>
      </div>
    </div>

  }

  renderTbody = (value = [], columns) => {
    if(value.length === 0){
      return <tr>
      <td height={50} colSpan={columns.length + 1}>暂无数据</td>
    </tr>}

    return <>
      {value.map((p, i) => <tr key={i}>
        {columns.map(c => <td key={c.dataIndex}>
          {c.dataType == 'Input' && <Input
              value={p[c.dataIndex]}
              onChange={e => {
                this.edit(c.dataIndex, e.target.value, i)
              }}/>}

          {c.dataType == 'InputNumber' && <InputNumber
              value={p[c.dataIndex]}
              onChange={v => {
                this.edit(c.dataIndex, v, i)
              }}/>}


          {c.dataType == 'Select' && <Select value={p[c.dataIndex]}
                                             onChange={v => this.edit(c.dataIndex, v, i)}
                                             style={{minWidth: 100}}
          >

            {Object.keys(c.valueEnum).map(k => <Select.Option key={k} value={k}>{c.valueEnum[k]}</Select.Option>)}
          </Select>}
        </td>)}
        <td>
          <DeleteOutlined onClick={() => this.remove(i)}></DeleteOutlined>
        </td>
      </tr>)}


    </>;
  };
}
