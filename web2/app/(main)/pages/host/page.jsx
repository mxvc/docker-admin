'use client';
import {Button} from 'primereact/button';
import {Column} from 'primereact/column';
import {DataTable} from 'primereact/datatable';
import {Dialog} from 'primereact/dialog';
import {FileUpload} from 'primereact/fileupload';
import {InputNumber} from 'primereact/inputnumber';
import {InputText} from 'primereact/inputtext';
import {InputTextarea} from 'primereact/inputtextarea';
import {RadioButton} from 'primereact/radiobutton';
import {Toast} from 'primereact/toast';
import {Toolbar} from 'primereact/toolbar';
import {classNames} from 'primereact/utils';
import React, {useEffect, useRef, useState} from 'react';
import {hutool} from "@moon-cn/hutool";
import {Card} from "primereact/card";

export default () => {

    const [products, setProducts] = useState(null);
    const [productDialog, setProductDialog] = useState(false);
    const [deleteProductDialog, setDeleteProductDialog] = useState(false);
    const [product, setProduct] = useState({});
    const [submitted, setSubmitted] = useState(false);
    const [globalFilter, setGlobalFilter] = useState('');
    const toast = useRef(null);
    const dt = useRef(null);

    const loadData = () => {
        hutool.http.get("/api/host/list").then(rs => {
            setProducts(rs.content)
        })
    };

    useEffect(loadData, []);


    const openNew = () => {
        setProduct({});
        setSubmitted(false);
        setProductDialog(true);
    };

    const hideDialog = () => {
        setSubmitted(false);
        setProductDialog(false);
    };

    const hideDeleteProductDialog = () => {
        setDeleteProductDialog(false);
    };
    const saveProduct = () => {
        setSubmitted(true);

        hutool.http.post('/api/host/save', product).then(rs => {
            toast.current?.show({
                severity: 'success',
                summary: 'Successful',
                detail: 'Product Updated',
                life: 3000
            });

            loadData()
            setProductDialog(false);
            setSubmitted(false)
        })


    };

    const editProduct = (product) => {
        setProduct({...product});
        setProductDialog(true);
    };

    const confirmDeleteProduct = (product) => {
        setProduct(product);
        setDeleteProductDialog(true);
    };

    const deleteProduct = () => {
        hutool.http.post("api/host//delete", {id: product.id}).then(rs => {
            loadData()
            setDeleteProductDialog(false);
            setProduct({});
            toast.current?.show({
                severity: 'success',
                summary: 'Successful',
                detail: 'Product Deleted',
                life: 3000
            });
        })

    };


    const onInputChange = (e, name) => {
        const val = (e.target && e.target.value) || '';
        let _product = {...product};
        _product[`${name}`] = val;

        setProduct(_product);
    };

    const onInputNumberChange = (e, name) => {
        const val = e.value || 0;
        let _product = {...product};
        _product[`${name}`] = val;

        setProduct(_product);
    };


    return (
        <Card>
            <Toast ref={toast}/>
            <Toolbar className="mb-4"
                     start={<>
                         <Button label="新增" icon="pi pi-plus" severity="success" className=" mr-2"
                                 onClick={openNew}/>
                     </>}
            ></Toolbar>

            <DataTable
                ref={dt}
                value={products}
                dataKey="id"
                paginator
                rows={10}
                rowsPerPageOptions={[5, 10, 25]}
                globalFilter={globalFilter}
                header={<InputText type="search" onInput={(e) => setGlobalFilter(e.currentTarget.value)}
                                   placeholder="搜索..."/>}
                responsiveLayout="scroll"
            >
                <Column field="name" header="名称"></Column>
                <Column field="dockerHost" header="Docker接口" sortable></Column>

                <Column body={rowData => <>
                    <Button icon="pi pi-pencil" rounded severity="success" className="mr-2"
                            onClick={() => editProduct(rowData)}/>
                    <Button icon="pi pi-trash" rounded severity="warning"
                            onClick={() => confirmDeleteProduct(rowData)}/>
                </>}></Column>
            </DataTable>

            <Dialog visible={productDialog} header="Product Details" modal
                    className="p-fluid"
                    footer={<>
                        <Button label="Cancel" icon="pi pi-times" text onClick={hideDialog}/>
                        <Button label="Save" icon="pi pi-check" text onClick={saveProduct}/>
                    </>}
                    onHide={hideDialog}>

                <div className="field">
                    <label htmlFor="name">Name</label>
                    <InputText
                        id="name"
                        value={product.name}
                        onChange={(e) => onInputChange(e, 'name')}
                        required
                        autoFocus
                        className={classNames({
                            'p-invalid': submitted && !product.name
                        })}
                    />
                    {submitted && !product.name && <small className="p-invalid">Name is required.</small>}
                </div>
                <div className="field">
                    <label htmlFor="description">Description</label>
                    <InputTextarea id="description" value={product.description}
                                   onChange={(e) => onInputChange(e, 'description')} required rows={3}
                                   cols={20}/>
                </div>

                <div className="formgrid grid">
                    <div className="field col">
                        <label htmlFor="price">Price</label>
                        <InputNumber id="price" value={product.price}
                                     onValueChange={(e) => onInputNumberChange(e, 'price')} mode="currency"
                                     currency="USD" locale="en-US"/>
                    </div>
                    <div className="field col">
                        <label htmlFor="quantity">Quantity</label>
                        <InputNumber id="quantity" value={product.quantity}
                                     onValueChange={(e) => onInputNumberChange(e, 'quantity')}/>
                    </div>
                </div>
            </Dialog>

            <Dialog visible={deleteProductDialog} style={{width: '450px'}} header="Confirm" modal
                    footer={<>
                        <Button label="No" icon="pi pi-times" text onClick={hideDeleteProductDialog}/>
                        <Button label="Yes" icon="pi pi-check" text onClick={deleteProduct}/>
                    </>} onHide={hideDeleteProductDialog}>
                <div className="flex align-items-center justify-content-center">
                    <i className="pi pi-exclamation-triangle mr-3" style={{fontSize: '2rem'}}/>
                    {product && (<span>
                                    Are you sure you want to delete <b>{product.name}</b>?
                                </span>
                    )}
                </div>
            </Dialog>


        </Card>
    );
};


