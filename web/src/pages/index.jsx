import React from "react";
import {Card, Divider, Table} from "antd";
import {HttpUtil, Page} from "@tmgg/tmgg-base";
import {StrUtil} from "@tmgg/tmgg-commons-lang";

export default class extends React.Component {

    state = {
        eventList: []
    }

    componentDidMount() {
        HttpUtil.get("home/eventList").then(rs => {
            this.setState({eventList: rs})
        })
    }

    render() {
        return <Page>
            <Card>
                欢迎使用容器管理系统
                <Divider/>

                <Table size='small'
                       title={() => <div>活动日志</div>}
                       style={{width: 500}}
                       dataSource={this.state.eventList} pagination={false} columns={[{
                    dataIndex: 'account', title: '账号', render(v) {
                        return StrUtil.subAfter(v, ' ')
                    }
                },
                    {dataIndex: 'module', title: '模块'},
                    {dataIndex: 'name', title: '操作'},
                    {dataIndex: 'param', title: '参数'},

                    {dataIndex: 'createTime', title: '时间'}

                ]} bordered></Table>

            </Card>
        </Page>
    }
}
