'use client';
import {LayoutProvider} from '../layout/context/layoutcontext';
import {addLocale, locale, PrimeReactProvider} from 'primereact/api';
import 'primereact/resources/primereact.css';
import 'primeflex/primeflex.css';
import 'primeicons/primeicons.css';
import 'primereact/resources/themes/lara-light-cyan/theme.css'
import '../styles/layout/layout.scss';
import React from "react";

import zhCn from "./zh-CN.json"
import {hutool} from "@moon-cn/hutool";


import {ConfirmPopup} from "primereact/confirmpopup";
import {commons, GlobalToast} from "@/commons/commons";

addLocale("zh-CN", zhCn["zh-CN"])

locale('zh-CN')

interface RootLayoutProps {
    children: React.ReactNode;
}

hutool.http.globalErrorMessageHandler = function (msg, error) {
    if (error?.code == 401) {
        localStorage.clear()
        window.location.href = "/"
        return
    }

    commons.message.error(msg?.message || msg)

}


export default function RootLayout({children}: RootLayoutProps) {
    return (
        <html lang="zh-CN" suppressHydrationWarning>
        <body>
        <PrimeReactProvider value={{
            locale: 'zh-CN',
        }}

        >
            <LayoutProvider>
                <ConfirmPopup/>
                <GlobalToast/>
                {children}
            </LayoutProvider>
        </PrimeReactProvider>
        </body>
        </html>
    );
}
