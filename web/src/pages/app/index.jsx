import {AutoComplete, Button, Form, Input, Modal, Splitter} from 'antd';
import React from 'react';
import ContainerStatus from "../../components/ContainerStatus";
import {notPermitted} from "../../utils/SysConfig";
import {FieldOrgTreeSelect, FieldSelect, HttpUtil, OrgTree, PageUtil, ProTable} from "@tmgg/tmgg-base";

let api = 'app/';


export default class extends React.Component {


    columns = [
        {
            title: '应用名称',
            dataIndex: 'name',
            sorter: true,
            render: (name, row) => {
                return <a onClick={() => PageUtil.open('/app/view?id=' + row.id,'应用-'+name)}>{name}</a>
            }
        },


        {
            title: '镜像',
            dataIndex: 'imageUrl',
            sorter: true,
            render: (_, record) => {
                if (record.imageUrl) {
                    return record.imageUrl
                }
                return <a onClick={() => PageUtil.open('project/view?id=' + record.project.id,'镜像-' +record.project.name)}>{record.project.name}</a>
            }
        },
        {
            title: '版本',
            dataIndex: 'imageTag',
        },
        {
            title: '运行主机',
            dataIndex: ['host','name'],
            sorter:true,
        },
        {
            title: '状态',
            dataIndex: 'containerStatus',
            hideInForm: true,
            render: (_, row) => {
                return <ContainerStatus hostId={row.host?.id} appName={row.name}></ContainerStatus>
            }
        },
        {
            title: '组织机构',
            dataIndex: ['sysOrg', 'name'],

        },

        {
            title: '最近更新',
            dataIndex: 'modifyTime',
        },


    ];
    state = {

        deployVisible: false,
        deployImageVisible: false,


        imageList: [],
        imageTagList: [],

        selectedOrgId:null

    }


    reload = () => {
        this.tableRef.current.reload()
    }


    handleSave = value => {
        HttpUtil.post(api + 'save', value).then(rs => {
            this.reload()
            this.setState({deployVisible: false})
        })
    }

    formRef = React.createRef()
    tableRef = React.createRef()
    handleAdd = () => {
        this.setState({deployVisible: true})
        this.loadImageList();
    }

    loadImageList = (text) => {
        HttpUtil.get('image/options', {searchText: text}).then(rs => {
            this.setState({imageList: rs})
        })
    };
    loadImageTagList = (text) => {
        const url = this.formRef.current.getFieldValue('imageUrl')
        if (url) {
            HttpUtil.get('image/tagOptions', {url, searchText: text}).then(rs => {
                this.setState({imageTagList: rs})
            })
        } else {
            this.setState({imageTagList: []})
        }

    };

    render() {
        return (
            <> <Splitter>
                <Splitter.Panel size={250}>
                    <OrgTree onChange={(v) => {
                        this.setState({selectedOrgId:v},()=>{
                            this.tableRef.current.reload()
                        })

                    }}/>

                </Splitter.Panel>
                <Splitter.Panel style={{paddingLeft:16}}>
                <ProTable
                    actionRef={this.tableRef}
                    toolBarRender={(action, {selectedRows}) => [
                        <Button disabled={notPermitted('app:save')} type="primary"
                                onClick={this.handleAdd}>
                            新增
                        </Button>,
                    ]}
                    request={(params) => {
                        params.orgId = this.state.selectedOrgId
                        return HttpUtil.pageData(api + 'list', params);
                    }}
                    columns={this.columns}




                />
                </Splitter.Panel>
            </Splitter>
                <Modal title='新增应用' open={this.state.deployVisible} destroyOnHidden={true}
                       onOk={()=>this.formRef.current.submit()}
                       onCancel={() => this.setState({deployVisible: false})}
                    width={800}
                >
                    <Form
                        layout='horizontal'
                        labelCol={{flex: '100px'}}
                        ref={this.formRef}
                        onValuesChange={changedValues => {
                            if (changedValues.imageUrl != null) {
                                this.loadImageTagList();
                            }
                        }}
                        onFinish={this.handleSave}
                    >
                        <Form.Item name='name' label='应用名称' required rules={[{required: true}]}>
                            <Input/>
                        </Form.Item>

                        <Form.Item name='imageUrl' label='镜像' required rules={[{required: true}]}>
                            <AutoComplete options={this.state.imageList} onSearch={this.loadImageList}></AutoComplete>
                        </Form.Item>


                        <Form.Item name='imageTag' label='版本' required rules={[{required: true}]}>
                            <AutoComplete options={this.state.imageTagList}
                                          onSearch={this.loadImageTagList}></AutoComplete>
                        </Form.Item>


                        <Form.Item name={['host', 'id']} label='部署主机' required rules={[{required: true}]}>
                            <FieldSelect showSearch url="host/options"/>
                        </Form.Item>



                        <Form.Item label='所属组织' name={['sysOrg', 'id']} >
                            <FieldOrgTreeSelect/>
                        </Form.Item>

                    </Form>
                </Modal>


            </>

        )
    }

}
