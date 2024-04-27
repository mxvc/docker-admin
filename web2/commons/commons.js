import React, {useRef} from "react";
import {Toast} from "primereact/toast";


let globalToastRef = null;
const message = {
    info: (msg) => {
        globalToastRef.current?.show({
            severity: 'success',
            summary: '信息',
            detail: msg,
            life: 3000
        });
    },
    success: msg => {
        globalToastRef.current?.show({
            severity: 'success',
            summary: '成功',
            detail: msg,
            life: 3000
        });
    },
    error: msg => {
        if(msg instanceof  Error){
            msg = msg.message
        }

        globalToastRef.current?.show({
            severity: 'error',
            summary: '错误',
            detail: msg,
            life: 5000
        });
    },



}

export function GlobalToast() {
    const ref = useRef(null);
    globalToastRef = ref
    return <Toast ref={ref} position="top-center"/>
}

export const commons = {
    message
}
