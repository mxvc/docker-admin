import React from 'react';
import {Layout, Menu, message} from 'antd';
import {history} from 'umi';
import {AppstoreOutlined, ClusterOutlined, LogoutOutlined, ProjectOutlined, SettingOutlined} from "@ant-design/icons";
import {notPermitted} from "../utils/SysConfig";
import hutool from "@moon-cn/hutool";
const {Content, Sider} = Layout;


export default class extends React.Component {

  state = {
    key: '/',
  };

  componentDidMount() {
    const loc = this.props.location;
    const {pathname} = loc
    const key = pathname.substr(1)

    this.setState({key})
  }

  logout = () => {
    localStorage.clear();
    const hide = message.loading("注销登录...", 0)
    hutool.http.get("/api/logout").then(rs => {
      history.push("/login")
      hide()
    })

  };

  render() {
    // 权限过滤
    let items = [
      {
        type: 'group', // Must have
        label: '应用平台',
        children: [
          {
            key: 'project',
            label: '项目',
            title:'构建镜像',
            disabled: notPermitted('project:list'),

            icon: <ProjectOutlined style={{color: 'green'}}/>
          },
          {
            key: 'app',
            label: '应用',
            title:'部署容器',
            icon: <AppstoreOutlined style={{color: 'gold'}}/>,
            disabled: notPermitted('app:list'),
          },
          {
            key: 'host',
            label: '主机',
            icon: <ClusterOutlined style={{color: 'darkmagenta'}}/>,
            disabled: notPermitted('host:list'),
          },
        ]
      },


      {
        type: 'group',
        label: '其他',
        children: [
          {
            key: 'settings',
            label: '设置',
            icon: <SettingOutlined style={{color: 'burlywood'}}/>,
            disabled: notPermitted('user:list'),
          },

          {
            key: 'logout',
            label: '退出',
            icon: <LogoutOutlined style={{color: 'rebeccapurple'}}/>
          },
        ]
      },


    ]


    // 过滤disabled
    for (let item of items) {
      let list = item.children;
      if (list) {
        list = list.filter(i => !(i.disabled === true))
        item.children = list
        if (list.length === 0) {
          item.disabled = true;
        }
      }
    }
    items = items.filter(i => !(i.disabled === true))


    return (
      <Layout style={{minHeight: 'calc(100vh - 20px)'}}>
        <Sider width={150}>

          <div className='py-3 m-4  bg-gray-800 flex align-middle justify-center '>
            <a className='text-white' onClick={() => history.push('/')}>容器面板</a>
          </div>
          <Menu theme='dark'
                onClick={this.onClick} selectedKeys={[this.state.key]} items={items}/>
        </Sider>

        <Content className='pl-2'>
          {this.props.children}
        </Content>
      </Layout>
    );

  }

  onClick = ({key}) => {
    this.setState({key})
    if (key === 'logout') {
      this.logout()
      return
    }
    history.push('/' + key)
  }


}


