import React from 'react';
import AppMenuitem from './AppMenuitem';
import {MenuProvider} from './context/menucontext';

export default () => {
    const model = [
        {
            label: 'Home',
            items: [{label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/'}]
        },
        {
            label: '应用平台',
            icon: 'pi pi-fw pi-briefcase',
            to: '/',
            items: [
                {
                    label: '项目',
                    icon: 'pi pi-github',
                    to: '/project'
                },
                {
                    label: '容器',
                    icon: 'pi pi-box',
                    to: '/app'
                },
                {
                    label: '主机',
                    icon: 'pi pi-server' ,
                    to: '/host'
                },
            ]
        }
    ];

    return <MenuProvider>
        <ul className="layout-menu">
            {model.map((item, i) => <AppMenuitem item={item} root={true} index={i} key={item.label}/>)}
        </ul>
    </MenuProvider>;
};


