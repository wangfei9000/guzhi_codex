import { useState, useEffect, useCallback } from 'react';
import {
  Table, Button, Space, Modal, Form, Input, Select, Popconfirm,
  Tag, message, Empty, Result, Skeleton,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, LockOutlined, ReloadOutlined } from '@ant-design/icons';
import { fetchUsers, createUser, updateUser, deleteUser, resetPassword } from '@/api/user';
import { fetchRoles } from '@/api/role';
import { fetchOrganizationOptions } from '@/api/organization';
import type { UserInfo, RoleInfo, OrganizationRecord } from '@/api/types';
import PageContainer from '@/components/common/PageContainer';

type StatusType = 'loading' | 'error' | 'empty' | 'success';

export default function UserListPage() {
  const [status, setStatus] = useState<StatusType>('loading');
  const [users, setUsers] = useState<UserInfo[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [roles, setRoles] = useState<RoleInfo[]>([]);
  const [organizations, setOrganizations] = useState<OrganizationRecord[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserInfo | null>(null);
  const [passwordModalOpen, setPasswordModalOpen] = useState(false);
  const [passwordUserId, setPasswordUserId] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();

  const loadUsers = useCallback(async () => {
    setStatus('loading');
    try {
      const res = await fetchUsers({ page: page - 1, size: 10 });
      setUsers(res.content);
      setTotal(res.totalElements);
      setStatus(res.content.length === 0 ? 'empty' : 'success');
    } catch {
      setStatus('error');
    }
  }, [page]);

  const loadRoles = async () => {
    try {
      const data = await fetchRoles();
      setRoles(data);
    } catch { /* ignore */ }
  };

  const loadOrganizations = async () => {
    try {
      const data = await fetchOrganizationOptions();
      setOrganizations(data);
    } catch { /* ignore */ }
  };

  useEffect(() => {
    loadUsers();
    loadRoles();
    loadOrganizations();
  }, [loadUsers]);

  const handleCreate = () => {
    setEditingUser(null);
    form.resetFields();
    form.setFieldsValue({ status: 1 });
    setModalOpen(true);
  };

  const handleEdit = (user: UserInfo) => {
    setEditingUser(user);
    form.setFieldsValue({
      username: user.username,
      email: user.email,
      phone: user.phone,
      nickname: user.nickname,
      status: user.status,
      organizationId: user.organizationId,
      roleIds: user.roles?.map((r) => r.id),
    });
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    setSubmitting(true);
    try {
      if (editingUser) {
        await updateUser(editingUser.id, {
          email: values.email,
          phone: values.phone,
          nickname: values.nickname,
          status: values.status,
          organizationId: values.organizationId,
          roleIds: values.roleIds,
        });
        message.success('更新成功');
      } else {
        await createUser(values);
        message.success('创建成功');
      }
      setModalOpen(false);
      loadUsers();
    } catch { /* validation handled */ }
    finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    await deleteUser(id);
    message.success('删除成功');
    loadUsers();
  };

  const handleResetPassword = async () => {
    const values = await passwordForm.validateFields();
    if (passwordUserId) {
      await resetPassword(passwordUserId, values.password);
      message.success('密码重置成功');
      setPasswordModalOpen(false);
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '用户名', dataIndex: 'username', width: 120 },
    { title: '昵称', dataIndex: 'nickname', width: 120 },
    { title: '邮箱', dataIndex: 'email', width: 180 },
    { title: '手机号', dataIndex: 'phone', width: 130 },
    { title: '机构', dataIndex: 'organizationName', width: 180, render: (name?: string) => name || '-' },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (s: number) => s === 1 ? <Tag color="green">启用</Tag> : <Tag color="red">禁用</Tag>,
    },
    {
      title: '角色',
      dataIndex: 'roles',
      width: 200,
      render: (r: RoleInfo[]) => r?.map((role) => <Tag key={role.id}>{role.roleName}</Tag>),
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      render: (_: unknown, record: UserInfo) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="link" size="small" icon={<LockOutlined />} onClick={() => {
            setPasswordUserId(record.id);
            setPasswordModalOpen(true);
            passwordForm.resetFields();
          }}>
            重置密码
          </Button>
          <Popconfirm title="确定删除该用户?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const renderContent = () => {
    switch (status) {
      case 'loading':
        return <Skeleton active paragraph={{ rows: 10 }} />;
      case 'error':
        return (
          <Result
            status="error"
            title="加载失败"
            extra={<Button icon={<ReloadOutlined />} onClick={loadUsers}>重试</Button>}
          />
        );
      case 'empty':
        return (
          <>
            <div style={{ marginBottom: 16 }}>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增用户</Button>
            </div>
            <Empty description="暂无用户数据" />
          </>
        );
      case 'success':
        return (
          <>
            <div style={{ marginBottom: 16 }}>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增用户</Button>
            </div>
            <Table
              rowKey="id"
              columns={columns}
              dataSource={users}
              pagination={{
                current: page,
                total,
                pageSize: 10,
                onChange: (p) => setPage(p),
                showTotal: (t) => `共 ${t} 条`,
              }}
            />
          </>
        );
    }
  };

  return (
    <PageContainer title="用户管理">
      {renderContent()}

      {/* Create/Edit Modal */}
      <Modal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={modalOpen}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        confirmLoading={submitting}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}>
          <Form.Item name="username" label="用户名" rules={[{ required: true, min: 3 }]}>
            <Input disabled={!!editingUser} />
          </Form.Item>
          {!editingUser && (
            <Form.Item name="password" label="密码" rules={[{ required: true, min: 6 }]}>
              <Input.Password />
            </Form.Item>
          )}
          <Form.Item name="nickname" label="昵称">
            <Input />
          </Form.Item>
          <Form.Item name="email" label="邮箱">
            <Input />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input />
          </Form.Item>
          <Form.Item name="organizationId" label="所属机构">
            <Select
              allowClear
              showSearch
              placeholder="选择机构"
              optionFilterProp="label"
              options={organizations.map((organization) => ({
                label: organization.organizationName,
                value: organization.id,
              }))}
            />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select>
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="roleIds" label="角色">
            <Select mode="multiple" placeholder="选择角色">
              {roles.map((r) => (
                <Select.Option key={r.id} value={r.id}>{r.roleName}</Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      {/* Reset Password Modal */}
      <Modal
        title="重置密码"
        open={passwordModalOpen}
        onOk={handleResetPassword}
        onCancel={() => setPasswordModalOpen(false)}
        destroyOnClose
      >
        <Form form={passwordForm} layout="vertical" preserve={false}>
          <Form.Item name="password" label="新密码" rules={[{ required: true, min: 6 }]}>
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
}
