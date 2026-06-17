import { useState, useEffect, useCallback } from 'react';
import {
  Table, Button, Space, Modal, Form, Input, TreeSelect, Popconfirm,
  message, Tag, Empty, Result, Skeleton,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import { fetchRoles, createRole, updateRole, deleteRole } from '@/api/role';
import { fetchPermissionTree } from '@/api/permission';
import type { RoleInfo, PermissionInfo } from '@/api/types';
import PageContainer from '@/components/common/PageContainer';

type StatusType = 'loading' | 'error' | 'empty' | 'success';
type PermissionSelectValue = number | string | { value?: number | string };

export default function RoleListPage() {
  const [status, setStatus] = useState<StatusType>('loading');
  const [roles, setRoles] = useState<RoleInfo[]>([]);
  const [permissions, setPermissions] = useState<PermissionInfo[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingRole, setEditingRole] = useState<RoleInfo | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();

  const loadData = useCallback(async () => {
    setStatus('loading');
    try {
      const [rolesData, permsData] = await Promise.all([fetchRoles(), fetchPermissionTree()]);
      setRoles(rolesData);
      setPermissions(permsData);
      setStatus(rolesData.length === 0 ? 'empty' : 'success');
    } catch {
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleCreate = () => {
    setEditingRole(null);
    form.resetFields();
    setModalOpen(true);
  };

  const handleEdit = (role: RoleInfo) => {
    setEditingRole(role);
    form.setFieldsValue({
      roleName: role.roleName,
      roleCode: role.roleCode,
      description: role.description,
      permissionIds: toDisplayPermissionIds(role.permissions?.map((p) => p.id) || [], permissions),
    });
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    const selectedPermissionIds = normalizePermissionIds(values.permissionIds || []);
    const submitValues = {
      ...values,
      permissionIds: includeParentPermissionIds(selectedPermissionIds, permissions),
    };
    setSubmitting(true);
    try {
      if (editingRole) {
        await updateRole(editingRole.id, submitValues);
        message.success('更新成功');
      } else {
        await createRole(submitValues);
        message.success('创建成功');
      }
      setModalOpen(false);
      loadData();
    } catch { /* handled */ }
    finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    await deleteRole(id);
    message.success('删除成功');
    loadData();
  };

  const makeTreeData = (perms: PermissionInfo[]): any[] => {
    return perms
      .map((p) => ({
        title: p.permName,
        value: p.id,
        key: p.id,
        children: p.children && p.children.length > 0 ? makeTreeData(p.children) : undefined,
      }));
  };

  const includeParentPermissionIds = (selectedIds: number[], perms: PermissionInfo[]) => {
    const result = new Set<number>(selectedIds);

    const walk = (nodes: PermissionInfo[], ancestors: number[]) => {
      nodes.forEach((node) => {
        const nextAncestors = [...ancestors, node.id];
        if (result.has(node.id)) {
          ancestors.forEach((id) => result.add(id));
        }
        if (node.children?.length) {
          walk(node.children, nextAncestors);
        }
      });
    };

    walk(perms, []);
    return Array.from(result);
  };

  const toDisplayPermissionIds = (grantedIds: number[], perms: PermissionInfo[]) => {
    const grantedSet = new Set(grantedIds);
    const displaySet = new Set(grantedIds);

    const hasGrantedDescendant = (node: PermissionInfo): boolean => {
      return !!node.children?.some((child) => grantedSet.has(child.id) || hasGrantedDescendant(child));
    };

    const walk = (nodes: PermissionInfo[]) => {
      nodes.forEach((node) => {
        if (grantedSet.has(node.id) && hasGrantedDescendant(node)) {
          displaySet.delete(node.id);
        }
        if (node.children?.length) {
          walk(node.children);
        }
      });
    };

    walk(perms);
    return Array.from(displaySet);
  };

  const normalizePermissionIds = (value: PermissionSelectValue[]) => {
    return value
      .map((item) => {
        const rawValue = typeof item === 'object' && item !== null ? item.value : item;
        const id = Number(rawValue);
        return Number.isFinite(id) ? id : null;
      })
      .filter((id): id is number => id !== null);
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '角色名称', dataIndex: 'roleName', width: 120 },
    { title: '角色编码', dataIndex: 'roleCode', width: 140 },
    { title: '描述', dataIndex: 'description', width: 200 },
    {
      title: '权限',
      dataIndex: 'permissions',
      render: (perms: PermissionInfo[]) =>
        perms?.length ? perms.map((p) => <Tag key={p.id}>{p.permName}</Tag>) : <Tag>无权限</Tag>,
    },
    {
      title: '操作',
      key: 'action',
      width: 160,
      render: (_: unknown, record: RoleInfo) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm title="确定删除该角色?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const renderContent = () => {
    switch (status) {
      case 'loading':
        return <Skeleton active paragraph={{ rows: 8 }} />;
      case 'error':
        return (
          <Result
            status="error"
            title="加载失败"
            extra={<Button icon={<ReloadOutlined />} onClick={loadData}>重试</Button>}
          />
        );
      case 'empty':
        return (
          <>
            <div style={{ marginBottom: 16 }}>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增角色</Button>
            </div>
            <Empty description="暂无角色数据" />
          </>
        );
      case 'success':
        return (
          <>
            <div style={{ marginBottom: 16 }}>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增角色</Button>
            </div>
            <Table rowKey="id" columns={columns} dataSource={roles} pagination={false} />
          </>
        );
    }
  };

  return (
    <PageContainer title="角色管理">
      {renderContent()}

      <Modal
        title={editingRole ? '编辑角色' : '新增角色'}
        open={modalOpen}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        confirmLoading={submitting}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}>
          <Form.Item name="roleName" label="角色名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="roleCode" label="角色编码" rules={[{ required: true }]}>
            <Input disabled={!!editingRole} />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="permissionIds" label="权限">
            <TreeSelect
              treeData={makeTreeData(permissions)}
              treeCheckable
              showCheckedStrategy="SHOW_ALL"
              placeholder="选择权限"
              style={{ width: '100%' }}
            />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
}
