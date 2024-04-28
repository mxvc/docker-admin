'use client';
import React from 'react';
import {hutool} from "@moon-cn/hutool";
import PageLoading from "../../../../commons/PageLoading";
import {Panel} from "primereact/panel";

let api = '/api/project/';

export default class extends React.Component {


  state = {
    project: null,
    showTrigger: false,
    triggerValueList: [],
    activeTab: 'jnl'
  }

  componentDidMount() {
    this.id = this.props.searchParams.id
    hutool.http.get(api+'get', {id: this.id}).then(rs=>this.setState({project: rs}))
  }
  render() {
    if(this.state.project == null){
      return <PageLoading />
    }

    const {project} = this.state;
    return (<>

        <Panel header={project.name} >
            <div className='formgroup-inline'>
                <div className='field'>
                    <label>id</label>
                    <div>{project.id}</div>
                </div>
                <div >
                    <label>项目名称</label>
                    <div>{project.name}</div>
                </div>
                <div>
                    <label>代码源</label>
                    <div>{project.gitUrl}</div>
                </div>
            </div>


            {/*<Descriptions.Item label='dockerfile'>{project.dockerfile}</Descriptions.Item>*/}
            {/*<Descriptions.Item label='默认版本'>{project.defaultVersion}</Descriptions.Item>*/}
            {/*<Descriptions.Item label='分支'>{project.branch}</Descriptions.Item>*/}
            {/*<Descriptions.Item label='创建时间'>{project.createTime}</Descriptions.Item>*/}
        </Panel>


    </>)
  }


}



