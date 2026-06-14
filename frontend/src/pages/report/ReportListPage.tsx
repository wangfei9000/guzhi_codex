import { useState, useEffect, useCallback } from 'react';
import {
  Table, Input, Button, Tag, Space, Result, Skeleton, Empty, message,
} from 'antd';
import { SearchOutlined, ReloadOutlined, FileTextOutlined, DownloadOutlined } from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import { fetchReports, generateReportPdf } from '@/api/report';
import type { ReportListRecord } from '@/api/types';

type PageStatus = 'loading' | 'error' | 'empty' | 'success';

export default function ReportListPage() {
  const [status, setStatus] = useState<PageStatus>('loading');
  const [data, setData] = useState<ReportListRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const [projectCode, setProjectCode] = useState('');
  const [address, setAddress] = useState('');
  const [inputCode, setInputCode] = useState('');
  const [inputAddress, setInputAddress] = useState('');

  const loadData = useCallback(async (code: string, addr: string, p: number, size: number) => {
    setStatus('loading');
    try {
      const res = await fetchReports({
        projectCode: code || undefined,
        address: addr || undefined,
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
    loadData(projectCode, address, page, pageSize);
  }, [projectCode, address, page, pageSize, loadData]);

  const handleSearch = () => {
    setProjectCode(inputCode);
    setAddress(inputAddress);
    setPage(1);
  };

  const [generatingId, setGeneratingId] = useState<number | null>(null);

  const handleReset = () => {
    setInputCode('');
    setInputAddress('');
    setProjectCode('');
    setAddress('');
    setPage(1);
  };

  const handleGeneratePdf = async (reportId: number) => {
    setGeneratingId(reportId);
    try {
      const { reportUrl, endTime } = await generateReportPdf(reportId);
      setData(prev => prev.map(record =>
        record.reportId === reportId ? { ...record, reportUrl, endTime: endTime || record.endTime } : record
      ));
      message.success('PDF报告生成成功');
      if (reportUrl) {
        window.open(reportUrl, '_blank');
      }
    } catch {
      message.error('PDF生成失败');
    } finally {
      setGeneratingId(null);
    }
  };

  const columns = [
    { title: '报告编号', dataIndex: 'reportCode', width: 180 },
    { title: '项目编号', dataIndex: 'projectCode', width: 180 },
    { title: '开始时间', dataIndex: 'startTime', width: 160 },
    { title: '结束时间', dataIndex: 'endTime', width: 160 },
    {
      title: '评估单价',
      dataIndex: 'unitPrice',
      width: 110,
      render: (v: number) => v ? `¥${v.toLocaleString()}` : '-',
    },
    { title: '抵押物地址', dataIndex: 'collateralAddress', width: 200, ellipsis: true },
    { title: '建筑面积', dataIndex: 'buildingArea', width: 100 },
    { title: '评价结果', dataIndex: 'valuationResult', width: 180, ellipsis: true },
    {
      title: '是否出报告',
      dataIndex: 'projectStatus',
      width: 110,
      render: (s: string) => {
        const done = s === '已出报告' || s === '已结款';
        return <Tag color={done ? 'green' : 'default'}>{done ? '是' : '否'}</Tag>;
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 240,
      fixed: 'right' as const,
      render: (_: unknown, record: ReportListRecord) => (
        <Space size="small">
          <Button
            type="primary"
            size="small"
            icon={<FileTextOutlined />}
            loading={generatingId === record.reportId}
            onClick={() => handleGeneratePdf(record.reportId)}
          >
            生成报告
          </Button>
          {record.reportUrl && (
            <Button
              size="small"
              icon={<DownloadOutlined />}
              onClick={() => window.open(record.reportUrl, '_blank')}
            >
              下载报告
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
          placeholder="项目编号"
          value={inputCode}
          onChange={(e) => setInputCode(e.target.value)}
          onPressEnter={handleSearch}
          allowClear
          style={{ width: 160 }}
        />
        <Input
          placeholder="地址"
          value={inputAddress}
          onChange={(e) => setInputAddress(e.target.value)}
          onPressEnter={handleSearch}
          allowClear
          style={{ width: 200 }}
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
            extra={<Button icon={<ReloadOutlined />} onClick={() => loadData(projectCode, address, page, pageSize)}>重试</Button>}
          />
        );
      case 'empty':
        return <>{searchBar}<Empty description="暂无报告数据" /></>;
      case 'success':
        return (
          <>
            {searchBar}
            <Table
              rowKey="reportId"
              columns={columns}
              dataSource={data}
              scroll={{ x: 1360 }}
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
    <PageContainer title="报告列表">
      {renderContent()}
    </PageContainer>
  );
}
