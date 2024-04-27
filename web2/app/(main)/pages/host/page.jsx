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
import {Field, Form, Formik} from "formik";
import {InputSwitch} from "primereact/inputswitch";
import {Paginator} from "primereact/paginator";

export default () => {


    const [formVisible, setFormVisible] = useState(false);
    const [formValues, setFormValues] = useState({});


    const [page, setPage] = useState({
        content: [], pageable: {
            offset: 0, pageSize: 10, pageNumber: 0, totalElements: 0
        }
    });

    const dt = useRef(null);

    const loadData = (params) => {
        hutool.http.get("/api/host/list", params).then(rs => {
            const {content, pageable} = rs
            pageable.totalElements = rs.totalElements
            setPage({content, pageable})
        })
    };

    useEffect(loadData, []);


    const openNew = () => {
        setFormValues({})
        setFormVisible(true);
    };

    const hideDialog = () => {
        setFormVisible(false);
    };
    const submitData = (data, form) => {
        hutool.http.post('/api/host/save', data).then(rs => {
            commons.message.success(rs.message)
            loadData()
            setFormVisible(false);
        }).catch(e => {
            commons.message.error(e);
        }).finally(() => {
            form.setSubmitting(false)
        })
    };

    const editRecord = (record) => {
        setFormValues(record)
        setFormVisible(true);
    };
    const deleteRecord = (record) => {
        hutool.http.post("api/host//delete", {id: record.id}).then(rs => {
            loadData()
            commons.message.success(rs.message)
        })
    };
    return (<Card>
        <Toolbar className="mb-4"
                 start={<InputText type="search"
                                   onInput={(e) => loadData({searchText: e.currentTarget.value})}
                                   placeholder="搜索..."/>}
                 end={<><Button label="新增" icon="pi pi-plus" severity="success" className=" mr-2"
                                onClick={openNew}/>
                     <Button icon="pi pi-refresh" rounded text onClick={loadData}/>
                 </>}
        ></Toolbar>


        <DataTable
            ref={dt}
            value={page.content}
            dataKey="id"
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

        <Paginator rows={page.pageable.pageSize}
                   first={page.pageable.offset}
                   totalRecords={page.pageable.totalElements} onPageChange={e => {
            loadData({pageNumber: e.page + 1})
        }}></Paginator>


        <Dialog visible={formVisible} header="主机信息" modal
                className="p-fluid"
                onHide={hideDialog}>
            <Formik initialValues={formValues} onSubmit={submitData}>
                {form => (<Form>
                    <div className="field ">
                        <label>主机名称</label>
                        <Field name="name" as={InputText}/>
                    </div>
                    <div className="field">
                        <label>Docker接口</label>
                        <Field name='dockerHost' as={InputText}/>
                    </div>
                    <div className="field">
                        <label>请求头Host</label>
                        <Field name='dockerHostHeader' as={InputText}/>
                    </div>
                    <div className="field">
                        <label>备注</label>
                        <Field name='remark' as={InputText}/>
                    </div>
                    <div className="field">
                        <label>是否构建主机</label>
                        <InputSwitch className='block' name='isRunner' checked={form.values.isRunner}
                                     onChange={form.handleChange}></InputSwitch>
                    </div>
                    <Button style={{width: 100}} label="确定" type='submit' disabled={form.isSubmitting}/>
                </Form>)}

            </Formik>
        </Dialog>


    </Card>);
};


