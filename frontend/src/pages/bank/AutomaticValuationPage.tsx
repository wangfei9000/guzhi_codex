import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { Button, Card, Col, DatePicker, Form, Input, InputNumber, Row, Select, Space, message } from 'antd';
import { ClearOutlined, ThunderboltOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { fetchProjectById, fetchValuationCities, fetchValuationDistricts, saveAutoValuation } from '@/api/project';
import type { ProjectRecord } from '@/api/types';
import PageContainer from '@/components/common/PageContainer';
import { useAuthStore } from '@/store/authStore';

const { TextArea } = Input;

const VALUATION_TYPE_AUTO = '自动估值';

function formatDate(value: unknown) {
  return dayjs.isDayjs(value) ? value.format('YYYY-MM-DD') : value;
}

export default function AutomaticValuationPage() {
  const [autoLoading, setAutoLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [savedProject, setSavedProject] = useState<ProjectRecord | null>(null);
  const [cities, setCities] = useState<string[]>([]);
  const [districts, setDistricts] = useState<string[]>([]);
  const [districtsLoading, setDistrictsLoading] = useState(false);
  const [form] = Form.useForm();
  const selectedCity = Form.useWatch('city', form);
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const userInfo = useAuthStore((state) => state.userInfo);

  useEffect(() => {
    fetchValuationCities()
      .then(setCities)
      .catch(() => {
        // handled by axios interceptor
      });
  }, []);

  useEffect(() => {
    if (!selectedCity) {
      setDistricts([]);
      return;
    }

    setDistrictsLoading(true);
    fetchValuationDistricts(selectedCity)
      .then((list) => {
        setDistricts(list);
        const currentDistrict = form.getFieldValue('district');
        if (currentDistrict && !list.includes(currentDistrict)) {
          form.setFieldValue('district', undefined);
        }
      })
      .catch(() => {
        // handled by axios interceptor
      })
      .finally(() => setDistrictsLoading(false));
  }, [form, selectedCity]);

  useEffect(() => {
    const projectId = Number(searchParams.get('projectId'));
    if (!projectId) return;

    setDetailLoading(true);
    fetchProjectById(projectId)
      .then((project) => {
        setSavedProject(project);
        form.setFieldsValue({
          city: project.city,
          district: project.district,
          area: project.area,
          address: project.address,
          buildingArea: project.buildingArea,
          valuationTime: project.valuationTime ? dayjs(project.valuationTime) : undefined,
          remark: project.remark,
        });
      })
      .catch(() => {
        // handled by axios interceptor
      })
      .finally(() => setDetailLoading(false));
  }, [form, searchParams]);

  const buildProjectPayload = (values: Record<string, unknown>, valuationType: string, status: string) => {
    const address = String(values.address || '').trim();
    return {
      city: values.city as string,
      district: values.district as string,
      area: values.area as string,
      address,
      buildingArea: values.buildingArea as number,
      valuationTime: formatDate(values.valuationTime) as string,
      remark: values.remark as string,
      clientName: userInfo?.organizationName || '',
      projectName: address || `${values.city || ''}${values.district || ''}${values.area || ''}` || '自动估值项目',
      status,
      valuationType,
    } as Partial<ProjectRecord>;
  };

  const handleAutoValuation = async () => {
    const clientName = userInfo?.organizationName;
    if (!clientName) {
      message.error('当前用户未设置所属机构，无法自动带出委托单位');
      return;
    }

    let values: Record<string, unknown>;
    try {
      values = await form.validateFields(['city', 'district', 'address', 'buildingArea', 'valuationTime']);
    } catch {
      return;
    }
    const valuationTime = formatDate(values.valuationTime) as string | undefined;

    setAutoLoading(true);
    try {
      const payload = {
        ...buildProjectPayload({ ...form.getFieldsValue(), ...values }, VALUATION_TYPE_AUTO, '已评估'),
        id: savedProject?.id,
        valuationTime,
      };
      const project = await saveAutoValuation(payload);

      setSavedProject(project);
      form.setFieldsValue({ remark: project.remark });
      if (project.remark === '该小区无法估值') {
        message.warning('自动估值失败，记录已保存');
      } else {
        message.success('自动估值完成，记录已保存');
      }
      navigate(`/bank/valuation-detail/${project.id}`);
    } catch {
      // handled by axios interceptor
    } finally {
      setAutoLoading(false);
    }
  };

  const handleClear = () => {
    form.resetFields();
    setSavedProject(null);
  };

  return (
    <PageContainer title="自动估值">
      <Card loading={detailLoading}>
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col xs={24} md={8}>
              <Form.Item name="city" label="城市" rules={[{ required: true, message: '请选择城市' }]}>
                <Select
                  showSearch
                  allowClear
                  placeholder="请选择城市"
                  options={cities.map((city) => ({ label: city, value: city }))}
                  onChange={() => form.setFieldValue('district', undefined)}
                  filterOption={(input, option) =>
                    (option?.label as string)?.toLowerCase().includes(input.toLowerCase())
                  }
                />
              </Form.Item>
            </Col>
            <Col xs={24} md={8}>
              <Form.Item name="district" label="行政区" rules={[{ required: true, message: '请选择行政区' }]}>
                <Select
                  showSearch
                  allowClear
                  placeholder={selectedCity ? '请选择行政区' : '请先选择城市'}
                  disabled={!selectedCity}
                  loading={districtsLoading}
                  options={districts.map((district) => ({ label: district, value: district }))}
                  filterOption={(input, option) =>
                    (option?.label as string)?.toLowerCase().includes(input.toLowerCase())
                  }
                />
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
            <Col xs={24}>
              <Form.Item name="remark" label="备注">
                <TextArea rows={3} placeholder="补充说明" />
              </Form.Item>
            </Col>
          </Row>
          <Space wrap>
            <Button type="primary" icon={<ThunderboltOutlined />} loading={autoLoading} onClick={handleAutoValuation}>
              自动估值
            </Button>
            <Button icon={<ClearOutlined />} onClick={handleClear}>
              清空
            </Button>
          </Space>
        </Form>
      </Card>
    </PageContainer>
  );
}
