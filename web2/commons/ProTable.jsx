'use client';
import {Button} from 'primereact/button';
import {DataTable} from 'primereact/datatable';
import {InputText} from 'primereact/inputtext';
import {Toolbar} from 'primereact/toolbar';
import React from 'react';
import {hutool} from "@moon-cn/hutool";
import {Card} from "primereact/card";


/**
 * url, toolbarRender
 */
export default class extends React.Component {

    state = {
        lazyState: {
            first: 0,
            rows: 10,
            page: 0,
            sortField: null,
            sortOrder: null,
            filters: null
        },
        filterDisplay: 'menu', // row, menu

        search: {},
        totalRecords: 0,
        tableData: [],
        loading: false
    }

    componentDidMount() {
        this.loadData()
    }


    loadData = () => {
        const {url} = this.props
        const {lazyState, search} = this.state
        this.setState({loading: true})
        const params = {...search}

        params.pageSize = lazyState.rows
        if (lazyState.page != null) { // 排序时会为空，相当于从第一页
            params.pageNumber = lazyState.page + 1; // springmvc设置了从1开始
        }

        if (lazyState.sortField) {
            params.orderBy = lazyState.sortField + "," + (lazyState.sortOrder == 1 ? "asc" : "desc")
        }
        params.filters = lazyState.filters

        hutool.http.get(url, params).then(rs => {
            const {content, totalElements} = rs
            this.setState({tableData: content, totalRecords: totalElements})
        }).finally(() => {
            this.setState({loading: false})
        })
    };
    setLazyState = e => {
        this.setState({lazyState: e})
    }

    render() {
        const {toolBarRender, url, children, ...rest} = this.props
        const {tableData, filterDisplay, lazyState, totalRecords, loading} = this.state
        return <>
            <Toolbar className="mb-4"
                     start={<InputText type="search"
                                       onInput={(e) => {
                                           let {lazyState} = this.state
                                           this.setState({
                                               search: {searchText: e.currentTarget.value}
                                           })
                                           lazyState.page = undefined
                                           this.setState({lazyState})
                                       }}
                                       placeholder="搜索..."/>}
                     end={<> {toolBarRender && toolBarRender()}
                         <Button icon="pi pi-refresh" text onClick={this.loadData}/>
                         <Button icon="pi pi-filter" text onClick={() => {
                             let {filterDisplay} = this.state
                             filterDisplay = filterDisplay === 'row' ? "menu" : 'row'
                             this.setState({filterDisplay})
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
                onPage={this.setLazyState}
                rowsPerPageOptions={[2, 10, 50, 100, 200, 500, 1000, 2000]}
                alwaysShowPaginator={false}

                sortField={lazyState.sortField}
                sortOrder={lazyState.sortOrder}
                onSort={this.setLazyState}

                onFilter={this.setLazyState}
                filters={lazyState.filters}
                filterDisplay={filterDisplay}

                {...rest}
            >
                {children}
            </DataTable>


        </>

    }

}




