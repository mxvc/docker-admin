import {Modal, Space, Tooltip} from 'antd';
import React from 'react';
import {
    CheckCircleFilled,
    ClockCircleOutlined,
    CloseCircleFilled,
    Loading3QuartersOutlined,
    MinusCircleTwoTone
} from "@ant-design/icons";
import {HttpUtil, ProTable} from "@tmgg/tmgg-base";
import {DateUtil, StrUtil} from "@tmgg/tmgg-commons-lang";
import LogView from "../components/LogView";


let api = 'buildLog/';

function getIcon(key, index) {
    const iconDict = {
        PENDING: <ClockCircleOutlined key={index}/>,
        PROCESSING: <Loading3QuartersOutlined key={index} spin/>,
        SUCCESS: <CheckCircleFilled key={index} style={{color: 'green'}}/>,
        ERROR: <CloseCircleFilled key={index} style={{color: 'red'}}/>,
        CANCEL: <MinusCircleTwoTone/>
    }
    return iconDict[key]
}


export default class extends React.Component {

    constructor(props) {
        super(props);
        this.listURL = api + "list"
        if (props.project) {
            this.listURL += "?projectId=" + props.project.id
            this.projectId = props.project.id
        }
    }


    listURL = null
    state = {
        curRow: {},

        showTrigger: false,

        hostOptions: []
    }
    actionRef = React.createRef();

    reload = () => {
        this.actionRef.current.reload()
    }

    columns = [
        {
            title: '项目',
            dataIndex: 'projectName',
        },
        {
            title: '开始时间',
            dataIndex: 'createTime',
            render(_, row) {
                return <Tooltip title={row.createTime}> {DateUtil.friendlyTime(row.createTime)}</Tooltip>
            }
        },
        {
            title: '分支/标签',
            dataIndex: 'value',
        },
        {
            title: '目录',
            dataIndex: 'context',
        },
        {
            title: 'Dockerfile',
            dataIndex: 'dockerfile',
        },
        {
            title: '版本',
            dataIndex: 'version',
        },
        {
            title: '代码日志',
            dataIndex: 'codeMessage',
            render(v) {
                return StrUtil.ellipsis(v, 20)
            }
        },
        {
            title: '构建主机',
            dataIndex: 'buildHostName',
        },
        {
            title: '状态',
            dataIndex: 'success',
            render(_, row) {
                let key = 'PROCESSING';

                if (row.success == true) {
                    key = "SUCCESS";
                } else if (row.success == false) {
                    key = "ERROR"
                }
                return getIcon(key, 1);
            }
        },


        {
            title: '耗时',
            dataIndex: 'timeSpend',
            render(t, row) {
                return DateUtil.friendlyTotalTime(t)
            }
        },
        {
            title: '-',
            dataIndex: 'option',
            valueType: 'option',
            fixed: 'right',
            render: (_, row) => {
                return <Space>
                    <a onClick={() => {
                        let logUrl = row.logUrl;

                        Modal.info({
                            title: '构建日志' ,
                            width: 1024,
                            closable: true,
                            icon: null,
                            okText:'关闭',
                            content: <LogView url={logUrl} />
                        })
                    }}>日志</a>
                </Space>
            }
        },
    ]











    render() {
        return (<>

            <ProTable
                request={(params) => HttpUtil.pageData('/home/buildingPage', params)}
                columns={this.columns}
            />

        </>)
    }


}



