import { useState, useEffect, useCallback } from 'react';
import {
  Drawer, Tabs, Spin, Result, Button, Empty, Image,
  Input, Select, DatePicker, InputNumber, Form, Space, Popconfirm, message, Row, Col, Switch, Upload, Tag,
} from 'antd';
import { ReloadOutlined, PlusOutlined, DeleteOutlined, SaveOutlined, UploadOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { fetchProjectDetail } from '@/api/project';
import {
  updateProject, queryValuationPrice,
  createCollateral, updateCollateral,
  createValuationReport, updateValuationReport, deleteValuationReport,
  createValuationMethod, updateValuationMethod, deleteValuationMethod,
  createSurvey, updateSurvey, deleteSurvey,
  createSurveyPhoto, deleteSurveyPhoto,
  saveOwnershipInfo,
  createReportReview, updateReportReview, deleteReportReview,
} from '@/api/detail-api';
import type { CollateralRecord, ProjectDetail, SurveyRecord } from '@/api/types';
import { uploadFile } from '@/api/file';
import { DEFAULT_PHOTO_CATEGORY, PHOTO_CATEGORY_LABELS, PHOTO_CATEGORY_OPTIONS } from '@/constants/reportFields';
import dayjs from 'dayjs';

interface Props {
  projectId: number | null;
  open: boolean;
  onClose: () => void;
}

const { TextArea } = Input;

function toDate(v: string | null | undefined): dayjs.Dayjs | null {
  return v ? dayjs(v) : null;
}

function formatDateValue(value: unknown) {
  return dayjs.isDayjs(value) ? value.format('YYYY-MM-DD') : value;
}

function formatTimeValue(value: unknown) {
  return dayjs.isDayjs(value) ? value.format('HH:mm') : value;
}

function defaultRecordCode(prefix: string, source: string | number | undefined) {
  const normalized = String(source || Date.now()).replace(/[^A-Za-z0-9-]/g, '-');
  return `${prefix}-${normalized}`.slice(0, 50);
}

const surveyFieldNames = [
  'surveyCode',
  'surveyor',
  'receptionist',
  'receptionistPhone',
  'surveyDate',
  'startTime',
  'endTime',
  'propertyCertVerified',
  'ownershipDispute',
  'remark',
] as const;

const collateralFieldNames = [
  'primaryCollateral',
  'collateralType',
  'collateralName',
  'communityName',
  'building',
  'unitName',
  'doorNumber',
  'collateralAddress',
  'buildingArea',
  'landArea',
  'currentFloor',
  'floorCount',
  'indoorHeight',
  'buildYear',
  'completionDate',
  'actualUse',
  'occupancyStatus',
  'decoration',
  'orientation',
  'spaceLayout',
  'facilitiesCondition',
  'maintenanceCondition',
  'surroundingEnvironment',
] as const;

function pickFormValues(values: Record<string, unknown>, keys: readonly string[]) {
  return keys.reduce<Record<string, unknown>>((payload, key) => {
    payload[key] = values[key];
    return payload;
  }, {});
}

export default function ProjectDetailDrawer({ projectId, open, onClose }: Props) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const [detail, setDetail] = useState<ProjectDetail | null>(null);
  const [savingProject, setSavingProject] = useState(false);

  // Track which items are being added (show cancel button)
  const [addingReport, setAddingReport] = useState(false);
  const [addingMethod, setAddingMethod] = useState<number | null>(null);
  const [addingReview, setAddingReview] = useState<number | null>(null);
  const [addingPhoto, setAddingPhoto] = useState<number | null>(null);
  const [uploadingPhoto, setUploadingPhoto] = useState(false);
  const [uploadedPhotoPath, setUploadedPhotoPath] = useState('');
  const [savingOwnership, setSavingOwnership] = useState(false);
  const [savingSurveyCollateral, setSavingSurveyCollateral] = useState(false);
  const [autoValuating, setAutoValuating] = useState(false);
  const [projectForm] = Form.useForm();

  const loadDetail = useCallback(async (id: number) => {
    setLoading(true);
    setError(false);
    try {
      const data = await fetchProjectDetail(id);
      setDetail(data);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (open && projectId) {
      loadDetail(projectId);
      setAddingReport(false);
      setAddingMethod(null);
      setAddingReview(null);
      setAddingPhoto(null);
      setUploadedPhotoPath('');
    }
  }, [open, projectId, loadDetail]);

  // ========== PROJECT ==========
  const handleSaveProject = async (values: Record<string, unknown>) => {
    if (!detail) return;
    setSavingProject(true);
    try {
      const updated = await updateProject(detail.project.id, {
        ...values as any,
        registrationDate: values.registrationDate instanceof dayjs ? (values.registrationDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.registrationDate,
        valuationTime: values.valuationTime instanceof dayjs ? (values.valuationTime as dayjs.Dayjs).format('YYYY-MM-DD') : values.valuationTime,
      });
      setDetail({ ...detail, project: { ...detail.project, ...updated } });
      message.success('项目信息已保存');
    } catch { /* ignore */ }
    finally { setSavingProject(false); }
  };

  const handleAutoValuation = async () => {
    if (!detail) return;
    const values = projectForm.getFieldsValue();
    const city = values.city as string | undefined;
    const address = values.address as string;
    const valuationTime = values.valuationTime instanceof dayjs
      ? (values.valuationTime as dayjs.Dayjs).format('YYYY-MM-DD')
      : (values.valuationTime as string | undefined);
    if (!address) {
      message.warning('请先填写地址');
      return;
    }
    setAutoValuating(true);
    try {
      const result = await queryValuationPrice(city, address, valuationTime);
      projectForm.setFieldsValue({
        valuationUnitPrice: result.valuationUnitPrice,
        valuationTotalPrice: result.valuationTotalPrice,
      });
      message.success('估值完成，请确认后点击保存');
    } catch { /* ignore */ }
    finally { setAutoValuating(false); }
  };

  // ========== VALUATION REPORT ==========
  const handleSaveReport = async (id: number, values: Record<string, unknown>) => {
    if (!detail) return;
    const updated = await updateValuationReport(id, {
      ...values as any,
      startTime: values.startTime instanceof dayjs ? (values.startTime as dayjs.Dayjs).toISOString() : values.startTime,
      endTime: values.endTime instanceof dayjs ? (values.endTime as dayjs.Dayjs).toISOString() : values.endTime,
      valueDate: values.valueDate instanceof dayjs ? (values.valueDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.valueDate,
      reportIssueDate: values.reportIssueDate instanceof dayjs ? (values.reportIssueDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.reportIssueDate,
      validStartDate: values.validStartDate instanceof dayjs ? (values.validStartDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.validStartDate,
      validEndDate: values.validEndDate instanceof dayjs ? (values.validEndDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.validEndDate,
    });
    setDetail({
      ...detail,
      valuationReports: (detail.valuationReports || []).map(r => r.id === id ? { ...r, ...updated } : r),
    });
    message.success('估价报告已保存');
  };

  const handleAddReport = async (values: Record<string, unknown>) => {
    if (!detail) return;
    const created = await createValuationReport(detail.project.id, {
      ...values as any,
      startTime: values.startTime instanceof dayjs ? (values.startTime as dayjs.Dayjs).toISOString() : values.startTime,
      endTime: values.endTime instanceof dayjs ? (values.endTime as dayjs.Dayjs).toISOString() : values.endTime,
      valueDate: values.valueDate instanceof dayjs ? (values.valueDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.valueDate,
      reportIssueDate: values.reportIssueDate instanceof dayjs ? (values.reportIssueDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.reportIssueDate,
      validStartDate: values.validStartDate instanceof dayjs ? (values.validStartDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.validStartDate,
      validEndDate: values.validEndDate instanceof dayjs ? (values.validEndDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.validEndDate,
    });
    setDetail({ ...detail, valuationReports: [...(detail.valuationReports || []), { ...created, valuationMethods: [], reportReviews: [] }] });
    setAddingReport(false);
    message.success('估价报告已添加');
  };

  const handleDeleteReport = async (id: number) => {
    await deleteValuationReport(id);
    setDetail({ ...detail!, valuationReports: (detail!.valuationReports || []).filter(r => r.id !== id) });
    message.success('估价报告已删除');
  };

  // ========== VALUATION METHOD ==========
  const handleSaveMethod = async (id: number, values: Record<string, unknown>) => {
    if (!detail) return;
    const updated = await updateValuationMethod(id, values as any);
    setDetail({
      ...detail,
      valuationReports: (detail.valuationReports || []).map(r => ({
        ...r,
        valuationMethods: (r.valuationMethods || []).map(m => m.id === id ? { ...m, ...updated } : m),
      })),
    });
    message.success('估价方法已保存');
  };

  const handleAddMethod = async (reportId: number, values: Record<string, unknown>) => {
    if (!detail) return;
    const created = await createValuationMethod(reportId, values as any);
    setDetail({
      ...detail,
      valuationReports: (detail.valuationReports || []).map(r =>
        r.id === reportId ? { ...r, valuationMethods: [...(r.valuationMethods || []), created] } : r
      ),
    });
    setAddingMethod(null);
    message.success('估价方法已添加');
  };

  const handleDeleteMethod = async (id: number) => {
    await deleteValuationMethod(id);
    setDetail({
      ...detail!,
      valuationReports: (detail!.valuationReports || []).map(r => ({
        ...r,
        valuationMethods: (r.valuationMethods || []).filter(m => m.id !== id),
      })),
    });
    message.success('估价方法已删除');
  };

  // ========== REPORT REVIEW ==========
  const handleSaveReview = async (id: number, values: Record<string, unknown>) => {
    if (!detail) return;
    const updated = await updateReportReview(id, {
      ...values as any,
      reviewDate: values.reviewDate instanceof dayjs ? (values.reviewDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.reviewDate,
    });
    setDetail({
      ...detail,
      valuationReports: (detail.valuationReports || []).map(r => ({
        ...r,
        reportReviews: (r.reportReviews || []).map(rv => rv.id === id ? { ...rv, ...updated } : rv),
      })),
    });
    message.success('审核记录已保存');
  };

  const handleAddReview = async (reportId: number, values: Record<string, unknown>) => {
    if (!detail) return;
    const created = await createReportReview(detail.project.id, reportId, {
      ...values as any,
      reviewDate: values.reviewDate instanceof dayjs ? (values.reviewDate as dayjs.Dayjs).format('YYYY-MM-DD') : values.reviewDate,
    });
    setDetail({
      ...detail,
      valuationReports: (detail.valuationReports || []).map(r =>
        r.id === reportId ? { ...r, reportReviews: [...(r.reportReviews || []), created] } : r
      ),
    });
    setAddingReview(null);
    message.success('审核记录已添加');
  };

  const handleDeleteReview = async (id: number) => {
    await deleteReportReview(id);
    setDetail({
      ...detail!,
      valuationReports: (detail!.valuationReports || []).map(r => ({
        ...r,
        reportReviews: (r.reportReviews || []).filter(rv => rv.id !== id),
      })),
    });
    message.success('审核记录已删除');
  };

  // ========== SURVEY ==========
  const handleSaveSurveyWithCollateral = async (
    survey: SurveyRecord | null,
    collateral: CollateralRecord | undefined,
    values: Record<string, unknown>
  ) => {
    if (!detail) return;
    setSavingSurveyCollateral(true);
    try {
      const surveyPayload = pickFormValues(values, surveyFieldNames);
      surveyPayload.surveyDate = formatDateValue(surveyPayload.surveyDate);
      surveyPayload.startTime = formatTimeValue(surveyPayload.startTime);
      surveyPayload.endTime = formatTimeValue(surveyPayload.endTime);

      const collateralPayload: Record<string, unknown> = {
        ...(collateral || {}),
        ...pickFormValues(values, collateralFieldNames),
        completionDate: formatDateValue(values.completionDate),
      };
      collateralPayload.collateralCode = collateral?.collateralCode || defaultRecordCode('COL', detail.project.projectCode || detail.project.id);
      collateralPayload.collateralAddress = collateralPayload.collateralAddress || detail.project.address;
      collateralPayload.collateralName = collateralPayload.collateralName || detail.project.projectName;
      collateralPayload.primaryCollateral = collateralPayload.primaryCollateral ?? true;

      const savedSurvey = survey
        ? await updateSurvey(survey.id, surveyPayload as any)
        : await createSurvey(detail.project.id, surveyPayload as any);
      const savedCollateral = collateral
        ? await updateCollateral(collateral.id, collateralPayload as any)
        : await createCollateral(detail.project.id, collateralPayload as any);

      const nextSurveys = survey
        ? (detail.surveys || []).map(item =>
            item.id === survey.id ? { ...item, ...savedSurvey, photos: item.photos || [] } : item
          )
        : [{ ...savedSurvey, photos: [] }, ...(detail.surveys || [])];
      const nextCollaterals = collateral
        ? (detail.collaterals || []).map(item =>
            item.id === collateral.id ? { ...item, ...savedCollateral } : item
          )
        : [{ ...savedCollateral }, ...(detail.collaterals || [])];

      setDetail({
        ...detail,
        surveys: nextSurveys,
        collaterals: nextCollaterals,
      });
      message.success('勘查及抵押物信息已保存');
    } catch {
      message.error('保存失败');
    } finally {
      setSavingSurveyCollateral(false);
    }
  };

  const handleDeleteSurvey = async (id: number) => {
    await deleteSurvey(id);
    setDetail({ ...detail!, surveys: (detail!.surveys || []).filter(s => s.id !== id) });
    message.success('勘查记录已删除');
  };

  // ========== SURVEY PHOTO ==========
  const handleAddPhoto = async (surveyId: number, values: Record<string, unknown>) => {
    if (!detail) return;
    const photoPath = uploadedPhotoPath || values.photoPath;
    if (!photoPath) {
      message.error('请先上传照片');
      return;
    }
    const created = await createSurveyPhoto(detail.project.id, surveyId, {
      photoPath: photoPath,
      photoCategory: values.photoCategory || DEFAULT_PHOTO_CATEGORY,
      photoDescription: values.photoDescription,
    } as any);
    setDetail({
      ...detail,
      surveys: (detail.surveys || []).map(s =>
        s.id === surveyId ? { ...s, photos: [...(s.photos || []), created] } : s
      ),
    });
    setAddingPhoto(null);
    setUploadedPhotoPath('');
    message.success('照片已添加');
  };

  const handleDeletePhoto = async (photoId: number, surveyId: number) => {
    await deleteSurveyPhoto(photoId);
    setDetail({
      ...detail!,
      surveys: (detail!.surveys || []).map(s =>
        s.id === surveyId ? { ...s, photos: (s.photos || []).filter(p => p.id !== photoId) } : s
      ),
    });
    message.success('照片已删除');
  };

  // ========== OWNERSHIP ==========
  const handleSaveOwnership = async (values: Record<string, unknown>) => {
    if (!detail) return;
    setSavingOwnership(true);
    try {
      const ownershipPayload: Record<string, unknown> = {
        ...(detail.ownershipInfo || {}),
        ...values,
      };
      const saved = await saveOwnershipInfo(detail.project.id, {
        ...(ownershipPayload as any),
        onlineSigningDate: ownershipPayload.onlineSigningDate instanceof dayjs ? (ownershipPayload.onlineSigningDate as dayjs.Dayjs).format('YYYY-MM-DD') : ownershipPayload.onlineSigningDate,
        reportIssueDate: ownershipPayload.reportIssueDate instanceof dayjs ? (ownershipPayload.reportIssueDate as dayjs.Dayjs).format('YYYY-MM-DD') : ownershipPayload.reportIssueDate,
        valuationTimePoint: ownershipPayload.valuationTimePoint instanceof dayjs ? (ownershipPayload.valuationTimePoint as dayjs.Dayjs).format('YYYY-MM-DD') : ownershipPayload.valuationTimePoint,
        rightRegistrationDate: ownershipPayload.rightRegistrationDate instanceof dayjs ? (ownershipPayload.rightRegistrationDate as dayjs.Dayjs).format('YYYY-MM-DD') : ownershipPayload.rightRegistrationDate,
        rightCancellationDate: ownershipPayload.rightCancellationDate instanceof dayjs ? (ownershipPayload.rightCancellationDate as dayjs.Dayjs).format('YYYY-MM-DD') : ownershipPayload.rightCancellationDate,
        landUseStartDate: ownershipPayload.landUseStartDate instanceof dayjs ? (ownershipPayload.landUseStartDate as dayjs.Dayjs).format('YYYY-MM-DD') : ownershipPayload.landUseStartDate,
        landUseEndDate: ownershipPayload.landUseEndDate instanceof dayjs ? (ownershipPayload.landUseEndDate as dayjs.Dayjs).format('YYYY-MM-DD') : ownershipPayload.landUseEndDate,
      });
      setDetail({ ...detail, ownershipInfo: saved });
      message.success('权属信息已保存');
    } catch { /* ignore */ }
    finally { setSavingOwnership(false); }
  };

  // ========== RENDER ==========
  if (loading) {
    return (
      <Drawer title="项目详情" open={open} onClose={onClose} width={900} destroyOnClose>
        <Spin tip="加载中..." style={{ display: 'block', textAlign: 'center', marginTop: 100 }} />
      </Drawer>
    );
  }
  if (error || !detail) {
    return (
      <Drawer title="项目详情" open={open} onClose={onClose} width={900} destroyOnClose>
        <Result status="error" title="加载失败"
          extra={<Button icon={<ReloadOutlined />} onClick={() => projectId && loadDetail(projectId)}>重试</Button>} />
      </Drawer>
    );
  }

  const p = detail.project;
  const collaterals = detail.collaterals || [];
  const valuationReports = detail.valuationReports || [];
  const surveys = detail.surveys || [];
  const ownershipInfo = detail.ownershipInfo;
  const primaryCollateral = collaterals.find(c => c.primaryCollateral) || collaterals[0];
  const surveyItems: Array<SurveyRecord | null> = surveys.length > 0 ? surveys : [null];

  // Common field style
  const fieldStyle: React.CSSProperties = { marginBottom: 8 };

  return (
    <Drawer title="项目详情" open={open} onClose={onClose} width={900} destroyOnClose>
      <Tabs defaultActiveKey="project" style={{ background: 'transparent' }}>

        {/* ====== PROJECT INFO ====== */}
        <Tabs.TabPane tab="项目基本信息" key="project">
          <Form key={`proj-${p.id}`} form={projectForm} initialValues={{
            projectCode: p.projectCode, projectName: p.projectName, city: p.city,
            district: p.district, area: p.area, address: p.address,
            registrar: p.registrar, registrationDate: toDate(p.registrationDate),
            clientName: p.clientName,
            clientContact: p.clientContact, clientPhone: p.clientPhone,
            mortgagorName: p.mortgagorName, mortgagorIdCard: p.mortgagorIdCard,
            mortgagorPhone: p.mortgagorPhone, borrowerName: p.borrowerName,
            borrowerIdCard: p.borrowerIdCard,
            valuationPurpose: p.valuationPurpose, valuationTime: toDate(p.valuationTime),
            expectedPrice: p.expectedPrice, valuationUnitPrice: p.valuationUnitPrice,
            valuationTotalPrice: p.valuationTotalPrice, buildingArea: p.buildingArea,
            valuationType: p.valuationType, status: p.status, remark: p.remark,
          }} onFinish={handleSaveProject} layout="vertical">
            <Row gutter={16}>
              <Col span={12}><Form.Item name="projectCode" label="项目编号" style={fieldStyle}><Input disabled /></Form.Item></Col>
              <Col span={12}><Form.Item name="projectName" label="项目名称" rules={[{ required: true }]} style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="city" label="城市" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="district" label="行政区" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="area" label="片区" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="address" label="地址" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="registrar" label="登记人" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="registrationDate" label="登记日期" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
              <Col span={12}><Form.Item name="clientName" label="委托单位" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="clientContact" label="委托方联系人" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="clientPhone" label="委托方电话" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="mortgagorName" label="抵押人" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="mortgagorIdCard" label="抵押人证件号" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="mortgagorPhone" label="抵押人电话" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="borrowerName" label="借款人" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="borrowerIdCard" label="借款人证件号" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="valuationPurpose" label="估价目的" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={12}><Form.Item name="valuationTime" label="估价时点" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
              <Col span={24}><Form.Item name="expectedPrice" label="期望价格" style={fieldStyle}><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
              <Col span={8}><Form.Item name="valuationTotalPrice" label="估值总价" style={fieldStyle}><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
              <Col span={8}><Form.Item name="valuationUnitPrice" label="估值单价" style={fieldStyle}><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
              <Col span={8}><Form.Item name="buildingArea" label="建筑面积" style={fieldStyle}><InputNumber style={{ width: '100%' }} suffix="㎡" /></Form.Item></Col>

              <Col span={12}>
                <Form.Item name="valuationType" label="估值类型" style={fieldStyle}>
                  <Select allowClear>
                    <Select.Option value="人工估值">人工估值</Select.Option>
                    <Select.Option value="自动估值">自动估值</Select.Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item name="status" label="状态" style={fieldStyle}>
                  <Select>
                    <Select.Option value="未评估">未评估</Select.Option>
                    <Select.Option value="已评估">已评估</Select.Option>
                    <Select.Option value="已出报告">已出报告</Select.Option>
                    <Select.Option value="已结款">已结款</Select.Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={24}><Form.Item name="remark" label="备注" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
            </Row>
            <Form.Item style={{ marginBottom: 0 }}>
              <Space>
                <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={savingProject}>保存项目信息</Button>
                {/*<Button icon={<ThunderboltOutlined />} onClick={handleAutoValuation} loading={autoValuating}>自动估值</Button>
                */}
              </Space>
            </Form.Item>
          </Form>
        </Tabs.TabPane>

        {/* ====== OWNERSHIP INFO ====== */}
        <Tabs.TabPane tab="权属信息" key="ownership">
          <Form key={`ownership-${ownershipInfo?.id || 'new'}`}
            initialValues={{
              rightHolder: ownershipInfo?.rightHolder,
              rightCertificateNumber: ownershipInfo?.rightCertificateNumber,
              registeredAddress: ownershipInfo?.registeredAddress,
              buildingStructure: ownershipInfo?.buildingStructure,
              usage: ownershipInfo?.usage,
              actualUse: ownershipInfo?.actualUse,
              decoration: ownershipInfo?.decoration,
              registeredBuildingArea: ownershipInfo?.registeredBuildingArea,
              currentFloor: ownershipInfo?.currentFloor,
              totalFloors: ownershipInfo?.totalFloors,
              rightNature: ownershipInfo?.rightNature,
              coOwnership: ownershipInfo?.coOwnership,
              houseOwnershipCertificate: ownershipInfo?.houseOwnershipCertificate,
              stateLandUseCertificateNumber: ownershipInfo?.stateLandUseCertificateNumber,
              landUseEndDate: toDate(ownershipInfo?.landUseEndDate),
              landUseArea: ownershipInfo?.landUseArea,
            }}
            onFinish={handleSaveOwnership}
            layout="vertical"
          >
            <Row gutter={16}>
              <Col span={8}><Form.Item name="houseOwnershipCertificate" label="房屋所有权证" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="rightHolder" label="权利人" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="rightCertificateNumber" label="权利证号" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={16}><Form.Item name="registeredAddress" label="证载坐落" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="usage" label="用途" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="actualUse" label="实际用途" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="registeredBuildingArea" label="建筑面积" style={fieldStyle}><InputNumber style={{ width: '100%' }} suffix="㎡" /></Form.Item></Col>
              <Col span={8}><Form.Item name="landUseArea" label="土地使用权面积" style={fieldStyle}><InputNumber style={{ width: '100%' }} suffix="㎡" /></Form.Item></Col>
              <Col span={8}><Form.Item name="rightNature" label="权利性质" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="landUseEndDate" label="土地使用终止日期" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
              <Col span={8}><Form.Item name="decoration" label="装修情况" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="buildingStructure" label="房屋结构" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="currentFloor" label="所在层" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="totalFloors" label="总楼层" style={fieldStyle}><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
              <Col span={8}><Form.Item name="coOwnership" label="共有情况" style={fieldStyle}><Input /></Form.Item></Col>
              <Col span={8}><Form.Item name="stateLandUseCertificateNumber" label="国有土地使用权证号" style={fieldStyle}><Input /></Form.Item></Col>
            </Row>
            <Form.Item style={{ marginBottom: 0 }}>
              <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={savingOwnership}>保存</Button>
            </Form.Item>
          </Form>
        </Tabs.TabPane>

        {/* ====== SURVEYS ====== */}
        <Tabs.TabPane tab={`勘查 (${surveys.length})`} key="survey">
          {surveyItems.map((s, index) => {
            const surveyId = s?.id;
            const photos = s?.photos || [];
            const initialValues = {
              surveyCode: s?.surveyCode || defaultRecordCode('SUR', p.projectCode || p.id),
              surveyor: s?.surveyor,
              receptionist: s?.receptionist,
              receptionistPhone: s?.receptionistPhone,
              surveyDate: toDate(s?.surveyDate),
              startTime: s?.startTime ? dayjs(s.startTime, 'HH:mm') : null,
              endTime: s?.endTime ? dayjs(s.endTime, 'HH:mm') : null,
              propertyCertVerified: s?.propertyCertVerified ?? false,
              ownershipDispute: s?.ownershipDispute,
              remark: s?.remark,
              collateralCode: primaryCollateral?.collateralCode || defaultRecordCode('COL', p.projectCode || p.id),
              collateralType: primaryCollateral?.collateralType,
              collateralName: primaryCollateral?.collateralName || p.projectName,
              collateralAddress: primaryCollateral?.collateralAddress || p.address,
              primaryCollateral: primaryCollateral?.primaryCollateral ?? true,
              actualUse: primaryCollateral?.actualUse,
              occupancyStatus: primaryCollateral?.occupancyStatus,
              decoration: primaryCollateral?.decoration,
              orientation: primaryCollateral?.orientation,
              currentFloor: primaryCollateral?.currentFloor,
              indoorHeight: primaryCollateral?.indoorHeight,
              buildingArea: primaryCollateral?.buildingArea,
              landArea: primaryCollateral?.landArea,
              communityName: primaryCollateral?.communityName,
              building: primaryCollateral?.building,
              unitName: primaryCollateral?.unitName,
              doorNumber: primaryCollateral?.doorNumber,
              buildYear: primaryCollateral?.buildYear,
              floorCount: primaryCollateral?.floorCount,
              propertyRightsYears: primaryCollateral?.propertyRightsYears,
              landUseYears: primaryCollateral?.landUseYears,
              completionDate: toDate(primaryCollateral?.completionDate),
              constructionLand: primaryCollateral?.constructionLand,
              landAcquisition: primaryCollateral?.landAcquisition,
              floorAreaRatio: primaryCollateral?.floorAreaRatio,
              aboveGroundRatio: primaryCollateral?.aboveGroundRatio,
              civilDefenseArea: primaryCollateral?.civilDefenseArea,
              undergroundRatio: primaryCollateral?.undergroundRatio,
              greeningRate: primaryCollateral?.greeningRate,
              buildingDensity: primaryCollateral?.buildingDensity,
              buildingHeight: primaryCollateral?.buildingHeight,
              householdCount: primaryCollateral?.householdCount,
              parkingCount: primaryCollateral?.parkingCount,
              parkingRatio: primaryCollateral?.parkingRatio,
              spaceLayout: primaryCollateral?.spaceLayout,
              facilitiesCondition: primaryCollateral?.facilitiesCondition,
              maintenanceCondition: primaryCollateral?.maintenanceCondition,
              surroundingEnvironment: primaryCollateral?.surroundingEnvironment,
              parcelShape: primaryCollateral?.parcelShape,
              terrain: primaryCollateral?.terrain,
              landLevel: primaryCollateral?.landLevel,
              soilCondition: primaryCollateral?.soilCondition,
              landDevelopmentLevel: primaryCollateral?.landDevelopmentLevel,
              landscape: primaryCollateral?.landscape,
            };
            return (
              <div key={surveyId ? `sur-${surveyId}` : `sur-new-${index}`} style={{ border: '1px solid #d9d9d9', borderRadius: 8, padding: 16, marginBottom: 12 }}>
                <Form
                  key={`survey-collateral-${surveyId || 'new'}-${primaryCollateral?.id || 'new'}`}
                  initialValues={initialValues}
                  onFinish={(v) => handleSaveSurveyWithCollateral(s, primaryCollateral, v)}
                  layout="vertical"
                >
                  <Row gutter={12}>
                    <Col span={24}><h4 style={{ margin: '0 0 8px', fontSize: 14 }}>基础位置</h4></Col>
                    <Col span={6}><Form.Item name="primaryCollateral" label="主抵押物" valuePropName="checked" style={fieldStyle}><Switch /></Form.Item></Col>
                    <Col span={8}><Form.Item name="collateralType" label="类型" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={10}><Form.Item name="collateralName" label="名称" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={8}><Form.Item name="communityName" label="小区" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={4}><Form.Item name="building" label="楼栋" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={4}><Form.Item name="unitName" label="单元" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={4}><Form.Item name="doorNumber" label="门号" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={24}><Form.Item name="collateralAddress" label="地址" style={fieldStyle}><Input /></Form.Item></Col>

                    <Col span={24}><h4 style={{ margin: '8px 0', fontSize: 14 }}>面积与楼层</h4></Col>
                    <Col span={6}><Form.Item name="buildingArea" label="建筑面积" style={fieldStyle}><InputNumber style={{ width: '100%' }} suffix="㎡" /></Form.Item></Col>
                    <Col span={6}><Form.Item name="landArea" label="土地面积" style={fieldStyle}><InputNumber style={{ width: '100%' }} suffix="㎡" /></Form.Item></Col>
                    <Col span={6}><Form.Item name="currentFloor" label="所在层" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={6}><Form.Item name="floorCount" label="总楼层" style={fieldStyle}><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
                    <Col span={8}><Form.Item name="indoorHeight" label="层高" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={8}><Form.Item name="buildYear" label="建成年代" style={fieldStyle}><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
                    <Col span={8}><Form.Item name="completionDate" label="竣工日期" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>

                    <Col span={24}><h4 style={{ margin: '8px 0', fontSize: 14 }}>实体状况</h4></Col>
                    <Col span={6}><Form.Item name="actualUse" label="实际用途" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={6}><Form.Item name="occupancyStatus" label="占用状态" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={6}><Form.Item name="decoration" label="装修" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={6}><Form.Item name="orientation" label="朝向" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={12}><Form.Item name="spaceLayout" label="空间布局" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                    <Col span={12}><Form.Item name="facilitiesCondition" label="设施设备" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                    <Col span={12}><Form.Item name="maintenanceCondition" label="维护" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                    <Col span={12}><Form.Item name="surroundingEnvironment" label="周边环境" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>

                    <Col span={24}><h4 style={{ margin: '8px 0', fontSize: 14 }}>勘查核验</h4></Col>
                    <Col span={8}><Form.Item name="surveyor" label="勘查人" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={8}><Form.Item name="receptionist" label="接待人" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={8}><Form.Item name="receptionistPhone" label="电话" style={fieldStyle}><Input /></Form.Item></Col>
                    <Col span={8}><Form.Item name="surveyDate" label="勘查日期" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                    <Col span={8}><Form.Item name="startTime" label="开始时间" style={fieldStyle}><DatePicker.TimePicker style={{ width: '100%' }} format="HH:mm" /></Form.Item></Col>
                    <Col span={8}><Form.Item name="endTime" label="结束时间" style={fieldStyle}><DatePicker.TimePicker style={{ width: '100%' }} format="HH:mm" /></Form.Item></Col>
                    <Col span={8}><Form.Item name="propertyCertVerified" label="验看房产证" valuePropName="checked" style={fieldStyle}><Switch /></Form.Item></Col>
                    <Col span={24}><Form.Item name="ownershipDispute" label="权属争议" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                    <Col span={24}><Form.Item name="remark" label="备注" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                  </Row>
                  <Form.Item style={{ marginBottom: 8 }}>
                    <Space>
                      <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={savingSurveyCollateral}>保存勘查及抵押物</Button>
                      {surveyId && (
                        <Popconfirm title="确定删除该勘查记录及相关照片?" onConfirm={() => handleDeleteSurvey(surveyId)}>
                          <Button danger icon={<DeleteOutlined />}>删除勘查</Button>
                        </Popconfirm>
                      )}
                    </Space>
                  </Form.Item>
                </Form>

                <div style={{ paddingLeft: 8, borderLeft: '3px solid #52c41a' }}>
                  <h4 style={{ marginBottom: 8, fontSize: 14 }}>勘查照片 ({photos.length})</h4>
                  <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 8 }}>
                    {photos.map(photo => {
                      const imgSrc = (path: string) => path?.startsWith('http') ? path : `/uploads/${path}`;
                      const thumbSrc = (path: string) =>
                        path?.startsWith('http') ? path : `/api/image/thumbnail?path=${encodeURIComponent(path)}&maxWidth=200&maxHeight=200`;
                      return (
                        <div key={photo.id} style={{
                          width: 160, height: 200, textAlign: 'center', position: 'relative',
                          border: '1px solid #f0f0f0', borderRadius: 4, overflow: 'hidden', background: '#fafafa',
                        }}>
                          <Image
                            src={thumbSrc(photo.photoPath)}
                            preview={{ src: imgSrc(photo.photoPath) }}
                            alt={photo.photoDescription}
                            width={160} height={120}
                            style={{ objectFit: 'cover' }}
                            placeholder={
                              <div style={{ width: 160, height: 120, background: '#f0f0f0',
                                display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#ccc' }}>
                                加载中...
                              </div>
                            }
                          />
                          <div style={{ padding: '4px 6px', fontSize: 11, color: '#666', lineHeight: '16px', height: 32, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                            {photo.photoDescription || '-'}
                          </div>
                          <Tag style={{ maxWidth: 136, margin: 0 }} color={photo.photoCategory ? 'blue' : 'default'}>
                            {PHOTO_CATEGORY_LABELS[photo.photoCategory] || '未分类'}
                          </Tag>
                          {surveyId && (
                            <Popconfirm title="确定删除该照片?" onConfirm={() => handleDeletePhoto(photo.id, surveyId)}>
                              <Button size="small" danger icon={<DeleteOutlined />}
                                style={{ position: 'absolute', top: 2, right: 2, opacity: 0.85 }} />
                            </Popconfirm>
                          )}
                        </div>
                      );
                    })}
                    {photos.length === 0 && <Empty description="无照片" image={Empty.PRESENTED_IMAGE_SIMPLE} />}
                  </div>

                  {surveyId && addingPhoto === surveyId && (
                    <Form key="new-photo" onFinish={(v) => handleAddPhoto(surveyId, v)} layout="vertical"
                      initialValues={{ photoCategory: DEFAULT_PHOTO_CATEGORY }}
                      style={{ marginBottom: 4, padding: 12, background: '#f6ffed', borderRadius: 4 }}>
                      <Row gutter={[12, 8]}>
                        <Col span={6}>
                          <Form.Item name="photoCategory" label="照片分类" rules={[{ required: true }]} style={{ marginBottom: 0 }}>
                            <Select options={PHOTO_CATEGORY_OPTIONS} />
                          </Form.Item>
                        </Col>
                        <Col span={10}>
                          <Form.Item name="photoDescription" label="照片描述" style={{ marginBottom: 0 }}>
                            <Input placeholder="照片描述" />
                          </Form.Item>
                        </Col>
                        <Col span={8}>
                          <Form.Item label="上传照片" style={{ marginBottom: 0 }}>
                            <Upload
                              accept="image/*"
                              showUploadList={false}
                              customRequest={async ({ file, onSuccess, onError }) => {
                                setUploadingPhoto(true);
                                try {
                                  const record = await uploadFile(file as File, detail?.project.projectCode);
                                  setUploadedPhotoPath(record.filePath);
                                  message.success('照片上传成功');
                                  if (onSuccess) onSuccess(record);
                                } catch {
                                  message.error('照片上传失败');
                                  if (onError) onError(new Error('上传失败'));
                                } finally {
                                  setUploadingPhoto(false);
                                }
                              }}
                            >
                              <Button icon={<UploadOutlined />} loading={uploadingPhoto}>
                                {uploadedPhotoPath ? '重新上传' : '选择照片'}
                              </Button>
                            </Upload>
                            {uploadedPhotoPath && (
                              <div style={{ marginTop: 4, fontSize: 12, color: '#52c41a' }}>
                                已上传
                              </div>
                            )}
                          </Form.Item>
                        </Col>
                        <Col span={24}>
                          <Space size="small">
                            <Button type="primary" size="small" icon={<SaveOutlined />} htmlType="submit">保存</Button>
                            <Button size="small" onClick={() => { setAddingPhoto(null); setUploadedPhotoPath(''); }}>取消</Button>
                          </Space>
                        </Col>
                      </Row>
                    </Form>
                  )}

                  {surveyId && addingPhoto !== surveyId && (
                    <Button type="dashed" size="small" icon={<PlusOutlined />} onClick={() => { setAddingPhoto(surveyId); setUploadedPhotoPath(''); }}>
                      添加照片
                    </Button>
                  )}
                </div>
              </div>
            );
          })}
        </Tabs.TabPane>

        {/* ====== VALUATION REPORTS ====== */}
        <Tabs.TabPane tab={`估价报告 (${valuationReports.length})`} key="report">
          {valuationReports.length === 0 && !addingReport && <Empty description="无估价报告" image={Empty.PRESENTED_IMAGE_SIMPLE} style={{ marginBottom: 12 }} />}

          {valuationReports.map(r => (
            <div key={`rep-${r.id}`} style={{ border: '1px solid #d9d9d9', borderRadius: 8, padding: 16, marginBottom: 16 }}>
              <Form key={`repf-${r.id}`} initialValues={{
                ...r, startTime: toDate(r.startTime), endTime: toDate(r.endTime),
                valueDate: toDate(r.valueDate),
                reportIssueDate: toDate(r.reportIssueDate),
                validStartDate: toDate(r.validStartDate),
                validEndDate: toDate(r.validEndDate),
              }} onFinish={(v) => handleSaveReport(r.id, v)} layout="vertical">
                <Row gutter={12}>
                  <Col span={12}><Form.Item name="reportCode" label="报告编号" rules={[{ required: true }]} style={fieldStyle}><Input /></Form.Item></Col>
                  <Col span={6}><Form.Item name="unitPrice" label="评估单价" style={fieldStyle}><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
                  <Col span={6}><Form.Item name="totalPrice" label="评估总价（万元）" style={fieldStyle}><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
                  <Col span={6}><Form.Item name="mortgageValue" label="抵押价值（万元）" style={fieldStyle}><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
                  <Col span={6}><Form.Item name="priorityCompensationAmount" label="优先受偿款（元）" style={fieldStyle}><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
                  <Col span={12}><Form.Item name="startTime" label="开始时间" style={fieldStyle}><DatePicker showTime style={{ width: '100%' }} /></Form.Item></Col>
                  <Col span={12}><Form.Item name="endTime" label="结束时间" style={fieldStyle}><DatePicker showTime style={{ width: '100%' }} /></Form.Item></Col>
                  <Col span={6}><Form.Item name="valueDate" label="价值时点" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                  <Col span={6}><Form.Item name="reportIssueDate" label="报告出具日期" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                  <Col span={6}><Form.Item name="validStartDate" label="有效期起" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                  <Col span={6}><Form.Item name="validEndDate" label="有效期止" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                  <Col span={6}><Form.Item name="valuer1Name" label="估价师1" style={fieldStyle}><Input /></Form.Item></Col>
                  <Col span={6}><Form.Item name="valuer1CertNo" label="估价师1证号" style={fieldStyle}><Input /></Form.Item></Col>
                  <Col span={6}><Form.Item name="valuer2Name" label="估价师2" style={fieldStyle}><Input /></Form.Item></Col>
                  <Col span={6}><Form.Item name="valuer2CertNo" label="估价师2证号" style={fieldStyle}><Input /></Form.Item></Col>
                  <Col span={24}><Form.Item name="priorityCompensationDescription" label="优先受偿款说明" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                  <Col span={24}><Form.Item name="valuationResult" label="评价结果" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                  <Col span={24}><Form.Item name="areaEvaluation" label="区域评价" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                  <Col span={24}><Form.Item name="surroundingTransactions" label="周边交易" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                  <Col span={24}><Form.Item name="liquidityAnalysis" label="变现能力分析" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                  <Col span={24}><Form.Item name="floorPlan" label="平面布局" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                  <Col span={8}><Form.Item name="landGrantDeduction" label="出让金扣减" style={fieldStyle}><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
                  <Col span={8}><Form.Item name="decorationNewRate" label="装修成新率" style={fieldStyle}><InputNumber style={{ width: '100%' }} min={0} max={1} step={0.01} /></Form.Item></Col>
                  <Col span={8}><Form.Item name="equipmentNewRate" label="设备成新率" style={fieldStyle}><InputNumber style={{ width: '100%' }} min={0} max={1} step={0.01} /></Form.Item></Col>
                  <Col span={24}><Form.Item name="reportUrl" label="报告URL" style={fieldStyle}><Input /></Form.Item></Col>
                  <Col span={24}><Form.Item name="bankSuggestion" label="银行建议" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                  <Col span={24}><Form.Item name="landPlot" label="地块" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                </Row>
                <Form.Item style={{ marginBottom: 8 }}>
                  <Space>
                    <Button type="primary" icon={<SaveOutlined />} htmlType="submit">保存报告</Button>
                    <Popconfirm title="确定删除该估价报告及相关数据?" onConfirm={() => handleDeleteReport(r.id)}>
                      <Button danger icon={<DeleteOutlined />}>删除报告</Button>
                    </Popconfirm>
                  </Space>
                </Form.Item>
              </Form>

              {/* Valuation Methods */}
              <div style={{ marginTop: 12, paddingLeft: 8, borderLeft: '3px solid #1890ff' }}>
                <h4 style={{ marginBottom: 8, fontSize: 14 }}>估价方法 ({(r.valuationMethods || []).length})</h4>
                {(r.valuationMethods || []).map(m => (
                  <Form key={`meth-${m.id}`} initialValues={m} onFinish={(v) => handleSaveMethod(m.id, v)} layout="inline"
                    style={{ marginBottom: 4, padding: '4px 0', borderBottom: '1px solid #f5f5f5' }}>
                    <Form.Item name="methodCode" rules={[{ required: true }]} style={fieldStyle}><Input placeholder="编号" style={{ width: 90 }} /></Form.Item>
                    <Form.Item name="methodName" style={fieldStyle}><Input placeholder="名称" style={{ width: 90 }} /></Form.Item>
                    <Form.Item name="weight" style={fieldStyle}><InputNumber placeholder="权重" style={{ width: 70 }} /></Form.Item>
                    <Form.Item name="unitPrice" style={fieldStyle}><InputNumber placeholder="单价" style={{ width: 100 }} /></Form.Item>
                    <Form.Item name="appraiserSignature" style={fieldStyle}><Input placeholder="签字" style={{ width: 80 }} /></Form.Item>
                    <Form.Item name="description" style={fieldStyle}><Input placeholder="描述" style={{ width: 150 }} /></Form.Item>
                    <Form.Item style={fieldStyle}>
                      <Space size="small">
                        <Button type="primary" size="small" icon={<SaveOutlined />} htmlType="submit" />
                        <Popconfirm title="确定删除?" onConfirm={() => handleDeleteMethod(m.id)}>
                          <Button size="small" danger icon={<DeleteOutlined />} />
                        </Popconfirm>
                      </Space>
                    </Form.Item>
                  </Form>
                ))}

                {addingMethod === r.id && (
                  <Form key="new-method" onFinish={(v) => handleAddMethod(r.id, v)} layout="inline"
                    style={{ marginBottom: 4, padding: '4px 0', background: '#e6f7ff', borderRadius: 4 }}>
                    <Form.Item name="methodCode" rules={[{ required: true }]} style={fieldStyle}><Input placeholder="编号" style={{ width: 90 }} /></Form.Item>
                    <Form.Item name="methodName" style={fieldStyle}><Input placeholder="名称" style={{ width: 90 }} /></Form.Item>
                    <Form.Item name="weight" style={fieldStyle}><InputNumber placeholder="权重" style={{ width: 70 }} /></Form.Item>
                    <Form.Item name="unitPrice" style={fieldStyle}><InputNumber placeholder="单价" style={{ width: 100 }} /></Form.Item>
                    <Form.Item name="appraiserSignature" style={fieldStyle}><Input placeholder="签字" style={{ width: 80 }} /></Form.Item>
                    <Form.Item name="description" style={fieldStyle}><Input placeholder="描述" style={{ width: 150 }} /></Form.Item>
                    <Form.Item style={fieldStyle}>
                      <Space size="small">
                        <Button type="primary" size="small" icon={<SaveOutlined />} htmlType="submit">保存</Button>
                        <Button size="small" onClick={() => setAddingMethod(null)}>取消</Button>
                      </Space>
                    </Form.Item>
                  </Form>
                )}

                {addingMethod !== r.id && (
                  <Button type="dashed" size="small" icon={<PlusOutlined />} onClick={() => setAddingMethod(r.id)}>添加估价方法</Button>
                )}
              </div>

              {/* Report Reviews */}
              <div style={{ marginTop: 12, paddingLeft: 8, borderLeft: '3px solid #fa8c16' }}>
                <h4 style={{ marginBottom: 8, fontSize: 14 }}>报告审核 ({(r.reportReviews || []).length})</h4>
                {(r.reportReviews || []).map(rv => (
                  <Form key={`rev-${rv.id}`} initialValues={{ ...rv, reviewDate: toDate(rv.reviewDate) }}
                    onFinish={(v) => handleSaveReview(rv.id, v)} layout="inline"
                    style={{ marginBottom: 4, padding: '4px 0', borderBottom: '1px solid #f5f5f5' }}>
                    <Form.Item name="reviewer" style={fieldStyle}><Input placeholder="审核人" style={{ width: 80 }} /></Form.Item>
                    <Form.Item name="reviewDate" style={fieldStyle}><DatePicker placeholder="审核日期" style={{ width: 130 }} /></Form.Item>
                    <Form.Item name="reviewOpinion" style={fieldStyle}><Input placeholder="审核意见" style={{ width: 180 }} /></Form.Item>
                    <Form.Item name="reviewResult" style={fieldStyle}><Input placeholder="审核结果" style={{ width: 90 }} /></Form.Item>
                    <Form.Item style={fieldStyle}>
                      <Space size="small">
                        <Button type="primary" size="small" icon={<SaveOutlined />} htmlType="submit" />
                        <Popconfirm title="确定删除?" onConfirm={() => handleDeleteReview(rv.id)}>
                          <Button size="small" danger icon={<DeleteOutlined />} />
                        </Popconfirm>
                      </Space>
                    </Form.Item>
                  </Form>
                ))}

                {addingReview === r.id && (
                  <Form key="new-review" onFinish={(v) => handleAddReview(r.id, v)} layout="inline"
                    style={{ marginBottom: 4, padding: '4px 0', background: '#fff7e6', borderRadius: 4 }}>
                    <Form.Item name="reviewer" style={fieldStyle}><Input placeholder="审核人" style={{ width: 80 }} /></Form.Item>
                    <Form.Item name="reviewDate" style={fieldStyle}><DatePicker placeholder="审核日期" style={{ width: 130 }} /></Form.Item>
                    <Form.Item name="reviewOpinion" style={fieldStyle}><Input placeholder="审核意见" style={{ width: 180 }} /></Form.Item>
                    <Form.Item name="reviewResult" style={fieldStyle}><Input placeholder="审核结果" style={{ width: 90 }} /></Form.Item>
                    <Form.Item style={fieldStyle}>
                      <Space size="small">
                        <Button type="primary" size="small" icon={<SaveOutlined />} htmlType="submit">保存</Button>
                        <Button size="small" onClick={() => setAddingReview(null)}>取消</Button>
                      </Space>
                    </Form.Item>
                  </Form>
                )}

                {addingReview !== r.id && (
                  <Button type="dashed" size="small" icon={<PlusOutlined />} onClick={() => setAddingReview(r.id)}>添加审核记录</Button>
                )}
              </div>
            </div>
          ))}

          {addingReport && (
            <Form key="new-report" onFinish={handleAddReport} layout="vertical"
              style={{ border: '1px solid #1890ff', borderRadius: 8, padding: 16, marginBottom: 12 }}>
              <Row gutter={12}>
                <Col span={12}><Form.Item name="reportCode" label="报告编号" rules={[{ required: true }]} style={fieldStyle}><Input /></Form.Item></Col>
                <Col span={6}><Form.Item name="unitPrice" label="评估单价" style={fieldStyle}><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
                <Col span={6}><Form.Item name="totalPrice" label="评估总价（万元）" style={fieldStyle}><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="mortgageValue" label="抵押价值（万元）" style={fieldStyle}><InputNumber style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="priorityCompensationAmount" label="优先受偿款（元）" initialValue={0} style={fieldStyle}><InputNumber style={{ width: '100%' }} prefix="¥" /></Form.Item></Col>
                <Col span={12}><Form.Item name="startTime" label="开始时间" style={fieldStyle}><DatePicker showTime style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={12}><Form.Item name="endTime" label="结束时间" style={fieldStyle}><DatePicker showTime style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="valueDate" label="价值时点" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="reportIssueDate" label="报告出具日期" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="validStartDate" label="有效期起" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="validEndDate" label="有效期止" style={fieldStyle}><DatePicker style={{ width: '100%' }} /></Form.Item></Col>
                <Col span={6}><Form.Item name="valuer1Name" label="估价师1" style={fieldStyle}><Input /></Form.Item></Col>
                <Col span={6}><Form.Item name="valuer1CertNo" label="估价师1证号" style={fieldStyle}><Input /></Form.Item></Col>
                <Col span={6}><Form.Item name="valuer2Name" label="估价师2" style={fieldStyle}><Input /></Form.Item></Col>
                <Col span={6}><Form.Item name="valuer2CertNo" label="估价师2证号" style={fieldStyle}><Input /></Form.Item></Col>
                <Col span={24}><Form.Item name="priorityCompensationDescription" label="优先受偿款说明" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                <Col span={24}><Form.Item name="valuationResult" label="评价结果" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
                <Col span={24}><Form.Item name="liquidityAnalysis" label="变现能力分析" style={fieldStyle}><TextArea rows={2} /></Form.Item></Col>
              </Row>
              <Form.Item style={{ marginBottom: 0 }}>
                <Space>
                  <Button type="primary" icon={<SaveOutlined />} htmlType="submit">保存</Button>
                  <Button onClick={() => setAddingReport(false)}>取消</Button>
                </Space>
              </Form.Item>
            </Form>
          )}

          {!addingReport && (
            <Button type="dashed" icon={<PlusOutlined />} onClick={() => setAddingReport(true)} block>添加估价报告</Button>
          )}
        </Tabs.TabPane>

      </Tabs>
    </Drawer>
  );
}
