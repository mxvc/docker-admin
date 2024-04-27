'use client';
import Layout from '../../layout/layout';
import React from "react";
import {hutool} from "@moon-cn/hutool";
import Link from "next/link";
import {ConfirmPopup} from "primereact/confirmpopup";


export default function (props) {
    const isLogin = hutool.storage.get("isLogin")
    if (!isLogin) {
        return <Link href='/auth/login'>请先登录</Link>
    }

    return <Layout>

        {props.children}
    </Layout>;
}
