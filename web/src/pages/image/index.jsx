import {Button, Card, Form, Input, Modal, Radio, Select} from 'antd';
import React from 'react';


import {FieldRemoteSelect, HttpUtil, PageUtil, ProTable} from "@tmgg/tmgg-base";


export default class extends React.Component {

    state = {

        registryOptions: [],

        searchParams: {},

        tagTableModalOpen: false,
        tabTableUrl: null,

        pullModalOpen: false,
        curRecord: {},
        pulling: false
    }

    actionRef = React.createRef();

    pullFormRef = React.createRef()

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
            render: (v, record) => {
                return <a onClick={() => this.showTagList(record.url)}> {v}</a>
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
            title: '创建时间',
            dataIndex: 'createTime',
        },
        {
            title: '最近更新',
            dataIndex: 'updateTime',
        },
        {
            title: '-',
            dataIndex: 'option',
            render: (_, record) => {
                return <Button size='small'
                               onClick={() => this.setState({pullModalOpen: true, curRecord: record})}>拉取</Button>
            }
        },
    ];

    componentDidMount() {
        HttpUtil.get('registry/options').then(rs => {
            this.setState({registryOptions: rs})
        })
    }


    showTagList = url => {
        this.setState({tagTableModalOpen: true, tabTableUrl: url})

    };

    handlePullImage = values => {
        this.setState({pulling: true})
        HttpUtil.postForm('/host/syncImageToHost', values).then(rs => {

        }).finally(() => {
            this.setState({pulling: false})
        })
    };

    render() {
        return <>


            <ProTable
                actionRef={this.actionRef}
                request={(params, sort) => {
                    return HttpUtil.pageData('image/page', {...params, ...this.state.searchParams}, sort);
                }}
                columns={this.columns}
                showSearch={false}
                searchFormItemsRender={() =>
                    <>
                        <Form.Item label='注册中心' name='registryId'>
                            <Select options={this.state.registryOptions} style={{width:400}} />
                        </Form.Item>
                        <Form.Item  name='keyword'>
                            <Input placeholder='名称、路径等'/>
                        </Form.Item>
                    </>}
            />


            <Modal title='版本列表' open={this.state.tagTableModalOpen}
                   width={700}
                   destroyOnClose
                   onCancel={() => this.setState({tagTableModalOpen: false})}
                   footer={null}
            >
                <ProTable
                    actionRef={this.actionRef}

                    request={(params, sort) => {
                        params.url = this.state.tabTableUrl;
                        return HttpUtil.pageData('/image/tagPage', params, sort);
                    }}

                    bordered={false}

                    columns={[
                        {
                            title: '版本',
                            dataIndex: 'tagName'
                        },
                        {
                            title: '时间',
                            dataIndex: 'time'
                        },
                    ]}
                    rowSelection={false}
                    search={false}
                    options={{search: true}}
                />
            </Modal>

            <Modal title='拉取镜像' open={this.state.pullModalOpen}
                   width={700}
                   destroyOnClose
                   onCancel={() => this.setState({pullModalOpen: false})}
                   onOk={() => this.pullFormRef.current.submit()}
                   okButtonProps={{
                       loading: this.state.pulling
                   }}
            >

                <Form ref={this.pullFormRef} labelCol={{flex: '100px'}} initialValues={this.state.curRecord}
                      onFinish={this.handlePullImage}>
                    <Form.Item label='镜像' name='url' rules={[{required: true}]}>
                        <Input disabled/>
                    </Form.Item>
                    <Form.Item label='版本' name='tag' rules={[{required: true}]}>
                        <FieldRemoteSelect url={'/image/tagOptions?url=' + this.state.curRecord.url}/>
                    </Form.Item>
                    <Form.Item label='主机' name='hostId' rules={[{required: true}]}>
                        <FieldRemoteSelect url={'/host/options'}/>
                    </Form.Item>
                    <Form.Item label='重命名' name='newName'>
                        <Input placeholder='如openjdk,mysql等，不修改则留空'/>
                    </Form.Item>
                </Form>

            </Modal>

        </>
    }


}



