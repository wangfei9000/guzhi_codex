import { useCallback, useEffect, useState } from 'react';
import {
  Button, Empty, Form, Input, Modal, Popconfirm, Result, Skeleton,
  Space, Table, Tag, Tooltip, Typography, message,
} from 'antd';
import {
  DeleteOutlined, EditOutlined, PlusOutlined, ReloadOutlined,
} from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import {
  createReportTemplate,
  deleteReportTemplate,
  fetchReportTemplates,
  updateReportTemplate,
} from '@/api/report-template';
import type { ReportTemplateRecord } from '@/api/types';

const { TextArea } = Input;
const { Text } = Typography;

type PageStatus = 'loading' | 'error' | 'empty' | 'success';

export default function ReportTemplateListPage() {
  const [status, setStatus] = useState<PageStatus>('loading');
  const [templates, setTemplates] = useState<ReportTemplateRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingTemplate, setEditingTemplate] = useState<ReportTemplateRecord | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [form] = Form.useForm();

  const loadData = useCallback(async (nextPage = page, nextPageSize = pageSize) => {
    setStatus('loading');
    try {
      const res = await fetchReportTemplates({
        page: nextPage - 1,
        size: nextPageSize,
        sort: 'id,desc',
      });
      setTemplates(res.content);
      setTotal(res.totalElements);
      setStatus(res.content.length === 0 ? 'empty' : 'success');
    } catch {
      setStatus('error');
    }
  }, [page, pageSize]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleCreate = () => {
    setEditingTemplate(null);
    form.resetFields();
    setModalOpen(true);
  };

  const handleEdit = (record: ReportTemplateRecord) => {
    setEditingTemplate(record);
    form.setFieldsValue({
      templateName: record.templateName,
      templateContent: record.templateContent,
    });
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    setSubmitting(true);
    try {
      if (editingTemplate) {
        await updateReportTemplate(editingTemplate.id, {
          templateName: editingTemplate.templateName,
          templateContent: values.templateContent,
          status: editingTemplate.status,
        });
        message.success('模版内容已更新');
      } else {
        await createReportTemplate({
          templateName: values.templateName,
          templateContent: values.templateContent,
          status: '启用',
        });
        message.success('模版已新增');
        setPage(1);
      }
      setModalOpen(false);
      loadData(editingTemplate ? page : 1, pageSize);
    } catch {
      // handled by axios interceptor
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    setDeletingId(id);
    try {
      await deleteReportTemplate(id);
      message.success('模版已删除');
      loadData(page, pageSize);
    } catch {
      // handled by axios interceptor
    } finally {
      setDeletingId(null);
    }
  };

  const columns = [
    { title: '模版ID', dataIndex: 'id', width: 100 },
    { title: '模版名称', dataIndex: 'templateName', width: 220 },
    {
      title: '模版内容',
      dataIndex: 'templateContent',
      hidden: true,
      ellipsis: true,
      render: (value: string) => (
        <Tooltip title={value}>
          <Text style={{ maxWidth: 560 }} ellipsis>
            {value}
          </Text>
        </Tooltip>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (value?: string) => <Tag color={value === '启用' ? 'green' : 'default'}>{value || '-'}</Tag>,
    },
    {
      title: '操作',
      key: 'action',
      width: 170,
      fixed: 'right' as const,
      render: (_: unknown, record: ReportTemplateRecord) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确定删除该模版?"
            okText="确定"
            cancelText="取消"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              loading={deletingId === record.id}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const toolbar = (
    <div style={{ marginBottom: 16 }}>
      <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
        新增模版
      </Button>
    </div>
  );

  const renderContent = () => {
    switch (status) {
      case 'loading':
        return <Skeleton active paragraph={{ rows: 10 }} />;
      case 'error':
        return (
          <>
            {toolbar}
            <Result
              status="error"
              title="加载失败"
              extra={<Button icon={<ReloadOutlined />} onClick={() => loadData(page, pageSize)}>重试</Button>}
            />
          </>
        );
      case 'empty':
        return (
          <>
            {toolbar}
            <Empty description="暂无报告模版" />
          </>
        );
      case 'success':
        return (
          <>
            {toolbar}
            <Table
              rowKey="id"
              columns={columns}
              dataSource={templates}
              scroll={{ x: 640 }}
              pagination={{
                current: page,
                total,
                pageSize,
                showSizeChanger: true,
                showTotal: (count) => `共 ${count} 条`,
                onChange: (nextPage, nextPageSize) => {
                  setPage(nextPage);
                  setPageSize(nextPageSize);
                },
              }}
            />
          </>
        );
    }
  };

  return (
    <PageContainer title="报告模版列表">
      {renderContent()}

      <Modal
        title={editingTemplate ? '编辑模版内容' : '新增模版'}
        open={modalOpen}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        confirmLoading={submitting}
        width={760}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}>
          <Form.Item
            name="templateName"
            label="模版名称"
            rules={[{ required: true, message: '请输入模版名称' }]}
          >
            <Input disabled={!!editingTemplate} placeholder="请输入模版名称" />
          </Form.Item>
          <Form.Item
            name="templateContent"
            label="模版内容（HTML）"
            rules={[{ required: true, message: '请输入模版内容' }]}
          >
            <TextArea rows={14} placeholder="请输入 HTML 内容" />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
}
