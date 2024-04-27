'use client';
import {Button} from 'primereact/button';
import {Column} from 'primereact/column';
import {DataTable} from 'primereact/datatable';
import {Dialog} from 'primereact/dialog';
import {InputNumber} from 'primereact/inputnumber';
import {InputText} from 'primereact/inputtext';
import {InputTextarea} from 'primereact/inputtextarea';
import {Toolbar} from 'primereact/toolbar';
import {classNames} from 'primereact/utils';
import React, {useEffect, useRef, useState} from 'react';
import {hutool} from "@moon-cn/hutool";
import {Card} from "primereact/card";
import Link from "next/link";
import Popconfirm from "../../../../commons/Popconfirm";
import {commons} from "../../../../commons/commons";

export default () => {

    const [tableData, setTableData] = useState(null);
    const [productDialog, setProductDialog] = useState(false);
    const [formData, setFormData] = useState({});
    const [submitted, setSubmitted] = useState(false);
    const [globalFilter, setGlobalFilter] = useState('');
    const dt = useRef(null);

    const loadData = () => {
        hutool.http.get("/api/host/list").then(rs => {
            setTableData(rs.content)
        })
    };

    useEffect(loadData, []);


    const openNew = () => {
        setFormData({});
        setSubmitted(false);
        setProductDialog(true);
    };

    const hideDialog = () => {
        setSubmitted(false);
        setProductDialog(false);
    };
    const submitData = () => {
        setSubmitted(true);

        hutool.http.post('/api/host/save', formData).then(rs => {
            commons.message.success(rs.message)

            loadData()
            setProductDialog(false);
            setSubmitted(false)
        }).catch(commons.message.error)


    };

    const editRecord = (data) => {
        setFormData({...data});
        setProductDialog(true);
    };
    const deleteRecord = (record) => {
        hutool.http.post("api/host//delete", {id: record.id}).then(rs => {
            loadData()
            commons.message.success(rs.message)
        })

    };


    const onInputChange = (e, name) => {
        const val = (e.target && e.target.value) || '';
        let _product = {...formData};
        _product[`${name}`] = val;

        setFormData(_product);
    };

    const onInputNumberChange = (e, name) => {
        const val = e.value || 0;
        let _product = {...formData};
        _product[`${name}`] = val;

        setFormData(_product);
    };


    return (
        <Card>
            <Toolbar className="mb-4"
                     start={<InputText type="search" onInput={(e) => setGlobalFilter(e.currentTarget.value)}
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
                globalFilter={globalFilter}
            >
                <Column field="name" header="主机名称"
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

            <Dialog visible={productDialog} header="主机信息" modal
                    className="p-fluid"
                    footer={<>
                        <Button label="取消" text onClick={hideDialog}/>
                        <Button label="确定" onClick={submitData}/>
                    </>}
                    onHide={hideDialog}>

                <div className="field">
                    <label htmlFor="name">Name</label>
                    <InputText
                        id="name"
                        value={formData.name}
                        onChange={(e) => onInputChange(e, 'name')}
                        required
                        autoFocus
                        className={classNames({
                            'p-invalid': submitted && !formData.name
                        })}
                    />
                    {submitted && !formData.name && <small className="p-invalid">Name is required.</small>}
                </div>
                <div className="field">
                    <label htmlFor="description">Description</label>
                    <InputTextarea id="description" value={formData.description}
                                   onChange={(e) => onInputChange(e, 'description')} required rows={3}
                                   cols={20}/>
                </div>

                <div className="formgrid grid">
                    <div className="field col">
                        <label htmlFor="price">Price</label>
                        <InputNumber id="price" value={formData.price}
                                     onValueChange={(e) => onInputNumberChange(e, 'price')} mode="currency"
                                     currency="USD" locale="en-US"/>
                    </div>
                    <div className="field col">
                        <label htmlFor="quantity">Quantity</label>
                        <InputNumber id="quantity" value={formData.quantity}
                                     onValueChange={(e) => onInputNumberChange(e, 'quantity')}/>
                    </div>
                </div>
            </Dialog>


        </Card>
    );
};


