import { useState, useEffect, useCallback } from 'react';
import {
  Upload, Button, Table, Space, Popconfirm, message, Empty, Result, Skeleton, Card,
} from 'antd';
import { DeleteOutlined, ReloadOutlined, InboxOutlined, LinkOutlined } from '@ant-design/icons';
import type { UploadFile } from 'antd';
import { uploadFile, fetchFiles, deleteFile } from '@/api/file';
import type { FileRecordItem } from '@/api/types';
import PageContainer from '@/components/common/PageContainer';

const { Dragger } = Upload;

type StatusType = 'loading' | 'error' | 'empty' | 'success';

export default function FileUploadPage() {
  const [status, setStatus] = useState<StatusType>('loading');
  const [files, setFiles] = useState<FileRecordItem[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [uploading, setUploading] = useState(false);

  const loadFiles = useCallback(async () => {
    setStatus('loading');
    try {
      const res = await fetchFiles({ page: page - 1, size: 10 });
      setFiles(res.content);
      setTotal(res.totalElements);
      setStatus(res.content.length === 0 ? 'empty' : 'success');
    } catch {
      setStatus('error');
    }
  }, [page]);

  useEffect(() => {
    loadFiles();
  }, [loadFiles]);

  const handleUpload = async (file: UploadFile) => {
    setUploading(true);
    try {
      if (file instanceof File) {
        await uploadFile(file);
      }
      message.success('上传成功');
      loadFiles();
    } catch {
      message.error('上传失败');
    } finally {
      setUploading(false);
    }
    return false;
  };

  const handleDelete = async (id: number) => {
    await deleteFile(id);
    message.success('删除成功');
    loadFiles();
  };

  const handleCopyLink = async (filePath: string) => {
    const url = `${window.location.origin}/uploads/${filePath}`;
    try {
      await navigator.clipboard.writeText(url);
      message.success('链接已复制');
    } catch {
      const input = document.createElement('input');
      input.value = url;
      document.body.appendChild(input);
      input.select();
      document.execCommand('copy');
      document.body.removeChild(input);
      message.success('链接已复制');
    }
  };

  const formatSize = (bytes: number) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '文件名', dataIndex: 'originalName', width: 240 },
    { title: '类型', dataIndex: 'contentType', width: 120 },
    {
      title: '大小',
      dataIndex: 'fileSize',
      width: 100,
      render: (s: number) => formatSize(s),
    },
    {
      title: '上传时间',
      dataIndex: 'createdAt',
      width: 180,
      render: (t: string) => t?.substring(0, 19).replace('T', ' '),
    },
    {
      title: '操作',
      key: 'action',
      width: 140,
      render: (_: unknown, record: FileRecordItem) => (
        <Space>
          <Button title="复制链接" type="link" size="small" icon={<LinkOutlined />} onClick={() => handleCopyLink(record.filePath)} />

          <Popconfirm title="确定删除该文件?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}  />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const renderContent = () => {
    return (
      <>
        <Card style={{ marginBottom: 16 }}>
          <Dragger
            name="file"
            multiple
            showUploadList={false}
            beforeUpload={handleUpload as any}
            disabled={uploading}
          >
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p className="ant-upload-text">点击或拖拽文件到此区域上传</p>
            <p className="ant-upload-hint">支持图片、PDF、Word、TXT 等文件，最大 10MB</p>
          </Dragger>
        </Card>

        {status === 'loading' && <Skeleton active paragraph={{ rows: 6 }} />}
        {status === 'error' && (
          <Result
            status="error"
            title="加载失败"
            extra={<Button icon={<ReloadOutlined />} onClick={loadFiles}>重试</Button>}
          />
        )}
        {status === 'empty' && <Empty description="暂无文件" />}
        {status === 'success' && (
          <Table
            rowKey="id"
            columns={columns}
            dataSource={files}
            pagination={{
              current: page,
              total,
              pageSize: 10,
              onChange: (p) => setPage(p),
              showTotal: (t) => `共 ${t} 个文件`,
            }}
          />
        )}
      </>
    );
  };

  return (
    <PageContainer title="文件管理">
      {renderContent()}
    </PageContainer>
  );
}
