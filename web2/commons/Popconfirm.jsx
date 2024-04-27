import {confirmPopup, ConfirmPopup} from "primereact/confirmpopup";

/**
 * 仿antd写法
 *
 *          <Popconfirm title='确定删除' onConfirm={() => this.handleDelete(record)}>
 *             <a>删除</a>
 *           </Popconfirm>
 * @param title
 * @param onConfirm
 * @param children
 * @returns {JSX.Element}
 */
export default function ({title, onConfirm, icon="pi pi-exclamation-triangle", children}){


    const onClick = (event)=>{
        console.log('onClick')
        event.stopPropagation()
        confirmPopup({
            target: event.currentTarget,
            message: title,
            icon: icon,
            defaultFocus: 'accept',
            accept: onConfirm
        });

        return false
    }

    return <>
        <span onClick={onClick}>
        {children}
            </span>
    </>

}
