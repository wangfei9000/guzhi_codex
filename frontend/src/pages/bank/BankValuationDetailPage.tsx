import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Alert, Button, Card, Col, DatePicker, Descriptions, Empty, Form, Input,
  InputNumber, message, Popconfirm, Result, Row, Skeleton, Space, Tag,
  Tooltip, Typography,
} from 'antd';
import {
  ArrowLeftOutlined, DownloadOutlined, FilePdfOutlined, ReloadOutlined,
  SaveOutlined, UserSwitchOutlined,
} from '@ant-design/icons';
import dayjs from 'dayjs';
import PageContainer from '@/components/common/PageContainer';
import { fetchProjectDetail, updateProject } from '@/api/project';
import { checkReportCodeAvailable, createValuationReport, updateValuationReport } from '@/api/detail-api';
import { fetchLatestDownloadableReport, generateReportPdf } from '@/api/report';
import type { ProjectDetail, ProjectRecord, ValuationReportRecord } from '@/api/types';

const { TextArea } = Input;

const STATUS_VALUED = '已评估';
const STATUS_REPORT_ISSUED = '已出报告';
const STATUS_UNVALUED = '未评估';
const VALUATION_TYPE_MANUAL = '人工估值';

const pendingReportCreations = new Map<number, Promise<ValuationReportRecord>>();

function toDate(value?: string | null): dayjs.Dayjs | null {
  return value ? dayjs(value) : null;
}

function formatValue(value?: string | number | null) {
  return value === undefined || value === null || value === '' ? '-' : value;
}

function formatArea(value?: number | null) {
  return value === undefined || value === null ? '-' : `${value.toLocaleString()}㎡`;
}

function formatUnitPrice(value?: number | null) {
  return value === undefined || value === null ? '-' : `¥${value.toLocaleString()}/㎡`;
}

function formatTotalPrice(value?: number | null) {
  return value === undefined || value === null ? '-' : `${value.toLocaleString()} 万元`;
}

function normalizeReportValues(values: Record<string, any>): Record<string, any> {
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

function reportToForm(report: ValuationReportRecord, project: ProjectRecord) {
  return {
    ...report,
    unitPrice: report.unitPrice ?? project.valuationUnitPrice,
    totalPrice: report.totalPrice ?? project.valuationTotalPrice,
    mortgageValue: report.mortgageValue ?? project.valuationTotalPrice,
    startTime: toDate(report.startTime),
    endTime: toDate(report.endTime),
    valueDate: toDate(report.valueDate || project.valuationTime),
    reportIssueDate: toDate(report.reportIssueDate),
    validStartDate: toDate(report.validStartDate),
    validEndDate: toDate(report.validEndDate),
  };
}

function buildReportCode(project: ProjectRecord) {
  const suffix = `BG-${dayjs().format('YYYYMMDDHHmmssSSS')}`;
  const prefix = (project.projectCode || `PRJ-${project.id}`).slice(0, 49 - suffix.length);
  return `${prefix}-${suffix}`;
}

function buildInitialReport(project: ProjectRecord) {
  const issueDate = dayjs();
  const valueDate = project.valuationTime || issueDate.format('YYYY-MM-DD');

  return {
    reportCode: buildReportCode(project),
    startTime: issueDate.toISOString(),
    unitPrice: project.valuationUnitPrice,
    totalPrice: project.valuationTotalPrice,
    mortgageValue: project.valuationTotalPrice,
    priorityCompensationAmount: 0,
    valueDate,
    reportIssueDate: issueDate.format('YYYY-MM-DD'),
    validStartDate: issueDate.format('YYYY-MM-DD'),
    validEndDate: issueDate.add(1, 'year').subtract(1, 'day').format('YYYY-MM-DD'),
  } as Partial<ValuationReportRecord>;
}

async function ensureBankReport(project: ProjectRecord) {
  const pending = pendingReportCreations.get(project.id);
  if (pending) {
    return pending;
  }

  const task = createValuationReport(project.id, buildInitialReport(project))
    .catch((error) => {
      pendingReportCreations.delete(project.id);
      throw error;
    });
  pendingReportCreations.set(project.id, task);
  return task;
}

export default function BankValuationDetailPage() {
  const { projectId } = useParams();
  const navigate = useNavigate();
  const [detail, setDetail] = useState<ProjectDetail | null>(null);
  const [selectedReportId, setSelectedReportId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [savingReport, setSavingReport] = useState(false);
  const [generatingReport, setGeneratingReport] = useState(false);
  const [downloadingReport, setDownloadingReport] = useState(false);
  const [transferring, setTransferring] = useState(false);
  const [reportForm] = Form.useForm();

  const numericProjectId = Number(projectId);

  const loadDetail = useCallback(async () => {
    if (!numericProjectId) return;
    setLoading(true);
    setError(false);

    try {
      let data = await fetchProjectDetail(numericProjectId);
      if (!data.valuationReports?.length) {
        const report = await ensureBankReport(data.project);
        data = {
          ...data,
          valuationReports: [{ ...report, valuationMethods: [], reportReviews: [] }],
        };
      }

      const firstReport = data.valuationReports?.[0];
      setDetail(data);
      setSelectedReportId(firstReport?.id ?? null);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [numericProjectId]);

  useEffect(() => {
    loadDetail();
  }, [loadDetail]);

  const activeReport = useMemo(() => {
    const reports = detail?.valuationReports || [];
    return reports.find(report => report.id === selectedReportId) || reports[0] || null;
  }, [detail?.valuationReports, selectedReportId]);

  useEffect(() => {
    if (detail && activeReport) {
      reportForm.setFieldsValue(reportToForm(activeReport, detail.project));
    } else {
      reportForm.resetFields();
    }
  }, [activeReport, detail, reportForm]);

  const updateReportInState = (report: ValuationReportRecord) => {
    setDetail(prev => {
      if (!prev) return prev;
      const reports = prev.valuationReports || [];
      const exists = reports.some(item => item.id === report.id);
      const nextReport = {
        ...report,
        valuationMethods: report.valuationMethods || [],
        reportReviews: report.reportReviews || [],
      };
      return {
        ...prev,
        valuationReports: exists
          ? reports.map(item =>
              item.id === report.id
                ? {
                    ...item,
                    ...report,
                    valuationMethods: report.valuationMethods || item.valuationMethods || [],
                    reportReviews: report.reportReviews || item.reportReviews || [],
                  }
                : item
            )
          : [nextReport, ...reports],
      };
    });
  };

  const handleSaveReport = async (values: Record<string, any>) => {
    if (!activeReport) return;

    const reportCode = String(values.reportCode || '').trim();
    if (!reportCode) {
      message.error('请输入报告编号');
      return;
    }

    setSavingReport(true);
    try {
      if (reportCode !== activeReport.reportCode) {
        const available = await checkReportCodeAvailable(reportCode, activeReport.id);
        if (!available) {
          message.error('报告编号不可用');
          return;
        }
      }

      const updated = await updateValuationReport(activeReport.id, {
        ...activeReport,
        ...normalizeReportValues(values),
        reportCode,
      } as any);
      updateReportInState({ ...activeReport, ...updated });
      message.success('估价报告已保存');
    } catch {
      message.error('保存失败');
    } finally {
      setSavingReport(false);
    }
  };

  const buildReportPayloadForPdf = () => {
    if (!detail || !activeReport) return null;

    const today = dayjs().format('YYYY-MM-DD');
    const values = normalizeReportValues(reportForm.getFieldsValue());
    return {
      ...activeReport,
      ...values,
      unitPrice: values.unitPrice ?? activeReport.unitPrice ?? detail.project.valuationUnitPrice,
      totalPrice: values.totalPrice ?? activeReport.totalPrice ?? detail.project.valuationTotalPrice,
      mortgageValue: values.mortgageValue ?? activeReport.mortgageValue ?? detail.project.valuationTotalPrice,
      valueDate: values.valueDate ?? activeReport.valueDate ?? detail.project.valuationTime ?? today,
      reportIssueDate: values.reportIssueDate ?? activeReport.reportIssueDate ?? today,
      validStartDate: values.validStartDate ?? activeReport.validStartDate ?? today,
      validEndDate: values.validEndDate ?? activeReport.validEndDate ?? dayjs().add(1, 'year').subtract(1, 'day').format('YYYY-MM-DD'),
    } as Partial<ValuationReportRecord>;
  };

  const project = detail?.project;
  const hasValuationPrice = project?.valuationUnitPrice !== undefined
    && project.valuationUnitPrice !== null
    && project.valuationTotalPrice !== undefined
    && project.valuationTotalPrice !== null;
  const canGenerateReport = !!activeReport
    && (project?.status === STATUS_VALUED || project?.status === STATUS_REPORT_ISSUED)
    && hasValuationPrice;
  const canDownloadReport = !!activeReport && project?.status !== STATUS_UNVALUED;
  const generateDisabledReason = !project
    ? ''
    : project.status !== STATUS_VALUED && project.status !== STATUS_REPORT_ISSUED
      ? '项目状态为已评估后才能生成报告'
      : !hasValuationPrice
        ? '项目有单价和总价后才能生成报告'
        : '';

  const handleGenerateReport = async () => {
    if (!detail || !activeReport) return;
    if (!canGenerateReport) {
      message.warning(generateDisabledReason);
      return;
    }

    const payload = buildReportPayloadForPdf();
    if (!payload) return;

    setGeneratingReport(true);
    try {
      const updated = await updateValuationReport(activeReport.id, payload);
      const { reportUrl, endTime } = await generateReportPdf(activeReport.id);
      const reportWithUrl = { ...activeReport, ...updated, reportUrl, endTime: endTime || updated.endTime };
      const updatedProject = await updateProject(detail.project.id, {
        ...detail.project,
        status: STATUS_REPORT_ISSUED,
      });
      setDetail(prev => {
        if (!prev) return prev;
        return {
          ...prev,
          project: { ...prev.project, ...updatedProject },
          valuationReports: (prev.valuationReports || []).map(report =>
            report.id === activeReport.id ? { ...report, ...reportWithUrl } : report
          ),
        };
      });
      reportForm.setFieldsValue(reportToForm(reportWithUrl, { ...detail.project, ...updatedProject }));
      message.success('报告已生成');
    } catch {
      message.error('报告生成失败');
    } finally {
      setGeneratingReport(false);
    }
  };

  const handleDownloadReport = async () => {
    if (!detail || !activeReport) return;

    setDownloadingReport(true);
    try {
      const latestReport = await fetchLatestDownloadableReport(detail.project.id);
      updateReportInState(latestReport);
      setSelectedReportId(latestReport.id);

      if (!latestReport.reportUrl) {
        message.warning('暂无可下载报告，请先生成报告');
        return;
      }

      window.open(latestReport.reportUrl, '_blank');
    } catch {
      message.warning('暂无可下载报告，请先生成报告');
    } finally {
      setDownloadingReport(false);
    }
  };

  const handleTransferManual = async () => {
    if (!detail) return;

    setTransferring(true);
    try {
      const updatedProject = await updateProject(detail.project.id, {
        ...detail.project,
        status: STATUS_UNVALUED,
        valuationType: VALUATION_TYPE_MANUAL,
        valuationUnitPrice: null as unknown as number,
        valuationTotalPrice: null as unknown as number,
      });

      let reports = detail.valuationReports || [];
      if (activeReport?.reportUrl) {
        const clearedReport = await updateValuationReport(activeReport.id, {
          ...activeReport,
          reportUrl: null as unknown as string,
        } as any);
        reports = reports.map(report =>
          report.id === activeReport.id ? { ...report, ...clearedReport, reportUrl: '' } : report
        );
      }

      setDetail({
        ...detail,
        project: { ...detail.project, ...updatedProject },
        valuationReports: reports,
      });
      message.success('已转人工估值');
    } catch {
      message.error('转人工失败');
    } finally {
      setTransferring(false);
    }
  };

  if (loading) {
    return (
      <PageContainer title="估值详情">
        <Skeleton active paragraph={{ rows: 12 }} />
      </PageContainer>
    );
  }

  if (error || !detail || !project) {
    return (
      <PageContainer title="估值详情">
        <Result
          status="error"
          title="加载失败"
          extra={<Button icon={<ReloadOutlined />} onClick={loadDetail}>重试</Button>}
        />
      </PageContainer>
    );
  }

  return (
    <PageContainer title="估值详情">
      <Space style={{ marginBottom: 16 }} wrap>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/bank/valuation-list')}>
          返回估值列表
        </Button>
        <Tag color="blue">{project.projectCode}</Tag>
        <Tag color={project.valuationType === VALUATION_TYPE_MANUAL ? 'orange' : 'cyan'}>
          {project.valuationType || '-'}
        </Tag>
      </Space>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={16}>
          <Card size="small" title="估值信息">
            <Descriptions bordered size="small" column={{ xs: 1, md: 2 }}>
              <Descriptions.Item label="项目编号">{formatValue(project.projectCode)}</Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={
                  project.status === STATUS_REPORT_ISSUED
                    ? 'orange'
                    : project.status === STATUS_VALUED
                      ? 'green'
                      : 'default'
                }>
                  {formatValue(project.status)}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="城市">{formatValue(project.city)}</Descriptions.Item>
              <Descriptions.Item label="行政区">{formatValue(project.district)}</Descriptions.Item>
              <Descriptions.Item label="片区">{formatValue(project.area)}</Descriptions.Item>
              <Descriptions.Item label="建筑面积">{formatArea(project.buildingArea)}</Descriptions.Item>
              <Descriptions.Item label="地址" span={2}>{formatValue(project.address)}</Descriptions.Item>
              <Descriptions.Item label="估价时点">{formatValue(project.valuationTime)}</Descriptions.Item>
              <Descriptions.Item label="单价">{formatUnitPrice(project.valuationUnitPrice)}</Descriptions.Item>
              <Descriptions.Item label="总价">{formatTotalPrice(project.valuationTotalPrice)}</Descriptions.Item>
              <Descriptions.Item label="委托单位">{formatValue(project.clientName)}</Descriptions.Item>
              <Descriptions.Item label="备注" span={2}>{formatValue(project.remark)}</Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>

        <Col xs={24} lg={8}>
          <Card size="small" title="估价报告">
            {activeReport ? (
              <Space direction="vertical" style={{ width: '100%' }} size="middle">
                <div>
                  <Typography.Text type="secondary">报告编号</Typography.Text>
                  <Typography.Paragraph strong copyable style={{ marginBottom: 0 }}>
                    {activeReport.reportCode}
                  </Typography.Paragraph>
                </div>
                <Tooltip title={generateDisabledReason}>
                  <Button
                    type="primary"
                    icon={<FilePdfOutlined />}
                    loading={generatingReport}
                    disabled={!canGenerateReport}
                    onClick={handleGenerateReport}
                    block
                  >
                    生成报告
                  </Button>
                </Tooltip>
                <Button
                  icon={<DownloadOutlined />}
                  disabled={!canDownloadReport}
                  loading={downloadingReport}
                  onClick={handleDownloadReport}
                  block
                >
                  下载报告
                </Button>
                <Popconfirm
                  title="确认转人工估值？"
                  description="转人工后项目将回到未评估状态。"
                  okText="确认"
                  cancelText="取消"
                  onConfirm={handleTransferManual}
                >
                  <Button
                    danger
                    icon={<UserSwitchOutlined />}
                    loading={transferring}
                    disabled={project.valuationType === VALUATION_TYPE_MANUAL}
                    block
                  >
                    转人工
                  </Button>
                </Popconfirm>
              </Space>
            ) : (
              <Empty description="暂无估价报告" />
            )}
          </Card>
        </Col>
      </Row>

      <div style={{ marginTop: 16 }}>
        {!activeReport ? (
          <Empty description="暂无估价报告" />
        ) : (
          <Card size="small" title="报告内容">
            {!canGenerateReport && (
              <Alert
                type="info"
                showIcon
                style={{ marginBottom: 16 }}
                message={generateDisabledReason}
              />
            )}
            <Form form={reportForm} layout="vertical" onFinish={handleSaveReport}>
              <Row gutter={12}>
                <Col xs={24} md={8}>
                  <Form.Item
                    name="reportCode"
                    label="报告编号"
                    rules={[{ required: true, message: '请输入报告编号' }]}
                  >
                    <Input placeholder="请输入报告编号" />
                  </Form.Item>
                </Col>
                <Col xs={24} md={8}>
                  <Form.Item name="unitPrice" label="评估单价">
                    <InputNumber style={{ width: '100%' }} prefix="¥" precision={2} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={8}>
                  <Form.Item name="totalPrice" label="评估总价（万元）">
                    <InputNumber style={{ width: '100%' }} precision={2} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={8}>
                  <Form.Item name="mortgageValue" label="抵押价值（万元）">
                    <InputNumber style={{ width: '100%' }} precision={2} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={8}>
                  <Form.Item name="priorityCompensationAmount" label="法定优先受偿款（元）">
                    <InputNumber style={{ width: '100%' }} prefix="¥" precision={2} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={8}>
                  <Form.Item name="valueDate" label="价值时点">
                    <DatePicker style={{ width: '100%' }} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={6}>
                  <Form.Item name="reportIssueDate" label="报告出具日期">
                    <DatePicker style={{ width: '100%' }} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={6}>
                  <Form.Item name="validStartDate" label="有效期起">
                    <DatePicker style={{ width: '100%' }} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={6}>
                  <Form.Item name="validEndDate" label="有效期止">
                    <DatePicker style={{ width: '100%' }} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={6}>
                  <Form.Item name="startTime" label="作业开始">
                    <DatePicker showTime style={{ width: '100%' }} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={6}>
                  <Form.Item name="endTime" label="作业结束">
                    <DatePicker showTime style={{ width: '100%' }} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={6}>
                  <Form.Item name="valuer1Name" label="估价师1">
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24} md={6}>
                  <Form.Item name="valuer1CertNo" label="估价师1证号">
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24} md={6}>
                  <Form.Item name="valuer2Name" label="估价师2">
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24} md={6}>
                  <Form.Item name="valuer2CertNo" label="估价师2证号">
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24}>
                  <Form.Item name="priorityCompensationDescription" label="优先受偿款说明">
                    <TextArea rows={2} />
                  </Form.Item>
                </Col>
                <Col xs={24}>
                  <Form.Item name="valuationResult" label="估价结果">
                    <TextArea rows={3} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item name="areaEvaluation" label="区域评价">
                    <TextArea rows={3} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item name="surroundingTransactions" label="周边成交">
                    <TextArea rows={3} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item name="liquidityAnalysis" label="变现能力分析">
                    <TextArea rows={3} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item name="floorPlan" label="平面布局">
                    <TextArea rows={3} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={8}>
                  <Form.Item name="landGrantDeduction" label="出让金扣减">
                    <InputNumber style={{ width: '100%' }} prefix="¥" precision={2} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={8}>
                  <Form.Item name="decorationNewRate" label="装修成新率">
                    <InputNumber style={{ width: '100%' }} min={0} max={1} step={0.01} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={8}>
                  <Form.Item name="equipmentNewRate" label="设备成新率">
                    <InputNumber style={{ width: '100%' }} min={0} max={1} step={0.01} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item name="bankSuggestion" label="银行建议">
                    <TextArea rows={2} />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item name="landPlot" label="地块说明">
                    <TextArea rows={2} />
                  </Form.Item>
                </Col>
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
