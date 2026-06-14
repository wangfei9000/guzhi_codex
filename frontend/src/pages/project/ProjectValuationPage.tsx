import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Alert, Button, Card, Col, DatePicker, Empty, Form, Input, InputNumber,
  message, Result, Row, Select, Skeleton, Space, Statistic, Tag,
} from 'antd';
import {
  ArrowLeftOutlined, FilePdfOutlined, PlusOutlined, ReloadOutlined,
  SaveOutlined, ThunderboltOutlined,
} from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import { fetchProjectDetail } from '@/api/project';
import { createValuationReport, queryValuationPrice, updateProject, updateValuationReport } from '@/api/detail-api';
import { generateReportPdf } from '@/api/report';
import type { ProjectDetail, ValuationReportRecord } from '@/api/types';
import dayjs from 'dayjs';

const { TextArea } = Input;
const STATUS_VALUED = '已评估';

function toDate(value?: string | null): dayjs.Dayjs | null {
  return value ? dayjs(value) : null;
}

function toFiniteNumber(value: unknown): number | null {
  if (typeof value === 'number') {
    return Number.isFinite(value) ? value : null;
  }
  if (typeof value === 'string' && value.trim() !== '') {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }
  return null;
}

function calculateTotalPrice(unitPrice: unknown, area: unknown): number | null {
  const unitPriceNumber = toFiniteNumber(unitPrice);
  const areaNumber = toFiniteNumber(area);
  if (unitPriceNumber === null || areaNumber === null) {
    return null;
  }
  return Math.round((unitPriceNumber * areaNumber / 10000) * 100) / 100;
}

function hasValuationValue(value: unknown): boolean {
  return value !== undefined && value !== null && value !== '';
}

function getPrimaryValuationArea(detail: ProjectDetail): number | null {
  const primaryCollateral = detail.collaterals?.find(item => item.primaryCollateral) || detail.collaterals?.[0];
  return toFiniteNumber(primaryCollateral?.buildingArea) ?? toFiniteNumber(detail.project.buildingArea);
}

function normalizeReportValues(values: Record<string, any>) {
  return {
    ...values,
    startTime: dayjs.isDayjs(values.startTime) ? values.startTime.toISOString() : values.startTime,
    endTime: dayjs.isDayjs(values.endTime) ? values.endTime.toISOString() : values.endTime,
    valueDate: dayjs.isDayjs(values.valueDate) ? values.valueDate.format('YYYY-MM-DD') : values.valueDate,
    reportIssueDate: dayjs.isDayjs(values.reportIssueDate) ? values.reportIssueDate.format('YYYY-MM-DD') : values.reportIssueDate,
    validStartDate: dayjs.isDayjs(values.validStartDate) ? values.validStartDate.format('YYYY-MM-DD') : values.validStartDate,
    validEndDate: dayjs.isDayjs(values.validEndDate) ? values.validEndDate.format('YYYY-MM-DD') : values.validEndDate,
  };
}

function reportToForm(report: ValuationReportRecord) {
  return {
    ...report,
    startTime: toDate(report.startTime),
    endTime: toDate(report.endTime),
    valueDate: toDate(report.valueDate),
    reportIssueDate: toDate(report.reportIssueDate),
    validStartDate: toDate(report.validStartDate),
    validEndDate: toDate(report.validEndDate),
  };
}

export default function ProjectValuationPage() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [detail, setDetail] = useState<ProjectDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [selectedReportId, setSelectedReportId] = useState<number | null>(null);
  const [savingProject, setSavingProject] = useState(false);
  const [savingReport, setSavingReport] = useState(false);
  const [creatingReport, setCreatingReport] = useState(false);
  const [autoValuating, setAutoValuating] = useState(false);
  const [generatingPdf, setGeneratingPdf] = useState(false);
  const [projectForm] = Form.useForm();
  const [reportForm] = Form.useForm();

  const numericProjectId = Number(projectId);

  const loadDetail = useCallback(async () => {
    if (!numericProjectId) return;
    setLoading(true);
    setError(false);
    try {
      const data = await fetchProjectDetail(numericProjectId);
      setDetail(data);
      const firstReport = data.valuationReports?.[0];
      setSelectedReportId(firstReport?.id ?? null);
      projectForm.setFieldsValue({
        valuationTime: toDate(data.project.valuationTime),
        valuationUnitPrice: data.project.valuationUnitPrice,
        valuationTotalPrice: data.project.valuationTotalPrice,
      });
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [numericProjectId, projectForm]);

  useEffect(() => {
    loadDetail();
  }, [loadDetail]);

  const activeReport = useMemo(() => (
    detail?.valuationReports?.find(report => report.id === selectedReportId) || null
  ), [detail?.valuationReports, selectedReportId]);

  useEffect(() => {
    if (activeReport) {
      reportForm.setFieldsValue(reportToForm(activeReport));
    } else {
      reportForm.resetFields();
    }
  }, [activeReport, reportForm]);

  const handleSaveProjectValuation = async (values: Record<string, any>) => {
    if (!detail) return;
    const area = getPrimaryValuationArea(detail);
    const calculatedTotalPrice = calculateTotalPrice(values.valuationUnitPrice, area);
    const valuationTotalPrice = hasValuationValue(values.valuationTotalPrice)
      ? values.valuationTotalPrice
      : calculatedTotalPrice;
    const shouldMarkValued = hasValuationValue(values.valuationUnitPrice) && hasValuationValue(valuationTotalPrice);
    if (!hasValuationValue(values.valuationTotalPrice) && valuationTotalPrice !== null) {
      projectForm.setFieldsValue({ valuationTotalPrice });
    }
    setSavingProject(true);
    try {
      const updated = await updateProject(detail.project.id, {
        ...detail.project,
        valuationTime: dayjs.isDayjs(values.valuationTime) ? values.valuationTime.format('YYYY-MM-DD') : values.valuationTime,
        valuationUnitPrice: values.valuationUnitPrice,
        valuationTotalPrice,
        status: shouldMarkValued ? STATUS_VALUED : detail.project.status,
      } as any);
      setDetail({ ...detail, project: { ...detail.project, ...updated } });
      message.success('项目估值缓存已保存');
    } catch {
      message.error('保存失败');
    } finally {
      setSavingProject(false);
    }
  };

  const handleProjectValuationValuesChange = (changedValues: Record<string, any>, allValues: Record<string, any>) => {
    if (!detail || !Object.prototype.hasOwnProperty.call(changedValues, 'valuationUnitPrice')) return;
    const totalPrice = calculateTotalPrice(allValues.valuationUnitPrice, getPrimaryValuationArea(detail));
    projectForm.setFieldsValue({ valuationTotalPrice: totalPrice ?? null });
  };

  const handleAutoValuation = async () => {
    if (!detail) return;
    const values = projectForm.getFieldsValue();
    const valuationTime = dayjs.isDayjs(values.valuationTime)
      ? values.valuationTime.format('YYYY-MM-DD')
      : values.valuationTime;
    setAutoValuating(true);
    try {
      const result = await queryValuationPrice(detail.project.city, detail.project.address, valuationTime);
      projectForm.setFieldsValue(result);
      message.success('自动估值完成，请确认后保存');
    } catch {
      message.error('自动估值失败');
    } finally {
      setAutoValuating(false);
    }
  };

  const handleCreateReport = async () => {
    if (!detail) return;
    setCreatingReport(true);
    try {
      const projectValues = projectForm.getFieldsValue();
      const issueDate = dayjs();
      const valuationTime = projectValues.valuationTime || toDate(detail.project.valuationTime) || issueDate;
      const reportCode = `${detail.project.projectCode}-BG-${issueDate.format('YYYYMMDDHHmm')}`;
      const created = await createValuationReport(detail.project.id, {
        reportCode,
        startTime: issueDate.toISOString(),
        unitPrice: projectValues.valuationUnitPrice,
        totalPrice: projectValues.valuationTotalPrice,
        mortgageValue: projectValues.valuationTotalPrice,
        priorityCompensationAmount: 0,
        valueDate: dayjs.isDayjs(valuationTime) ? valuationTime.format('YYYY-MM-DD') : valuationTime,
        reportIssueDate: issueDate.format('YYYY-MM-DD'),
        validStartDate: issueDate.format('YYYY-MM-DD'),
        validEndDate: issueDate.add(1, 'year').subtract(1, 'day').format('YYYY-MM-DD'),
      } as any);
      setDetail({
        ...detail,
        valuationReports: [...(detail.valuationReports || []), { ...created, valuationMethods: [], reportReviews: [] }],
      });
      setSelectedReportId(created.id);
      message.success('估价报告已创建');
    } catch {
      message.error('创建失败');
    } finally {
      setCreatingReport(false);
    }
  };

  const handleSaveReport = async (values: Record<string, any>) => {
    if (!detail || !activeReport) return;
    setSavingReport(true);
    try {
      const updated = await updateValuationReport(activeReport.id, {
        ...activeReport,
        ...normalizeReportValues(values),
      } as any);
      setDetail({
        ...detail,
        valuationReports: (detail.valuationReports || []).map(report =>
          report.id === activeReport.id ? { ...report, ...updated } : report
        ),
      });
      message.success('估价报告已保存');
    } catch {
      message.error('保存失败');
    } finally {
      setSavingReport(false);
    }
  };

  const handleGeneratePdf = async () => {
    if (!detail || !activeReport) return;
    setGeneratingPdf(true);
    try {
      const { reportUrl, endTime } = await generateReportPdf(activeReport.id);
      setDetail({
        ...detail,
        valuationReports: (detail.valuationReports || []).map(report =>
          report.id === activeReport.id ? { ...report, reportUrl, endTime: endTime || report.endTime } : report
        ),
      });
      message.success('PDF报告生成成功');
      if (reportUrl) {
        window.open(reportUrl, '_blank');
      }
    } catch {
      message.error('PDF生成失败');
    } finally {
      setGeneratingPdf(false);
    }
  };

  if (loading) {
    return (
      <PageContainer title="估值工作区">
        <Skeleton active paragraph={{ rows: 12 }} />
      </PageContainer>
    );
  }

  if (error || !detail) {
    return (
      <PageContainer title="估值工作区">
        <Result
          status="error"
          title="加载失败"
          extra={<Button icon={<ReloadOutlined />} onClick={loadDetail}>重试</Button>}
        />
      </PageContainer>
    );
  }

  const { project } = detail;
  const reports = detail.valuationReports || [];
  const primaryCollateral = detail.collaterals?.find(item => item.primaryCollateral) || detail.collaterals?.[0];

  return (
    <PageContainer title="估值工作区">
      <Space style={{ marginBottom: 16 }} wrap>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/project/list')}>返回项目列表</Button>
        <Tag color="blue">{project.projectCode}</Tag>
        <span>{project.projectName || project.address}</span>
      </Space>

      <Row gutter={[16, 16]}>
        <Col span={16}>
          <Card size="small" title="项目估值">
            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={8}><Statistic title="估价对象地址" value={primaryCollateral?.collateralAddress || project.address || '-'} /></Col>
              <Col span={8}><Statistic title="建筑面积" value={primaryCollateral?.buildingArea || project.buildingArea || '-'} suffix="㎡" /></Col>
              <Col span={8}><Statistic title="当前状态" value={project.status || '-'} /></Col>
            </Row>
            <Form
              form={projectForm}
              layout="vertical"
              onFinish={handleSaveProjectValuation}
              onValuesChange={handleProjectValuationValuesChange}
            >
              <Row gutter={12}>
                <Col span={8}><Form.Item name="valuationTime" label="估价时点"><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={8}><Form.Item name="valuationUnitPrice" label="自动/缓存单价"><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
                <Col span={8}><Form.Item name="valuationTotalPrice" label="自动/缓存总价（万元）"><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
              </Row>
              <Space>
                <Button icon={<ThunderboltOutlined />} loading={autoValuating} onClick={handleAutoValuation}>自动估值</Button>
                <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={savingProject}>保存估值</Button>
              </Space>
            </Form>
          </Card>
        </Col>

        <Col span={8}>
          <Card size="small" title="报告批次">
            <Space direction="vertical" style={{ width: '100%' }}>
              <Select
                value={selectedReportId ?? undefined}
                placeholder="选择估价报告"
                options={reports.map(report => ({ label: report.reportCode, value: report.id }))}
                onChange={setSelectedReportId}
                style={{ width: '100%' }}
              />
              <Button icon={<PlusOutlined />} onClick={handleCreateReport} loading={creatingReport} block>
                新建估价报告
              </Button>
              <Button
                type="primary"
                icon={<FilePdfOutlined />}
                disabled={!activeReport}
                loading={generatingPdf}
                onClick={handleGeneratePdf}
                block
              >
                生成 PDF
              </Button>
              {activeReport?.reportUrl && (
                <Button onClick={() => window.open(activeReport.reportUrl, '_blank')} block>
                  下载已生成报告
                </Button>
              )}
            </Space>
          </Card>
        </Col>
      </Row>

      <div style={{ marginTop: 16 }}>
        {!activeReport ? (
          <Empty description="暂无估价报告，请先新建报告批次" />
        ) : (
          <Card size="small" title="估价报告">
            <Alert
              type="info"
              showIcon
              style={{ marginBottom: 16 }}
              message="报告字段会优先用于 PDF 模板；项目估值字段只作为列表展示和自动估值缓存。"
            />
            <Form form={reportForm} layout="vertical" onFinish={handleSaveReport}>
              <Row gutter={12}>
                <Col span={8}><Form.Item name="reportCode" label="报告编号" rules={[{ required: true }]}><Input /></Form.Item></Col>
                <Col span={8}><Form.Item name="unitPrice" label="评估单价"><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
                <Col span={8}><Form.Item name="totalPrice" label="评估总价（万元）"><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={8}><Form.Item name="mortgageValue" label="抵押价值（万元）"><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={8}><Form.Item name="priorityCompensationAmount" label="法定优先受偿款（元）"><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
                <Col span={8}><Form.Item name="valueDate" label="价值时点"><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="reportIssueDate" label="报告出具日期"><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="validStartDate" label="有效期起"><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="validEndDate" label="有效期止"><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="startTime" label="作业开始"><DatePicker showTime style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="endTime" label="作业结束"><DatePicker showTime style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="valuer1Name" label="估价师1"><Input /></Form.Item></Col>
                <Col span={6}><Form.Item name="valuer1CertNo" label="估价师1证号"><Input /></Form.Item></Col>
                <Col span={6}><Form.Item name="valuer2Name" label="估价师2"><Input /></Form.Item></Col>
                <Col span={6}><Form.Item name="valuer2CertNo" label="估价师2证号"><Input /></Form.Item></Col>
                <Col span={24}><Form.Item name="priorityCompensationDescription" label="优先受偿款说明"><TextArea rows={2} /></Form.Item></Col>
                <Col span={24}><Form.Item name="valuationResult" label="估价结果"><TextArea rows={3} /></Form.Item></Col>
                <Col span={12}><Form.Item name="areaEvaluation" label="区域评价"><TextArea rows={3} /></Form.Item></Col>
                <Col span={12}><Form.Item name="surroundingTransactions" label="周边成交"><TextArea rows={3} /></Form.Item></Col>
                <Col span={12}><Form.Item name="liquidityAnalysis" label="变现能力分析"><TextArea rows={3} /></Form.Item></Col>
                <Col span={12}><Form.Item name="floorPlan" label="平面布局"><TextArea rows={3} /></Form.Item></Col>
                <Col span={8}><Form.Item name="landGrantDeduction" label="出让金扣减"><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
                <Col span={8}><Form.Item name="decorationNewRate" label="装修成新率"><InputNumber style={{ width: '100%' }} min={0} max={1} step={0.01} /></Form.Item></Col>
                <Col span={8}><Form.Item name="equipmentNewRate" label="设备成新率"><InputNumber style={{ width: '100%' }} min={0} max={1} step={0.01} /></Form.Item></Col>
                <Col span={12}><Form.Item name="bankSuggestion" label="银行建议"><TextArea rows={2} /></Form.Item></Col>
                <Col span={12}><Form.Item name="landPlot" label="地块说明"><TextArea rows={2} /></Form.Item></Col>
              </Row>
              <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={savingReport}>
                保存报告
              </Button>
            </Form>
          </Card>
        )}
      </div>
    </PageContainer>
  );
}
