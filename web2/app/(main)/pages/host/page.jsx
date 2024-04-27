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


export default () => {
    const [formVisible, setFormVisible] = useState(false);
    const [formValues, setFormValues] = useState({});

    const [loading, setLoading] = useState(false)
    const [tableData, setTableData] = useState([])
    const [totalRecords, setTotalRecords] = useState(0)
    const [search, setSearch] = useState({})
    const [filterDisplay, setFilterDisplay] = useState("row"); // row, menu

    const [lazyState, setLazyState] = useState({
        first: 0,
        rows: 10,
        page: 0,
        sortField: null,
        sortOrder: null,
        filters: null

    });


    const loadData = () => {
        setLoading(true)
        const params = {...search}
        params.pageSize = lazyState.rows
        if (lazyState.page != null) { // 排序时会为空，相当于从第一页
            params.pageNumber = lazyState.page + 1; // springmvc设置了从1开始
        }

        console.log(lazyState)
        if (lazyState.sortField) {
            params.orderBy = lazyState.sortField + "," + (lazyState.sortOrder == 1 ? "asc" : "desc")
        }


        params.filters = lazyState.filters


        hutool.http.get("/api/host/list", params).then(rs => {
            const {content} = rs
            setTableData(content)
            setLazyState(lazyState)
            setTotalRecords(rs.totalElements)
        }).finally(() => {
            setLoading(false)
        })
    };

    useEffect(loadData, [lazyState, search]);


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
                                   onInput={(e) => {
                                       setSearch({searchText: e.currentTarget.value});
                                       lazyState.page = undefined
                                       setLazyState(lazyState)
                                   }}
                                   placeholder="搜索..."/>}
                 end={<><Button label="新增" icon="pi pi-plus" severity="success" className=" mr-2"
                                onClick={openNew}/>
                     <Button icon="pi pi-refresh" text onClick={loadData}/>
                     <Button icon="pi pi-filter" text onClick={() => {
                         setFilterDisplay(filterDisplay == 'row' ? "menu" : 'row')
                     }}/>
                 </>}
        ></Toolbar>


        <DataTable
            value={tableData}
            loading={loading}
            dataKey="id"

            lazy

            paginator
            rows={lazyState.rows}
            first={lazyState.first}
            totalRecords={totalRecords}
            onPage={e => {
                setLazyState(e)
            }}
            rowsPerPageOptions={[2, 10, 50, 100, 200, 500, 1000, 2000]}

            sortField={lazyState.sortField}
            sortOrder={lazyState.sortOrder}
            onSort={e => {
                setLazyState(e)
            }}

            onFilter={e => {
                setLazyState(e)
            }}
            filters={lazyState.filters}
            filterDisplay={filterDisplay}

        >
            <Column field="name" header="主机名称" sortable filter
                    body={(data) => <Link href={'host/view?id=' + data.id}>{data.name}</Link>}/>
            <Column field="dockerHost" header="Docker接口" sortable filter></Column>
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


