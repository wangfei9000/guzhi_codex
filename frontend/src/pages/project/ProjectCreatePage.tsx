import { useState, useEffect, useMemo } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  Form, Input, Select, DatePicker, InputNumber, Button, Row, Col, Card, message,
} from 'antd';
import { SaveOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import { createProject } from '@/api/project';
import { fetchUserOptions, fetchUsersByRole, type UserOption } from '@/api/user';
import dayjs from 'dayjs';

const { TextArea } = Input;

export default function ProjectCreatePage() {
  const [loading, setLoading] = useState(false);
  const [users, setUsers] = useState<UserOption[]>([]);
  const [surveyors, setSurveyors] = useState<UserOption[]>([]);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [form] = Form.useForm();

  const returnTo = useMemo(() => searchParams.get('returnTo') || '/project/list', [searchParams]);

  useEffect(() => {
    fetchUserOptions().then(setUsers).catch(() => {});
    fetchUsersByRole('ROLE_SURVEYOR').then(setSurveyors).catch(() => {});
  }, []);

  const handleSubmit = async (values: Record<string, unknown>) => {
    setLoading(true);
    try {
      await createProject({
        ...values as any,
        registrationDate: values.registrationDate instanceof dayjs
          ? (values.registrationDate as dayjs.Dayjs).format('YYYY-MM-DD')
          : values.registrationDate,
        valuationTime: values.valuationTime instanceof dayjs
          ? (values.valuationTime as dayjs.Dayjs).format('YYYY-MM-DD')
          : values.valuationTime,
      });
      message.success('项目创建成功');
      navigate(returnTo, { replace: true });
    } catch {
      // handled by axios interceptor
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <Card
        title="新增项目"
        extra={
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(returnTo)}>
            返回列表
          </Button>
        }
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{ status: '未评估' }}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="projectName" label="项目名称" rules={[{ required: true, message: '请输入项目名称' }]}>
                <Input placeholder="请输入项目名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="status" label="状态">
                <Select>
                  <Select.Option value="未评估">未评估</Select.Option>
                  <Select.Option value="已评估">已评估</Select.Option>
                  <Select.Option value="已出报告">已出报告</Select.Option>
                  <Select.Option value="已结款">已结款</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="valuationType" label="估值类型">
                <Select allowClear placeholder="选择估值类型">
                  <Select.Option value="人工估值">人工估值</Select.Option>
                  <Select.Option value="自动估值">自动估值</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="city" label="城市">
                <Input placeholder="城市" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="district" label="行政区">
                <Input placeholder="行政区" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="area" label="片区">
                <Input placeholder="片区" />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item name="address" label="地址">
                <Input placeholder="详细地址" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="buildingArea" label="建筑面积">
                <InputNumber style={{ width: '100%' }} min={0} precision={2} addonAfter="㎡" placeholder="建筑面积" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="registrar" label="登记人">
                <Select
                  showSearch
                  placeholder="选择登记人"
                  filterOption={(input, option) =>
                    (option?.label as string || '').toLowerCase().includes(input.toLowerCase())
                  }
                  options={users.map(u => ({
                    label: u.nickname || u.username,
                    value: u.nickname || u.username,
                  }))}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="surveyor" label="勘查人">
                <Select
                  showSearch
                  placeholder="选择勘查人"
                  filterOption={(input, option) =>
                    (option?.label as string || '').toLowerCase().includes(input.toLowerCase())
                  }
                  options={surveyors.map(u => ({
                    label: u.nickname || u.username,
                    value: u.nickname || u.username,
                  }))}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="registrationDate" label="登记日期">
                <DatePicker style={{ width: '100%' }} placeholder="选择登记日期" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="clientName" label="委托单位">
                <Input placeholder="委托单位/银行名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="clientContact" label="委托方联系人">
                <Input placeholder="委托方联系人" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="clientPhone" label="委托方电话">
                <Input placeholder="委托方电话" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="mortgagorName" label="抵押人">
                <Input placeholder="抵押人姓名/名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="mortgagorIdCard" label="抵押人证件号">
                <Input placeholder="抵押人身份证/证件号" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="mortgagorPhone" label="抵押人电话">
                <Input placeholder="抵押人电话" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="borrowerName" label="借款人">
                <Input placeholder="借款人姓名/名称" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="borrowerIdCard" label="借款人证件号">
                <Input placeholder="借款人身份证/证件号" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="valuationPurpose" label="估价目的">
                <Input placeholder="估价目的" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="valuationTime" label="估价时点">
                <DatePicker style={{ width: '100%' }} placeholder="选择估价时点" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="expectedPrice" label="期望价格">
                <InputNumber style={{ width: '100%' }} prefix="¥" placeholder="期望价格" />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item name="remark" label="备注">
                <TextArea rows={3} placeholder="备注信息" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item>
            <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={loading} size="large">
              创建项目
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
