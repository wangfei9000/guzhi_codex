import { useCallback, useEffect, useState } from 'react';
import { Button, Card, Empty, message, Result, Space, Table, Tag, Tooltip, Typography } from 'antd';
import { DownloadOutlined, ReloadOutlined } from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import { fetchRevaluations } from '@/api/revaluation';
import type { RevaluationRecord } from '@/api/revaluation';

const { Text } = Typography;

function buildDownloadUrl(fileUrl: string) {
  if (fileUrl.startsWith('http') || fileUrl.startsWith('/uploads/')) {
    return fileUrl;
  }
  return `/uploads/${fileUrl}`;
}

function downloadFile(fileUrl?: string) {
  if (!fileUrl) {
    message.warning('暂无可下载文件');
    return;
  }

  const link = document.createElement('a');
  link.href = buildDownloadUrl(fileUrl);
  link.download = fileUrl.split('/').pop() || '复估结果.csv';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

export default function RevaluationListPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const [data, setData] = useState<RevaluationRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [refreshKey, setRefreshKey] = useState(0);

  const loadData = useCallback(async (nextPage: number, nextPageSize: number) => {
    setLoading(true);
    setError(false);
    try {
      const res = await fetchRevaluations({
        page: nextPage - 1,
        size: nextPageSize,
        sort: 'createdAt,desc',
      });
      setData(res.content);
      setTotal(res.totalElements);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData(page, pageSize);
  }, [loadData, page, pageSize, refreshKey]);

  const handleRefresh = () => {
    setPage(1);
    setRefreshKey((value) => value + 1);
  };

  const columns = [
    { title: '复估id', dataIndex: 'id', width: 100 },
    { title: '复估日期', dataIndex: 'revaluationDate', width: 140, render: (value?: string) => value || '-' },
    {
      title: '复估结果',
      dataIndex: 'result',
      width: 130,
      render: (value?: string) => (
        <Tag color={value === '已完成' ? 'green' : value === '进行中' ? 'processing' : 'default'}>
          {value || '-'}
        </Tag>
      ),
    },
    {
      title: '文件URL',
      dataIndex: 'fileUrl',
      width: 320,
      ellipsis: true,
      render: (value?: string) => value ? (
        <Tooltip title={value}>
          <Text ellipsis>{value}</Text>
        </Tooltip>
      ) : '-',
    },
    {
      title: '备注',
      dataIndex: 'remark',
      width: 260,
      ellipsis: true,
      render: (value?: string) => value ? (
        <Tooltip title={value}>
          <Text ellipsis>{value}</Text>
        </Tooltip>
      ) : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right' as const,
      render: (_: unknown, record: RevaluationRecord) => (
        <Button
          size="small"
          icon={<DownloadOutlined />}
          disabled={!record.fileUrl}
          onClick={() => downloadFile(record.fileUrl)}
        >
          下载
        </Button>
      ),
    },
  ];

  if (error) {
    return (
      <PageContainer title="复估列表">
        <Result
          status="error"
          title="加载失败"
          extra={<Button icon={<ReloadOutlined />} onClick={handleRefresh}>刷新</Button>}
        />
      </PageContainer>
    );
  }

  return (
    <PageContainer title="复估列表">
      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Button icon={<ReloadOutlined />} loading={loading} onClick={handleRefresh}>
            刷新
          </Button>
        </Space>
        <Table
          rowKey="id"
          columns={columns}
          dataSource={data}
          loading={loading}
          locale={{ emptyText: <Empty description="暂无复估记录" /> }}
          scroll={{ x: 980 }}
          pagination={{
            current: page,
            total,
            pageSize,
            showSizeChanger: true,
            showTotal: (count) => `共 ${count} 条`,
            onChange: (nextPage, nextPageSize) => {
              setPage(nextPage);
              setPageSize(nextPageSize);
            },
          }}
        />
      </Card>
    </PageContainer>
  );
}
