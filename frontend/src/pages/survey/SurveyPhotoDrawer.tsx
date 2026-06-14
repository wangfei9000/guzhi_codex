import { useState, useEffect, useCallback } from 'react';
import {
  Drawer, Button, Space, Popconfirm, message, Upload, Form, Input, Image, Empty, Spin, Select, Tag,
} from 'antd';
import { DeleteOutlined, SaveOutlined, UploadOutlined } from '@ant-design/icons';
import { uploadFile } from '@/api/file';
import { createSurveyPhoto, deleteSurveyPhoto } from '@/api/detail-api';
import type { SurveyPhotoRecord } from '@/api/types';
import { DEFAULT_PHOTO_CATEGORY, PHOTO_CATEGORY_LABELS, PHOTO_CATEGORY_OPTIONS } from '@/constants/reportFields';

interface Props {
  surveyId: number | null;
  projectId: number;
  projectCode: string;
  open: boolean;
  onClose: () => void;
}

export default function SurveyPhotoDrawer({ surveyId, projectId, projectCode, open, onClose }: Props) {
  const [photos, setPhotos] = useState<SurveyPhotoRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploadedPath, setUploadedPath] = useState('');

  const loadPhotos = useCallback(async () => {
    if (!surveyId) return;
    setLoading(true);
    try {
      const { default: client } = await import('@/api/client');
      const res = await client.get<{ data: SurveyPhotoRecord[] }>(`/survey-photo/by-survey/${surveyId}`);
      setPhotos(res.data.data || []);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  }, [surveyId]);

  useEffect(() => {
    if (open && surveyId) {
      loadPhotos();
      setUploadedPath('');
    } else {
      setPhotos([]);
    }
  }, [open, surveyId, loadPhotos]);

  const handleUpload = async (file: File) => {
    setUploading(true);
    try {
      const record = await uploadFile(file, projectCode);
      setUploadedPath(record.filePath);
      message.success('照片上传成功');
    } catch {
      message.error('上传失败');
    } finally {
      setUploading(false);
    }
  };

  const handleSavePhoto = async (values: { photoDescription?: string; photoCategory?: string }) => {
    if (!surveyId || !uploadedPath) {
      message.error('请先上传照片');
      return;
    }
    try {
      const created = await createSurveyPhoto(projectId, surveyId!, {
        photoPath: uploadedPath,
        photoCategory: values.photoCategory || DEFAULT_PHOTO_CATEGORY,
        photoDescription: values.photoDescription || '',
      } as any);
      setPhotos(prev => [...prev, created]);
      setUploadedPath('');
      message.success('照片已保存');
    } catch {
      message.error('保存失败');
    }
  };

  const handleDeletePhoto = async (photoId: number) => {
    await deleteSurveyPhoto(photoId);
    setPhotos(prev => prev.filter(p => p.id !== photoId));
    message.success('照片已删除');
  };

  const imgSrc = (path: string) => path?.startsWith('http') ? path : `/uploads/${path}`;

  return (
    <Drawer title="勘查照片" open={open} onClose={onClose} width={600} destroyOnClose>
      {loading ? <Spin /> : (
        <>
          {/* Upload new photo */}
          <div style={{ marginBottom: 16, padding: 12, background: '#f6ffed', borderRadius: 8 }}>
            <Space direction="vertical" style={{ width: '100%' }}>
              <Upload
                accept="image/*"
                showUploadList={false}
                customRequest={({ file, onSuccess }) => {
                  handleUpload(file as File);
                  if (onSuccess) onSuccess({});
                }}
              >
                <Button icon={<UploadOutlined />} loading={uploading}>
                  选择照片上传
                </Button>
              </Upload>
              {uploadedPath && (
                <div>
                  <Image src={imgSrc(uploadedPath)} width={200} height={140} style={{ objectFit: 'cover', borderRadius: 4 }} />
                  <Form
                    onFinish={handleSavePhoto}
                    layout="inline"
                    initialValues={{ photoCategory: DEFAULT_PHOTO_CATEGORY }}
                    style={{ marginTop: 8 }}
                  >
                    <Form.Item name="photoCategory" rules={[{ required: true, message: '请选择照片分类' }]}>
                      <Select options={PHOTO_CATEGORY_OPTIONS} style={{ width: 150 }} />
                    </Form.Item>
                    <Form.Item name="photoDescription">
                      <Input placeholder="照片描述" style={{ width: 200 }} />
                    </Form.Item>
                    <Form.Item>
                      <Space>
                        <Button type="primary" size="small" icon={<SaveOutlined />} htmlType="submit">保存</Button>
                        <Button size="small" onClick={() => setUploadedPath('')}>取消</Button>
                      </Space>
                    </Form.Item>
                  </Form>
                </div>
              )}
            </Space>
          </div>

          {/* Existing photos */}
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
            {photos.map(photo => (
              <div key={photo.id} style={{
                width: 180, height: 218, textAlign: 'center', position: 'relative',
                border: '1px solid #f0f0f0', borderRadius: 4, background: '#fafafa', padding: 4,
              }}>
                <Image src={imgSrc(photo.photoPath)} alt={photo.photoDescription}
                  width={172} height={124} style={{ objectFit: 'cover', borderRadius: 2 }} />
                <div style={{ fontSize: 11, color: '#666', padding: '2px 4px', height: 28, overflow: 'hidden' }}>
                  {photo.photoDescription || '-'}
                </div>
                <Tag style={{ maxWidth: 160, margin: 0 }} color={photo.photoCategory ? 'blue' : 'default'}>
                  {PHOTO_CATEGORY_LABELS[photo.photoCategory] || '未分类'}
                </Tag>
                <Popconfirm title="确定删除?" onConfirm={() => handleDeletePhoto(photo.id)}>
                  <Button size="small" danger icon={<DeleteOutlined />}
                    style={{ position: 'absolute', top: 4, right: 4, opacity: 0.85 }} />
                </Popconfirm>
              </div>
            ))}
            {photos.length === 0 && !uploadedPath && <Empty description="暂无照片" image={Empty.PRESENTED_IMAGE_SIMPLE} />}
          </div>
        </>
      )}
    </Drawer>
  );
}
