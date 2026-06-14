import { useCallback, useEffect, useState } from 'react';
import {
  Button, Empty, Form, Input, Modal, Popconfirm, Result, Select, Skeleton, Space, Table, message,
} from 'antd';
import { DeleteOutlined, EditOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import {
  createOrganization,
  deleteOrganization,
  fetchOrganizations,
  updateOrganization,
} from '@/api/organization';
import { fetchReportTemplates } from '@/api/report-template';
import type { OrganizationRecord, ReportTemplateRecord } from '@/api/types';
import PageContainer from '@/components/common/PageContainer';

type StatusType = 'loading' | 'error' | 'empty' | 'success';

export default function OrganizationListPage() {
  const [status, setStatus] = useState<StatusType>('loading');
  const [organizations, setOrganizations] = useState<OrganizationRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingOrganization, setEditingOrganization] = useState<OrganizationRecord | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [templateLoading, setTemplateLoading] = useState(false);
  const [templateOptions, setTemplateOptions] = useState<ReportTemplateRecord[]>([]);
  const [form] = Form.useForm();

  const loadData = useCallback(async () => {
    setStatus('loading');
    try {
      const res = await fetchOrganizations({ page: page - 1, size: 10 });
      setOrganizations(res.content);
      setTotal(res.totalElements);
      setStatus(res.content.length === 0 ? 'empty' : 'success');
    } catch {
      setStatus('error');
    }
  }, [page]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const loadTemplateOptions = useCallback(async () => {
    setTemplateLoading(true);
    try {
      const res = await fetchReportTemplates({ page: 0, size: 200, sort: 'id,desc' });
      setTemplateOptions(res.content);
    } catch {
      // handled by axios interceptor
    } finally {
      setTemplateLoading(false);
    }
  }, []);

  useEffect(() => {
    loadTemplateOptions();
  }, [loadTemplateOptions]);

  const handleCreate = () => {
    setEditingOrganization(null);
    form.resetFields();
    setModalOpen(true);
  };

  const handleEdit = (record: OrganizationRecord) => {
    setEditingOrganization(record);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    setSubmitting(true);
    try {
      if (editingOrganization) {
        await updateOrganization(editingOrganization.id, values);
        message.success('更新成功');
      } else {
        await createOrganization(values);
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
    await deleteOrganization(id);
    message.success('删除成功');
    loadData();
  };

  const columns = [
    { title: '机构ID', dataIndex: 'id', width: 100 },
    { title: '机构类型', dataIndex: 'organizationType', width: 160 },
    { title: '机构名称', dataIndex: 'organizationName', width: 220 },
    { title: '机构联系人', dataIndex: 'contactName', width: 160 },
    { title: '机构联系人电话', dataIndex: 'contactPhone', width: 180 },
    {
      title: '报告模版',
      dataIndex: 'reportTemplateName',
      width: 220,
      render: (value: string | null, record: OrganizationRecord) => {
        if (!record.reportTemplateId) return '-';
        return value ? `${value}（${record.reportTemplateId}）` : record.reportTemplateId;
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 160,
      render: (_: unknown, record: OrganizationRecord) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm title="确定删除该机构?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const toolbar = (
    <div style={{ marginBottom: 16 }}>
      <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增机构</Button>
    </div>
  );

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
            {toolbar}
            <Empty description="暂无机构数据" />
          </>
        );
      case 'success':
        return (
          <>
            {toolbar}
            <Table
              rowKey="id"
              columns={columns}
              dataSource={organizations}
              scroll={{ x: 1180 }}
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
    <PageContainer title="机构管理">
      {renderContent()}

      <Modal
        title={editingOrganization ? '编辑机构' : '新增机构'}
        open={modalOpen}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        confirmLoading={submitting}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}>
          <Form.Item name="organizationType" label="机构类型">
            <Input />
          </Form.Item>
          <Form.Item name="organizationName" label="机构名称" rules={[{ required: true, message: '请输入机构名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="contactName" label="机构联系人">
            <Input />
          </Form.Item>
          <Form.Item name="contactPhone" label="机构联系人电话">
            <Input />
          </Form.Item>
          <Form.Item name="reportTemplateId" label="报告模版">
            <Select
              allowClear
              loading={templateLoading}
              placeholder="请选择报告模版"
              options={templateOptions.map(template => ({
                label: `${template.templateName}（${template.id}）`,
                value: template.id,
              }))}
            />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
}
