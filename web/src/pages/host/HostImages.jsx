import {Button, message, Skeleton, Table, Tag} from 'antd';
import React from 'react';
import {HttpUtil} from "@tmgg/tmgg-base";


let api = 'host/';


export default class extends React.Component {

    state = {
        loading: true,
        list: [],
    }

    componentDidMount() {
        this.loadData();
    }

    loadData() {
        this.setState({loading: true})
        HttpUtil.get(api + "images", {id: this.props.id}).then(list => {
            this.setState({list})
        }).finally(() => {
            this.setState({loading: false})
        })
    }

    delete = imageId => {
        const hide = message.loading("删除镜像" + imageId + "中...", 0)
        HttpUtil.postForm(api + "/deleteImage", {id: this.props.id, imageId}).then(rs => {
            this.loadData()
        }).finally(hide)
    }

    columns = [
        {
            title: '序号',
            dataIndex: 'index',
            render(tags, row, index) {
                return index + 1;
            }
        },
        {
            title: '标识',
            dataIndex: 'Id',
            render(v) {
                return v.substr(7, 12);
            }
        },
        {
            title: '版本',
            dataIndex: 'RepoTags',
            render(tags, row) {
                return tags && tags.map(tag => <div>{tag}</div>)
            }
        },

        {
            title: '创建于',
            dataIndex: 'Created',
            render: function (v, row) {
                let date = new Date(v * 1000);
                return date.toLocaleDateString();
            }
        },
        {
            title: '大小',
            dataIndex: 'Size',
            render(v) {
                return (v / 1024 / 1024).toFixed(1) + " MB";
            }
        },

        {
            dataIndex: 'action',
            fixed: 'right',
            width: 200,
            render: (_, row) => {
                return <Button size="small" onClick={() => this.delete(row.Id)}>删除 </Button>;
            }
        },
    ];

    render() {
        if (this.state.loading) {
            return <Skeleton active/>
        }
        return <Table
            dataSource={this.state.list}
            rowKey="Id"
            size={"small"}
            columns={this.columns}
            scroll={{x: 'max-content'}}
            pagination={false}
        />
    }


}



