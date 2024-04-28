/**
 * 没啥功能，只是简化布局
 */

export default function ({label, labelWidth = 120, children}) {


    return <div className="field grid">
        <label className="col-fixed" style={{width: labelWidth}}>{label}</label>
        <div className="col">
            {children}
        </div>
    </div>
}
