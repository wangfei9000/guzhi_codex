import { useCallback, useEffect, useState } from 'react';
import { Button, Card, Col, DatePicker, Form, Input, InputNumber, Row, Table, Tag, message } from 'antd';
import { CalculatorOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { createProject, fetchProjects } from '@/api/project';
import type { ProjectRecord } from '@/api/types';
import PageContainer from '@/components/common/PageContainer';
import { useAuthStore } from '@/store/authStore';

export default function ManualValuationPage() {
  const [loading, setLoading] = useState(false);
  const [tableLoading, setTableLoading] = useState(false);
  const [projects, setProjects] = useState<ProjectRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [form] = Form.useForm();
  const userInfo = useAuthStore((state) => state.userInfo);

  const loadProjects = useCallback(async (nextPage = page) => {
    const clientName = userInfo?.organizationName;
    if (!clientName) {
      setProjects([]);
      setTotal(0);
      return;
    }

    setTableLoading(true);
    try {
      const res = await fetchProjects({
        clientName,
        page: nextPage - 1,
        size: 10,
        sort: 'id,desc',
      });
      setProjects(res.content);
      setTotal(res.totalElements);
    } catch {
      // handled by axios interceptor
    } finally {
      setTableLoading(false);
    }
  }, [page, userInfo?.organizationName]);

  useEffect(() => {
    loadProjects();
  }, [loadProjects]);

  const handleSubmit = async (values: Record<string, unknown>) => {
    const clientName = userInfo?.organizationName;
    if (!clientName) {
      message.error('当前用户未设置所属机构，无法自动带出委托单位');
      return;
    }

    const valuationTime = dayjs.isDayjs(values.valuationTime)
      ? (values.valuationTime as dayjs.Dayjs).format('YYYY-MM-DD')
      : values.valuationTime;
    const address = String(values.address || '').trim();

    setLoading(true);
    try {
      await createProject({
        city: values.city as string,
        district: values.district as string,
        area: values.area as string,
        address,
        buildingArea: values.buildingArea as number,
        valuationTime: valuationTime as string,
        clientName,
        projectName: address,
        status: '未评估',
        valuationType: '人工估值',
      });
      message.success('人工估值项目已保存');
      form.resetFields();
      form.setFieldsValue({ valuationTime: dayjs() });
      if (page !== 1) {
        setPage(1);
      } else {
        loadProjects(1);
      }
    } catch {
      // handled by axios interceptor
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { title: '城市', dataIndex: 'city', width: 100 },
    { title: '行政区', dataIndex: 'district', width: 120 },
    { title: '区域', dataIndex: 'area', width: 140 },
    { title: '地址', dataIndex: 'address', width: 240, ellipsis: true },
    { title: '面积', dataIndex: 'buildingArea', width: 110, render: (value?: number) => value ? `${value}㎡` : '-' },
    {
      title: '单价',
      dataIndex: 'valuationUnitPrice',
      width: 120,
      render: (value?: number) => value ? `¥${value}` : '-',
    },
    {
      title: '总价',
      dataIndex: 'valuationTotalPrice',
      width: 130,
      render: (value?: number) => value ? `¥${value}` : '-',
    },
    { title: '价格时点', dataIndex: 'valuationTime', width: 120 },
    {
      title: '估值状态',
      dataIndex: 'status',
      width: 120,
      render: (status?: string) => <Tag color={status === '已评估' ? 'green' : 'default'}>{status || '-'}</Tag>,
    },
    {
      title: '估值类型',
      dataIndex: 'valuationType',
      width: 120,
      render: (value?: string) => <Tag color={value === '人工估值' ? 'orange' : 'default'}>{value || '-'}</Tag>,
    },
  ];

  return (
    <PageContainer title="人工估值">
      <Card>
        <Form form={form} layout="vertical" onFinish={handleSubmit} initialValues={{ valuationTime: dayjs() }}>
          <Row gutter={16}>
            <Col xs={24} md={8}>
              <Form.Item name="city" label="城市" rules={[{ required: true, message: '请输入城市' }]}>
                <Input placeholder="城市" />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item name="district" label="行政区" rules={[{ required: true, message: '请输入行政区' }]}>
                <Input placeholder="行政区" />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item name="area" label="片区">
                <Input placeholder="片区" />
              </Form.Item>
            </Col>
            <Col xs={24}>
              <Form.Item name="address" label="地址" rules={[{ required: true, message: '请输入地址' }]}>
                <Input placeholder="详细地址" />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item name="buildingArea" label="面积" rules={[{ required: true, message: '请输入面积' }]}>
                <InputNumber style={{ width: '100%' }} min={0} precision={2} addonAfter="㎡" placeholder="面积" />
              </Form.Item>
            </Col>
            <Col xs={24} md={12}>
              <Form.Item name="valuationTime" label="估价时点" rules={[{ required: true, message: '请选择估价时点' }]}>
                <DatePicker style={{ width: '100%' }} placeholder="选择估价时点" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item style={{ marginBottom: 0 }}>
            <Button type="primary" icon={<CalculatorOutlined />} htmlType="submit" loading={loading} size="large">
              人工估值
            </Button>
          </Form.Item>
        </Form>
      </Card>
      <Card title="估值列表" style={{ marginTop: 16 }}>
        <Table
          rowKey="id"
          columns={columns}
          dataSource={projects}
          loading={tableLoading}
          scroll={{ x: 1300 }}
          pagination={{
            current: page,
            total,
            pageSize: 10,
            onChange: (nextPage) => {
              setPage(nextPage);
              loadProjects(nextPage);
            },
            showTotal: (count) => `共 ${count} 条`,
          }}
        />
      </Card>
    </PageContainer>
  );
}
