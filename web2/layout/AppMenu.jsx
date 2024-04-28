import React from 'react';
import AppMenuitem from './AppMenuitem';
import { MenuProvider } from './context/menucontext';

export default () => {
    const model = [
        {
            label: 'Home',
            items: [{ label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' }]
        },
        {
            label: 'Pages',
            icon: 'pi pi-fw pi-briefcase',
            to: '/pages',
            items: [
                {
                    label: '主机',
                    icon: 'pi pi-fw pi-pencil',
                    to: '/pages/host'
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


