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
import ProTable from "../../../../commons/ProTable";

export default () => {
    const [formVisible, setFormVisible] = useState(false);
    const [formValues, setFormValues] = useState({});
    const tableRef = useRef();

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
            tableRef.current.loadData()
            setFormVisible(false);
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
            tableRef.current.loadData()
            commons.message.success(rs.message)
        })
    };
    return (<Card>
        <ProTable ref={tableRef}
                  url='api/host/list'
                  toolBarRender={() => {
                      return <Button label="新增" icon="pi pi-plus" className=" mr-2"
                                     onClick={openNew}/>;
                  }}>
            <Column field="name" header="主机名称" sortable filter
                    body={(data) => <Link href={'host/view?id=' + data.id}>{data.name}</Link>}/>
            <Column field="dockerHost" header="Docker接口" sortable filter></Column>
            <Column field="dockerHostHeader" header="请求头Host"></Column>
            <Column field="remark" header="备注"></Column>
            <Column field="isRunner" header="是否构建主机" body={d => d.isRunner ? "是" : "否"}></Column>

            <Column body={rowData => <div>
                <Button plain label='编辑'  severity="success" className="mr-2"
                        onClick={() => editRecord(rowData)}/>

                <Popconfirm title='确定删除？' onConfirm={() => deleteRecord(rowData)}>
                    <Button  label='删除'  severity="warning"/>
                </Popconfirm>
            </div>}/>
        </ProTable>

        <Dialog visible={formVisible}
                header="主机信息"
                modal
                className="p-fluid"
                onHide={hideDialog}>
            <Formik initialValues={formValues}
                    onSubmit={submitData}>
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
                        <small>如：unix:///var/run/docker.sock,tcp://192.168.1.2:2375</small>
                    </div>
                    <div className="field">
                        <label>备注</label>
                        <Field name='remark' as={InputText}/>
                    </div>
                    <div className="field">
                        <label>是否构建主机</label>
                        <InputSwitch className='block'
                                     name='isRunner'
                                     checked={form.values.isRunner}
                                     onChange={form.handleChange}></InputSwitch>
                    </div>
                    <Button style={{width: 100}} label="确定" type='submit' disabled={form.isSubmitting}/>
                </Form>)}
            </Formik>
        </Dialog>
    </Card>);
};


