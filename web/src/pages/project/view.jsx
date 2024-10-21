import {Card, Descriptions, Spin} from 'antd';
import React from 'react';
import BuildLogList from "./BuildLogList";
import {HttpUtil, PageUtil} from "@tmgg/tmgg-base";


let api = 'project/';

export default class extends React.Component {


    state = {
        project: null,
        showTrigger: false,
        triggerValueList: [],
        activeTab: 'jnl'
    }

    componentDidMount() {
        this.id = PageUtil.currentLocationQuery().id

        HttpUtil.get(api + 'get', {id: this.id}).then(rs => this.setState({project: rs}))
    }

    render() {
        if (this.state.project == null) {
            return <Spin/>
        }

        const {project} = this.state;
        return (<>

            <Card className='mb-2'>
                <Descriptions title={project.name}>
                    <Descriptions.Item label='id'>{project.id}</Descriptions.Item>
                    <Descriptions.Item label='代码源'>{project.gitUrl}</Descriptions.Item>
                    <Descriptions.Item label='dockerfile'>{project.dockerfile}</Descriptions.Item>
                    <Descriptions.Item label='分支'>{project.branch}</Descriptions.Item>
                    <Descriptions.Item label='创建时间'>{project.createTime}</Descriptions.Item>

                    <Descriptions.Item
                        label='自动推送latest版本'>            {project.autoPushLatest ? '是' : '否'}                    </Descriptions.Item>
                </Descriptions>
            </Card>

            <BuildLogList project={project}/>
        </>)
    }


}



