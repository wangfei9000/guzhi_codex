import { useState, useEffect, useCallback } from 'react';
import {
  Table, Input, Button, Tag, Space, Result, Skeleton, Empty, message, Upload,
} from 'antd';
import { SearchOutlined, ReloadOutlined, UploadOutlined, DownloadOutlined } from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import { fetchSeals, uploadSealedReport } from '@/api/seal';
import { uploadFile } from '@/api/file';
import type { SealListRecord } from '@/api/types';

type PageStatus = 'loading' | 'error' | 'empty' | 'success';

export default function SealListPage() {
  const [status, setStatus] = useState<PageStatus>('loading');
  const [data, setData] = useState<SealListRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [uploadingId, setUploadingId] = useState<number | null>(null);

  const [reportCode, setReportCode] = useState('');
  const [inputReportCode, setInputReportCode] = useState('');
  const [inputProjectCode, setInputProjectCode] = useState('');
  const [projectCode, setProjectCode] = useState('');

  const loadData = useCallback(async (rCode: string, pCode: string, p: number, size: number) => {
    setStatus('loading');
    try {
      const res = await fetchSeals({
        reportCode: rCode || undefined,
        projectCode: pCode || undefined,
        page: p - 1,
        size,
      });
      setData(res.content);
      setTotal(res.totalElements);
      setStatus(res.content.length === 0 ? 'empty' : 'success');
    } catch {
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    loadData(reportCode, projectCode, page, pageSize);
  }, [reportCode, projectCode, page, pageSize, loadData]);

  const handleSearch = () => {
    setReportCode(inputReportCode);
    setProjectCode(inputProjectCode);
    setPage(1);
  };

  const handleReset = () => {
    setInputReportCode('');
    setInputProjectCode('');
    setReportCode('');
    setProjectCode('');
    setPage(1);
  };

  const handleUpload = async (sealId: number, projectCodeVal: string, file: File) => {
    setUploadingId(sealId);
    try {
      const record = await uploadFile(file, projectCodeVal);
      await uploadSealedReport(sealId, record.filePath);
      message.success('盖章报告上传成功');
      loadData(reportCode, projectCode, page, pageSize);
    } catch {
      message.error('上传失败');
    } finally {
      setUploadingId(null);
    }
  };

  const columns = [
    { title: '报告编号', dataIndex: 'reportCode', width: 150 },
    { title: '项目编号', dataIndex: 'projectCode', width: 150 },
    {
      title: '盖章报告',
      dataIndex: 'sealedReportUrl',
      width: 120,
      render: (v: string) => v ? <Tag color="green">已上传</Tag> : <Tag color="default">未上传</Tag>,
    },
    { title: '盖章人', dataIndex: 'sealer', width: 100, render: (v: string) => v || '-' },
    { title: '盖章日期', dataIndex: 'sealDate', width: 120, render: (v: string) => v || '-' },
    {
      title: '项目状态',
      dataIndex: 'projectStatus',
      width: 100,
      render: (s: string) => {
        const done = s === '已出报告' || s === '已结款';
        return <Tag color={done ? 'green' : 'default'}>{done ? '是' : '否'}</Tag>;
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right' as const,
      render: (_: unknown, record: SealListRecord) => (
        <Space size="small">
          <Upload
            accept=".pdf"
            showUploadList={false}
            customRequest={({ file, onSuccess }) => {
              handleUpload(record.sealId, record.projectCode, file as File);
              if (onSuccess) onSuccess({});
            }}
          >
            <Button
              size="small"
              icon={<UploadOutlined />}
              loading={uploadingId === record.sealId}
            >
              上传盖章报告
            </Button>
          </Upload>
          {record.sealedReportUrl && (
            <Button
              size="small"
              icon={<DownloadOutlined />}
              onClick={() => window.open(`/uploads/${record.sealedReportUrl}`, '_blank')}
            >
              下载
            </Button>
          )}
        </Space>
      ),
    },
  ];

  const searchBar = (
    <div style={{ marginBottom: 16 }}>
      <Space wrap>
        <Input
          placeholder="报告编号"
          value={inputReportCode}
          onChange={(e) => setInputReportCode(e.target.value)}
          onPressEnter={handleSearch}
          allowClear
          style={{ width: 160 }}
        />
        <Input
          placeholder="项目编号"
          value={inputProjectCode}
          onChange={(e) => setInputProjectCode(e.target.value)}
          onPressEnter={handleSearch}
          allowClear
          style={{ width: 160 }}
        />
        <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
          搜索
        </Button>
        <Button onClick={handleReset}>重置</Button>
      </Space>
    </div>
  );

  const renderContent = () => {
    switch (status) {
      case 'loading':
        return <><Skeleton active paragraph={{ rows: 10 }} /></>;
      case 'error':
        return (
          <Result
            status="error"
            title="加载失败"
            extra={<Button icon={<ReloadOutlined />} onClick={() => loadData(reportCode, projectCode, page, pageSize)}>重试</Button>}
          />
        );
      case 'empty':
        return <>{searchBar}<Empty description="暂无盖章数据" /></>;
      case 'success':
        return (
          <>
            {searchBar}
            <Table
              rowKey="sealId"
              columns={columns}
              dataSource={data}
              scroll={{ x: 1100 }}
              pagination={{
                current: page,
                total,
                pageSize,
                showSizeChanger: true,
                showTotal: (t) => `共 ${t} 条`,
                onChange: (p, s) => { setPage(p); setPageSize(s); },
              }}
            />
          </>
        );
    }
  };

  return (
    <PageContainer title="盖章列表">
      {renderContent()}
    </PageContainer>
  );
}
