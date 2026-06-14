import { useState, useEffect } from 'react';
import {
  Input, Button, Card, message, Spin, Upload, Image, Popconfirm,
  Progress, Row, Col, Switch, DatePicker, TimePicker, Typography, Select, Tag,
} from 'antd';
import { UploadOutlined, DeleteOutlined, UserOutlined, LogoutOutlined } from '@ant-design/icons';
import client from '@/api/client';
import { uploadFile } from '@/api/file';
import { updateSurvey, createSurveyPhoto, deleteSurveyPhoto } from '@/api/detail-api';
import { fetchSurveys } from '@/api/survey-list';
import { useAuthStore } from '@/store/authStore';
import type { SurveyRecord, SurveyPhotoRecord, ProjectRecord, SurveyListRecord } from '@/api/types';
import { DEFAULT_PHOTO_CATEGORY, PHOTO_CATEGORY_LABELS, PHOTO_CATEGORY_OPTIONS } from '@/constants/reportFields';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { Title, Text } = Typography;

export default function SurveyMobilePage() {
  const userInfo = useAuthStore((s) => s.userInfo);
  const logout = useAuthStore((s) => s.logout);

  // ---- Survey list ----
  const [surveyList, setSurveyList] = useState<SurveyListRecord[]>([]);
  const [fetchingList, setFetchingList] = useState(false);

  // ---- Selected survey ----
  const [survey, setSurvey] = useState<SurveyRecord | null>(null);
  const [project, setProject] = useState<ProjectRecord | null>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [newPhotoCategory, setNewPhotoCategory] = useState(DEFAULT_PHOTO_CATEGORY);

  // Current user's display name (used to filter surveyor)
  const currentUserName = userInfo?.nickname || userInfo?.username || '';

  // Load un-surveyed records for current surveyor
  useEffect(() => {
    setFetchingList(true);
    fetchSurveys({ size: 200 })
      .then((res) => {
        // Filter: only "未查勘" status AND assigned to current user
        setSurveyList(res.content.filter(
          (item) => item.surveyStatus === '未查勘' && item.surveyor === currentUserName,
        ));
      })
      .catch(() => {
        message.error('加载列表失败');
      })
      .finally(() => setFetchingList(false));
  }, [currentUserName]);

  // When user selects a survey from the dropdown
  const handleSelectSurvey = async (surveyCode: string) => {
    if (!surveyCode) return;
    setLoading(true);
    try {
      const res = await client.get<{ data: SurveyRecord }>(`/survey/by-short-code/${surveyCode}`);
      const s = res.data.data;
      setSurvey(s);
      try {
        const projRes = await client.get<{ data: ProjectRecord }>(`/project/${(s as any).projectId || 0}`);
        setProject(projRes.data.data);
      } catch { /* ignore */ }
    } catch {
      message.error('加载勘查信息失败');
      setSurvey(null);
      setProject(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSaveAll = async () => {
    if (!survey) return;
    setSaving(true);
    try {
      await updateSurvey(survey.id, {
        surveyor: survey.surveyor,
        receptionist: survey.receptionist,
        receptionistPhone: survey.receptionistPhone,
        surveyDate: survey.surveyDate,
        startTime: survey.startTime,
        endTime: survey.endTime,
        propertyCertVerified: survey.propertyCertVerified,
        ownershipDispute: survey.ownershipDispute,
        remark: survey.remark,
        surveyStatus: survey.surveyStatus,
      } as any);
      message.success('保存成功');
      // Refresh list with same filter
      fetchSurveys({ size: 200 }).then((res) => {
        setSurveyList(res.content.filter(
          (item) => item.surveyStatus === '未查勘' && item.surveyor === currentUserName,
        ));
      }).catch(() => {});
    } catch {
      message.error('保存失败');
    } finally {
      setSaving(false);
    }
  };

  const handleUploadPhoto = async (file: File) => {
    if (!survey || !project) return;
    setUploading(true);
    setUploadProgress(0);
    const progressInterval = setInterval(() => {
      setUploadProgress(prev => Math.min(prev + 20, 90));
    }, 300);
    try {
      const record = await uploadFile(file, project.projectCode);
      clearInterval(progressInterval);
      setUploadProgress(100);
      const created = await createSurveyPhoto((survey as any).projectId || project.id, survey.id, {
        photoPath: record.filePath,
        photoCategory: newPhotoCategory,
        photoDescription: '',
      } as any);
      setSurvey({
        ...survey,
        photos: [...(survey.photos || []), created],
      });
      message.success('照片上传成功');
    } catch {
      message.error('上传失败');
    } finally {
      clearInterval(progressInterval);
      setUploading(false);
      setUploadProgress(0);
    }
  };

  const handleDeletePhoto = async (photoId: number) => {
    if (!survey) return;
    await deleteSurveyPhoto(photoId);
    setSurvey({
      ...survey,
      photos: (survey.photos || []).filter(p => p.id !== photoId),
    });
    message.success('已删除');
  };

  const imgSrc = (path: string) => path?.startsWith('http') ? path : `/uploads/${path}`;

  const thumbSrc = (path: string) =>
    path?.startsWith('http') ? path : `/api/image/thumbnail?path=${encodeURIComponent(path)}&maxWidth=200&maxHeight=200`;

  const inputStyle: React.CSSProperties = { fontSize: 16, height: 44 };
  const labelStyle: React.CSSProperties = { fontSize: 14, color: '#666', marginBottom: 4 };

  return (
    <div style={{ maxWidth: 480, margin: '0 auto', padding: 16, minHeight: '100vh', background: '#f5f5f5' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <Title level={4} style={{ margin: 0 }}>外勘录入</Title>
        {userInfo && (
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ fontSize: 13, color: '#666', display: 'flex', alignItems: 'center', gap: 4 }}>
              <UserOutlined />
              {userInfo.nickname || userInfo.username}
            </span>
            <Button
              type="text"
              size="small"
              icon={<LogoutOutlined />}
              onClick={logout}
              style={{ color: '#999' }}
            />
          </div>
        )}
      </div>

      {/* Survey Selector */}
      <Card size="small" style={{ marginBottom: 16 }}>
        <Select
          showSearch
          placeholder="选择勘查地址"
          size="large"
          style={{ width: '100%' }}
          loading={fetchingList}
          notFoundContent={fetchingList ? '加载中...' : '暂无未勘查记录'}
          filterOption={(input, option) =>
            (option?.label as string || '').toLowerCase().includes(input.toLowerCase())
          }
          options={surveyList.map((item) => ({
            label: item.projectAddress || item.projectCode,
            value: item.code,
          }))}
          onChange={(val) => handleSelectSurvey(val)}
        />
        <div style={{ fontSize: 12, color: '#999', marginTop: 8 }}>
          共 {surveyList.length} 条待勘查
        </div>
      </Card>

      {loading && <Spin style={{ display: 'block', textAlign: 'center', margin: 40 }} />}

      {survey && (
        <>
          {/* Project Info */}
          {project && (
            <Card size="small" style={{ marginBottom: 16 }} title="项目信息">
              <div style={labelStyle}>项目编号</div>
              <div style={{ fontSize: 16, marginBottom: 8 }}>{project.projectCode}</div>
              <div style={labelStyle}>项目名称</div>
              <div style={{ fontSize: 16, marginBottom: 8 }}>{project.projectName}</div>
              <div style={labelStyle}>地址</div>
              <div style={{ fontSize: 16 }}>{project.address || '-'}</div>
            </Card>
          )}

          {/* Survey Fields */}
          <Card size="small" style={{ marginBottom: 16 }} title="勘查信息">
            <div style={labelStyle}>勘查码</div>
            <Text strong style={{ fontSize: 20, display: 'block', marginBottom: 14, letterSpacing: 8 }}>
              {survey.code || survey.surveyCode}
            </Text>

            <div style={{ marginBottom: 12 }}>
              <span style={{ fontSize: 14, color: '#666', marginRight: 38 }}>勘查状态</span>
              <Switch
                checked={survey.surveyStatus === '已查勘'}
                onChange={(v) => setSurvey({ ...survey, surveyStatus: v ? '已查勘' : '未查勘' })}
                checkedChildren="已查勘"
                unCheckedChildren="未查勘"
              />
            </div>

            {[
              { label: '勘查人', field: 'surveyor' },
              { label: '接待人', field: 'receptionist' },
              { label: '接待人电话', field: 'receptionistPhone' },
            ].map(({ label, field }) => (
              <div key={field} style={{ marginBottom: 12 }}>
                <div style={labelStyle}>{label}</div>
                <Input
                  value={(survey as any)[field] || ''}
                  onChange={(e) => setSurvey({ ...survey, [field]: e.target.value })}
                  style={inputStyle}
                  placeholder={label}
                  disabled={field === 'surveyor'}
                />
              </div>
            ))}

            <Row gutter={12}>
              <Col span={12}>
                <div style={labelStyle}>勘查日期</div>
                <DatePicker
                  value={survey.surveyDate ? dayjs(survey.surveyDate) : null}
                  onChange={(_, dateStr) => setSurvey({ ...survey, surveyDate: dateStr as string })}
                  style={{ width: '100%', height: 44 }}
                />
              </Col>
              <Col span={12}>
                <div style={{ ...labelStyle, marginTop: 0 }}>
                  <Switch
                    checked={survey.propertyCertVerified}
                    onChange={(v) => setSurvey({ ...survey, propertyCertVerified: v })}
                    style={{ marginRight: 8 }}
                  />
                  验看房产证
                </div>
              </Col>
            </Row>

            <Row gutter={12} style={{ marginTop: 12 }}>
              <Col span={12}>
                <div style={labelStyle}>开始时间</div>
                <TimePicker
                  value={survey.startTime ? dayjs(survey.startTime, 'HH:mm') : null}
                  onChange={(_, timeStr) => setSurvey({ ...survey, startTime: timeStr as string })}
                  format="HH:mm"
                  style={{ width: '100%', height: 44 }}
                />
              </Col>
              <Col span={12}>
                <div style={labelStyle}>结束时间</div>
                <TimePicker
                  value={survey.endTime ? dayjs(survey.endTime, 'HH:mm') : null}
                  onChange={(_, timeStr) => setSurvey({ ...survey, endTime: timeStr as string })}
                  format="HH:mm"
                  style={{ width: '100%', height: 44 }}
                />
              </Col>
            </Row>

            <div style={{ marginTop: 12 }}>
              <div style={labelStyle}>权属争议</div>
              <TextArea
                value={survey.ownershipDispute || ''}
                onChange={(e) => setSurvey({ ...survey, ownershipDispute: e.target.value })}
                rows={2}
                style={{ fontSize: 16 }}
                placeholder="权属争议"
              />
            </div>

            <div style={{ marginTop: 12 }}>
              <div style={labelStyle}>备注</div>
              <TextArea
                value={survey.remark || ''}
                onChange={(e) => setSurvey({ ...survey, remark: e.target.value })}
                rows={2}
                style={{ fontSize: 16 }}
                placeholder="备注"
              />
            </div>
          </Card>

          {/* Save Button */}
          <Button
            type="primary"
            size="large"
            block
            loading={saving}
            onClick={handleSaveAll}
            style={{ marginBottom: 16, height: 48, fontSize: 16 }}
          >
            保存勘查信息
          </Button>

          {/* Photos */}
          <Card size="small" style={{ marginBottom: 16 }} title={`勘查照片 (${(survey.photos || []).length})`}>
            <div style={{ marginBottom: 12 }}>
              <Select
                value={newPhotoCategory}
                options={PHOTO_CATEGORY_OPTIONS}
                onChange={setNewPhotoCategory}
                style={{ width: '100%', marginBottom: 8 }}
              />
              <Upload
                accept="image/*"
                showUploadList={false}
                customRequest={({ file, onSuccess }) => {
                  handleUploadPhoto(file as File);
                  if (onSuccess) onSuccess({});
                }}
              >
                <Button icon={<UploadOutlined />} loading={uploading} block size="large">
                  选择照片上传
                </Button>
              </Upload>
              {uploading && (
                <Progress percent={uploadProgress} size="small" style={{ marginTop: 8 }} />
              )}
            </div>

            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              {(survey.photos || []).map(photo => (
                <div key={photo.id} style={{
                  width: 'calc(50% - 4px)', position: 'relative',
                  border: '1px solid #f0f0f0', borderRadius: 4, background: '#fafafa',
                }}>
                  <Image
                    src={thumbSrc(photo.photoPath)}
                    preview={{ src: imgSrc(photo.photoPath) }}
                    alt={photo.photoDescription}
                    width="100%" height={160}
                    style={{ objectFit: 'cover', borderRadius: '4px 4px 0 0' }}
                    placeholder={
                      <div style={{ width: '100%', height: 160, background: '#f0f0f0',
                        display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#ccc' }}>
                        加载中...
                      </div>
                    }
                  />
                  <div style={{ padding: 4, fontSize: 12, textAlign: 'center' }}>
                    {photo.photoDescription || '-'}
                  </div>
                  <div style={{ padding: '0 4px 6px', textAlign: 'center' }}>
                    <Tag style={{ maxWidth: '100%', margin: 0 }} color={photo.photoCategory ? 'blue' : 'default'}>
                      {PHOTO_CATEGORY_LABELS[photo.photoCategory] || '未分类'}
                    </Tag>
                  </div>
                  <Popconfirm title="确定删除?" onConfirm={() => handleDeletePhoto(photo.id)}>
                    <Button size="small" danger icon={<DeleteOutlined />}
                      style={{ position: 'absolute', top: 4, right: 4 }} />
                  </Popconfirm>
                </div>
              ))}
            </div>
          </Card>
        </>
      )}

      {!survey && !loading && (
        <div style={{ textAlign: 'center', color: '#999', marginTop: 40 }}>
          {fetchingList ? '加载中...' : '请从上方下拉列表中选择勘查地址'}
        </div>
      )}
    </div>
  );
}
