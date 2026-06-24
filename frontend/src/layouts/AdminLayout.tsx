import { useState, useMemo } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button, Dropdown, Badge, theme } from 'antd';
import {
  DashboardOutlined,
  UserOutlined,
  TeamOutlined,
  SafetyOutlined,
  FileOutlined,
  BellOutlined,
  BankOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  ScheduleOutlined,
  ProjectOutlined,
  FileTextOutlined,
  SafetyCertificateOutlined,
  CameraOutlined,
  CalculatorOutlined,
  SettingOutlined,
  ThunderboltOutlined,
  UnorderedListOutlined,
  RedoOutlined,
  FileExcelOutlined,
  AuditOutlined,
  MessageOutlined,
} from '@ant-design/icons';
import { useAuthStore } from '@/store/authStore';
import { useNotificationStore } from '@/store/notificationStore';
import { useWebSocket } from '@/hooks/useWebSocket';
import type { MenuProps } from 'antd';

const { Header, Sider, Content } = Layout;

const allMenuItems: any[] = [
  { key: '/dashboard', icon: <DashboardOutlined />, label: '仪表盘' },
  //评估公司
  {
    key: '/project',
    icon: <SettingOutlined />,
    label: '个人业务',
    requiredPermission: 'project:manage',
    children: [
      { key: '/order/list', icon: <ScheduleOutlined />, label: '接单列表' ,requiredPermission: 'order:list'  },
      { key: '/project/list', icon: <ProjectOutlined />, label: '项目列表' ,requiredPermission: 'project:list'  },
      { key: '/survey/list', icon: <CameraOutlined />, label: '外勘列表'  ,requiredPermission: 'project:surv' },
      { key: '/report/list', icon: <FileTextOutlined />, label: '报告列表' ,requiredPermission: 'project:report' },
      { key: '/seal/list', icon: <SafetyCertificateOutlined />, label: '盖章列表' ,requiredPermission: 'project:seal' },
    ],
  },
  {
    key: '/bank',
    icon: <BankOutlined />,
    label: '银行业务',
    requiredPermission: 'bank:manage',
    children: [
      { key: '/bank/auto-valuation', icon: <ThunderboltOutlined />, label: '自动估值', requiredPermission: 'bank:auto-valuation' },
      { key: '/bank/valuation-list', icon: <UnorderedListOutlined />, label: '估值列表', requiredPermission: 'bank:valuation-list' },
      //{ key: '/bank/manual-valuation', icon: <CalculatorOutlined />, label: '人工估值', requiredPermission: 'bank:manual-valuation' },
      { key: '/bank/revaluation', icon: <RedoOutlined />, label: '一键复估', requiredPermission: 'bank:revaluation' },
      { key: '/bank/revaluation-list', icon: <FileExcelOutlined />, label: '复估列表', requiredPermission: 'bank:revaluation-list' },
      { key: '/bank/reconciliation', icon: <AuditOutlined />, label: '对账', requiredPermission: 'bank:reconciliation' },
    ],
  },
  { key: '/assistant', icon: <MessageOutlined />, label: '我的助理', requiredPermission: 'assistant:use' },
  {
    key: '/system',
    icon: <SettingOutlined />,
    label: '系统管理',
    requiredPermission: 'system:manage',
    children: [
      { key: '/system/user', icon: <UserOutlined />, label: '用户管理', requiredPermission: 'user:list' },
      { key: '/system/organization', icon: <BankOutlined />, label: '机构管理', requiredPermission: 'organization:list' },
      { key: '/system/role', icon: <TeamOutlined />, label: '角色管理', requiredPermission: 'role:list' },
      { key: '/system/permission', icon: <SafetyOutlined />, label: '权限管理', requiredPermission: 'perm:list' },
      { key: '/report/template-list', icon: <FileTextOutlined />, label: '报告模版', requiredPermission: 'project:report-template' },
    ],
  },

  { key: '/file/manage', icon: <FileOutlined />, label: '文件管理', requiredPermission: 'file:manage' },
  { key: '/chat', icon: <MessageOutlined />, label: '即时聊天' },
  { key: '/notification', icon: <BellOutlined />, label: '通知中心', requiredPermission: 'notification:view' },
];

export default function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { userInfo, logout, hasPermission, permissions } = useAuthStore();

  // Debug: print permissions and user info
  console.log('[Menu] user roles:', userInfo?.roles?.map(r => ({ code: r.roleCode, perms: r.permissions?.map(p => p.permCode) })));
  console.log('[Menu] flat permissions array:', permissions);

  const menuItems = useMemo(() => {
    const filter = (items: any[]): any[] => {
      return items
        .filter(item => {
          const ok = !item.requiredPermission || hasPermission(item.requiredPermission);
          if (item.requiredPermission) {
            console.log('[Menu]', item.key, 'need:', item.requiredPermission, 'has:', ok, 'perms:', permissions);
          }
          return ok;
        })
        .map(item => ({
          ...item,
          children: item.children ? filter(item.children) : undefined,
        }));
    };
    const result = filter(allMenuItems);
    console.log('[Menu] visible keys:', result.map((i: any) => i.key));
    return result;
  }, [hasPermission, permissions]);
  const { unreadCount } = useNotificationStore();
  const { token: themeToken } = theme.useToken();

  useWebSocket();

  const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
    navigate(key);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const dropdownItems: MenuProps['items'] = [
    { key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: handleLogout },
  ];

  const selectedKey = location.pathname;
  const openKeys = ['/project'];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        theme="dark"
        width={220}
        style={{ borderRight: '1px solid #303030' }}
      >
        <div
          style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            fontSize: collapsed ? 16 : 20,
            fontWeight: 700,
            letterSpacing: 2,
            whiteSpace: 'nowrap',
            overflow: 'hidden',
          }}
        >
          {collapsed ? 'G' : '德衡评估'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          defaultOpenKeys={openKeys}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            padding: '0 24px',
            background: themeToken.colorBgContainer,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            borderBottom: '1px solid #f0f0f0',
          }}
        >
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
          />
          <div style={{ display: 'flex', alignItems: 'center', gap: 20 }}>
            <Badge count={unreadCount} size="small">
              <BellOutlined
                style={{ fontSize: 18, cursor: 'pointer' }}
                onClick={() => navigate('/notification')}
              />
            </Badge>
            <Dropdown menu={{ items: dropdownItems }} placement="bottomRight">
              <span style={{ cursor: 'pointer' }}>
                <UserOutlined style={{ marginRight: 8 }} />
                {userInfo?.nickname || userInfo?.username || '用户'}
              </span>
            </Dropdown>
          </div>
        </Header>
        <Content
          style={{
            margin: 16,
            padding: 0,
            background: themeToken.colorBgContainer,
            borderRadius: 8,
            minHeight: 280,
            overflow: 'auto',
          }}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
