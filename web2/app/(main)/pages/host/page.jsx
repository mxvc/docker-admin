'use client';
import {Button} from 'primereact/button';
import {Column} from 'primereact/column';
import {DataTable} from 'primereact/datatable';
import {Dialog} from 'primereact/dialog';
import {InputText} from 'primereact/inputtext';
import {Toolbar} from 'primereact/toolbar';
import React, {useEffect, useRef, useState} from 'react';
import {hutool} from "@moon-cn/hutool";
import {Card} from "primereact/card";
import Link from "next/link";
import Popconfirm from "../../../../commons/Popconfirm";
import {commons} from "../../../../commons/commons";

export default () => {

    const [tableData, setTableData] = useState(null);
    const [formVisible, setFormVisible] = useState(false);
    const [globalFilter, setGlobalFilter] = useState('');
    const dt = useRef(null);

    const loadData = () => {
        hutool.http.get("/api/host/list").then(rs => {
            setTableData(rs.content)
        })
    };

    useEffect(loadData, []);


    const openNew = () => {
        setFormVisible(true);
    };

    const hideDialog = () => {
        setFormVisible(false);
    };
    const submitData = (data) => {
        hutool.http.post('/api/host/save', data).then(rs => {
            commons.message.success(rs.message)
            loadData()
            setFormVisible(false);
        }).catch(e=>{commons.message.error(e); console.log(e)})
    };

    const editRecord = (data) => {
        setFormVisible(true);
    };
    const deleteRecord = (record) => {
        hutool.http.post("api/host//delete", {id: record.id}).then(rs => {
            loadData()
            commons.message.success(rs.message)
        })
    };
    return (
        <Card>
            <Toolbar className="mb-4"
                     start={<InputText type="search"
                                       value={globalFilter}
                                       onInput={(e) => setGlobalFilter(e.currentTarget.value)}
                                       placeholder="搜索..."/>
                     }
                     end={<Button label="新增" icon="pi pi-plus" severity="success" className=" mr-2"
                                  onClick={openNew}/>}
            ></Toolbar>


            <DataTable
                ref={dt}
                value={tableData}
                dataKey="id"
                paginator
                rows={10}
                rowsPerPageOptions={[5, 10, 25]}
            >
                <Column field="name" header="主机名称" filter
                        body={(data) => <Link href={'host/view?id=' + data.id}>{data.name}</Link>}/>
                <Column field="dockerHost" header="Docker接口"
                        headerTooltip={'如：unix:///var/run/docker.sock,tcp://192.168.1.2:2375'}></Column>
                <Column field="dockerHostHeader" header="请求头Host"></Column>
                <Column field="remark" header="备注"></Column>
                <Column field="isRunner" header="是否构建主机" body={d => d.isRunner ? "是" : "否"}></Column>
                <Column body={rowData => <>
                    <Button icon="pi pi-pencil" rounded severity="success" className="mr-2"
                            onClick={() => editRecord(rowData)}/>

                    <Popconfirm title='确定删除？' onConfirm={() => deleteRecord(rowData)}>
                        <Button icon="pi pi-trash" rounded severity="warning"/>
                    </Popconfirm>

                </>}/>
            </DataTable>

            <Dialog visible={formVisible} header="主机信息" modal
                    className="p-fluid"
                    onHide={hideDialog}>
                <form onSubmit={(event) => {
                    event.preventDefault()
                    const fd =new FormData(event.target);
                    const data =Object.fromEntries(fd.entries())

                    submitData(data)
                    return false
                }} >
                    <div className="field">
                        <label>主机名称</label>
                        <InputText name='name' required />
                    </div>
                    <div className="field">
                        <label>Docker接口</label>
                        <InputText name='dockerHost' required/>
                    </div>
                    <div className="field">
                        <label>请求头Host</label>
                        <InputText name='dockerHostHeader'/>
                    </div>
                    <div className="field">
                        <label>备注</label>
                        <InputText name='remark'/>
                    </div>
                    <div className="field">
                        <label>是否构建主机</label>
                        <InputText name='isRunner'/>
                    </div>
                        <Button style={{width: 100}} label="确定" type='submit'/>
                </form>
            </Dialog>


        </Card>
    );
};


