import { useState, useEffect, useCallback } from 'react';
import {
  Tree, Button, Space, Modal, Form, Input, Select, InputNumber, Popconfirm,
  message, Empty, Result, Skeleton, Card,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons';
import { fetchPermissionTree, createPermission, updatePermission, deletePermission } from '@/api/permission';
import type { PermissionInfo } from '@/api/types';
import PageContainer from '@/components/common/PageContainer';

type StatusType = 'loading' | 'error' | 'empty' | 'success';

export default function PermissionPage() {
  const [status, setStatus] = useState<StatusType>('loading');
  const [permissions, setPermissions] = useState<PermissionInfo[]>([]);
  const [selectedKey, setSelectedKey] = useState<number | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingPerm, setEditingPerm] = useState<PermissionInfo | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();

  const loadPermissions = useCallback(async () => {
    setStatus('loading');
    try {
      const data = await fetchPermissionTree();
      setPermissions(data);
      setStatus(data.length === 0 ? 'empty' : 'success');
    } catch {
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    loadPermissions();
  }, [loadPermissions]);

  const handleAdd = (parentId: number | null = null) => {
    setEditingPerm(null);
    form.resetFields();
    form.setFieldsValue({ parentId, type: 'MENU', sortOrder: 0 });
    setModalOpen(true);
  };

  const handleEdit = (perm: PermissionInfo) => {
    setEditingPerm(perm);
    form.setFieldsValue({
      permName: perm.permName,
      permCode: perm.permCode,
      parentId: perm.parentId,
      type: perm.type,
      path: perm.path,
      icon: perm.icon,
      sortOrder: perm.sortOrder,
    });
    setModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    await deletePermission(id);
    message.success('删除成功');
    loadPermissions();
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    setSubmitting(true);
    try {
      if (editingPerm) {
        await updatePermission(editingPerm.id, values);
        message.success('更新成功');
      } else {
        await createPermission(values);
        message.success('创建成功');
      }
      setModalOpen(false);
      loadPermissions();
    } catch { /* handled */ }
    finally {
      setSubmitting(false);
    }
  };

  const makeTreeData = (perms: PermissionInfo[]): any[] => {
    return perms.map((p) => ({
      title: (
        <Space>
          <span>{p.permName}</span>
          <span style={{ color: '#999', fontSize: 12 }}>({p.permCode})</span>
          <Button type="link" size="small" icon={<PlusOutlined />}
            onClick={(e) => { e.stopPropagation(); handleAdd(p.id); }} />
          <Button type="link" size="small" icon={<EditOutlined />}
            onClick={(e) => { e.stopPropagation(); handleEdit(p); }} />
          <Popconfirm title="确定删除?" onConfirm={() => handleDelete(p.id)}
            onPopupClick={(e) => e?.stopPropagation()}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}
              onClick={(e) => e.stopPropagation()} />
          </Popconfirm>
        </Space>
      ),
      key: p.id,
      children: p.children && p.children.length > 0 ? makeTreeData(p.children) : undefined,
    }));
  };

  const renderContent = () => {
    switch (status) {
      case 'loading':
        return <Skeleton active paragraph={{ rows: 8 }} />;
      case 'error':
        return (
          <Result
            status="error"
            title="加载失败"
            extra={<Button icon={<ReloadOutlined />} onClick={loadPermissions}>重试</Button>}
          />
        );
      case 'empty':
        return (
          <>
            <div style={{ marginBottom: 16 }}>
              <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAdd(null)}>
                新增顶级权限
              </Button>
            </div>
            <Empty description="暂无权限数据" />
          </>
        );
      case 'success':
        return (
          <>
            <div style={{ marginBottom: 16 }}>
              <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAdd(null)}>
                新增顶级权限
              </Button>
            </div>
            <Card>
              <Tree
                treeData={makeTreeData(permissions)}
                defaultExpandAll
                blockNode
                selectedKeys={selectedKey ? [selectedKey] : []}
                onSelect={(keys) => setSelectedKey(keys[0] as number)}
              />
            </Card>
          </>
        );
    }
  };

  const parentOptions = permissions
    .filter((p) => p.type === 'MENU')
    .map((p) => ({ label: p.permName, value: p.id }));

  return (
    <PageContainer title="权限管理">
      {renderContent()}

      <Modal
        title={editingPerm ? '编辑权限' : '新增权限'}
        open={modalOpen}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        confirmLoading={submitting}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}>
          <Form.Item name="permName" label="权限名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="permCode" label="权限编码" rules={[{ required: true }]}>
            <Input disabled={!!editingPerm} />
          </Form.Item>
          <Form.Item name="parentId" label="父权限">
            <Select allowClear placeholder="无(顶级)">
              {parentOptions.map((o) => (
                <Select.Option key={o.value} value={o.value}>{o.label}</Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="type" label="类型" rules={[{ required: true }]}>
            <Select>
              <Select.Option value="MENU">菜单</Select.Option>
              <Select.Option value="BUTTON">按钮</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="path" label="路由路径">
            <Input />
          </Form.Item>
          <Form.Item name="icon" label="图标">
            <Input placeholder="Ant Design 图标名" />
          </Form.Item>
          <Form.Item name="sortOrder" label="排序">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
}
