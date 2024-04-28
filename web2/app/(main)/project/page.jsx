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
import Popconfirm from "../../../commons/Popconfirm";
import {commons} from "../../../commons/commons";
import {Field, Form, Formik} from "formik";
import {InputSwitch} from "primereact/inputswitch";
import ProTable from "../../../commons/ProTable";
import {Inplace, InplaceContent, InplaceDisplay} from "primereact/inplace";
import {Divider} from "primereact/divider";
import FormItem from "../../../commons/FormItem";

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
        hutool.http.post('/api/project/save', data).then(rs => {
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
        hutool.http.post("api/project//delete", {id: record.id}).then(rs => {
            tableRef.current.loadData()
            commons.message.success(rs.message)
        })
    };
    return (<Card>
        <ProTable ref={tableRef}
                  url='api/project/list'
                  toolBarRender={() => {
                      return <Button label="新增" icon="pi pi-plus" className=" mr-2"
                                     onClick={openNew}/>;
                  }}>
            <Column field="name" header="项目名称" sortable filter
                    body={(data) => <Link href={'project/view?id=' + data.id}>{data.name}</Link>}/>
            <Column field="gitUrl" header="git地址" sortable filter></Column>
            <Column field="branch" header="分支"></Column>

            <Column field="dockerfile" header="dockerfile"></Column>
            <Column field="defaultVersion" header="默认版本"></Column>
            <Column field="modifyTime" header="更新时间"></Column>

            <Column style={{width: 150}} body={rowData => <div>
                <a className="mr-2 cursor-pointer" onClick={() => editRecord(rowData)}> 编辑 </a>
                <Popconfirm title='确定删除？' onConfirm={() => deleteRecord(rowData)}>
                    <a className='cursor-pointer inline-block'>删除</a>
                </Popconfirm>
            </div>}/>
        </ProTable>

        <Dialog visible={formVisible}
                header="项目信息"
                modal
                className="p-fluid"
                onHide={hideDialog}>
            <Formik initialValues={formValues}
                    onSubmit={submitData}>
                {form => (<Form>

                    <FormItem label='项目名称'>
                        <Field name="name" as={InputText}/>
                    </FormItem>

                    <FormItem label='git地址'>
                        <Field name="gitUrl" as={InputText}/>
                    </FormItem>


                    <Divider className='mt-6'>高级设置</Divider>
                    <FormItem label='分支'>
                        <Field name='branch' as={InputText} placeholder='master'/>
                    </FormItem>
                    <FormItem label='dockerfile'>
                        <Field name='dockerfile' as={InputText} placeholder='Dockerfile'/>
                    </FormItem>


                    <FormItem label='默认版本'>
                        <Field name='defaultVersion' as={InputText}/>
                        <small>默认每日一个版本，可填 latest等</small>
                    </FormItem>

                    <Button style={{width: 100}} label="确定" type='submit' disabled={form.isSubmitting}/>
                </Form>)}
            </Formik>
        </Dialog>
    </Card>);
};


