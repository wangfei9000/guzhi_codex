import { useCallback, useEffect, useState } from 'react';
import { Button, Card, DatePicker, Empty, message, Result, Space, Table, Tag, Tooltip, Typography } from 'antd';
import { AuditOutlined, DownloadOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import type { Dayjs } from 'dayjs';
import PageContainer from '@/components/common/PageContainer';
import { fetchReconciliations, startReconciliation } from '@/api/reconciliation';
import type { ReconciliationRecord } from '@/api/reconciliation';

const { RangePicker } = DatePicker;
const { Text } = Typography;

type DateRange = [Dayjs, Dayjs] | null;

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
  link.download = fileUrl.split('/').pop() || '对账结果.csv';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

function formatRange(range: DateRange) {
  return {
    startTime: range?.[0]?.format('YYYY-MM-DD'),
    endTime: range?.[1]?.format('YYYY-MM-DD'),
  };
}

export default function ReconciliationPage() {
  const [loading, setLoading] = useState(false);
  const [reconciling, setReconciling] = useState(false);
  const [error, setError] = useState(false);
  const [data, setData] = useState<ReconciliationRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [dateRange, setDateRange] = useState<DateRange>(null);
  const [query, setQuery] = useState<{ startTime?: string; endTime?: string }>({});
  const [refreshKey, setRefreshKey] = useState(0);

  const loadData = useCallback(async (nextPage: number, nextPageSize: number) => {
    setLoading(true);
    setError(false);
    try {
      const res = await fetchReconciliations({
        ...query,
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
  }, [query]);

  useEffect(() => {
    loadData(page, pageSize);
  }, [loadData, page, pageSize, refreshKey]);

  const handleSearch = () => {
    setPage(1);
    setQuery(formatRange(dateRange));
  };

  const handleReset = () => {
    setDateRange(null);
    setPage(1);
    setQuery({});
  };

  const handleRefresh = () => {
    setPage(1);
    setRefreshKey((value) => value + 1);
  };

  const handleReconcile = async () => {
    if (!dateRange?.[0] || !dateRange?.[1]) {
      message.warning('请选择开始时间和结束时间');
      return;
    }
    if (dateRange[0].isAfter(dateRange[1], 'day')) {
      message.warning('开始时间不能晚于结束时间');
      return;
    }

    const nextQuery = formatRange(dateRange);
    setReconciling(true);
    try {
      await startReconciliation({
        startTime: nextQuery.startTime!,
        endTime: nextQuery.endTime!,
      });
      message.success('对账成功，后台正在生成CSV');
      setPage(1);
      setQuery(nextQuery);
      setRefreshKey((value) => value + 1);
    } catch {
      message.error('对账失败');
    } finally {
      setReconciling(false);
    }
  };

  const columns = [
    { title: '对账id', dataIndex: 'id', width: 100 },
    { title: '开始时间', dataIndex: 'startTime', width: 130, render: (value?: string) => value || '-' },
    { title: '结束时间', dataIndex: 'endTime', width: 130, render: (value?: string) => value || '-' },
    { title: '对账日期', dataIndex: 'reconciliationDate', width: 130, render: (value?: string) => value || '-' },
    {
      title: '对账结果',
      dataIndex: 'result',
      width: 130,
      render: (value?: string) => (
        <Tag color={value === '已完成' ? 'green' : value === '进行中' ? 'processing' : 'default'}>
          {value || '-'}
        </Tag>
      ),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      width: 240,
      ellipsis: true,
      render: (value?: string) => value ? (
        <Tooltip title={value}>
          <Text ellipsis>{value}</Text>
        </Tooltip>
      ) : '-',
    },
    {
      title: '文件URL',
      dataIndex: 'fileUrl',
      width: 320,
      ellipsis: true,
      render: (value?: string) => value ? (
        <Tooltip title={value}>
          <Button type="link" size="small" icon={<DownloadOutlined />} onClick={() => downloadFile(value)}>
            {value}
          </Button>
        </Tooltip>
      ) : '-',
    },
  ];

  const filterBar = (
    <Space wrap style={{ marginBottom: 16 }}>
      <RangePicker
        value={dateRange}
        onChange={(dates) => setDateRange(dates as DateRange)}
        allowClear
        style={{ width: 260 }}
        placeholder={['开始时间', '结束时间']}
      />
      <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
        查询
      </Button>
      <Button onClick={handleReset}>重置</Button>
      <Button icon={<ReloadOutlined />} loading={loading} onClick={handleRefresh}>
        刷新
      </Button>
      <Button type="primary" icon={<AuditOutlined />} loading={reconciling} onClick={handleReconcile}>
        创建对账
      </Button>
    </Space>
  );

  if (error) {
    return (
      <PageContainer title="对账">
        <Card>
          {filterBar}
          <Result
            status="error"
            title="加载失败"
            extra={<Button icon={<ReloadOutlined />} onClick={handleRefresh}>重试</Button>}
          />
        </Card>
      </PageContainer>
    );
  }

  return (
    <PageContainer title="对账">
      <Card>
        {filterBar}
        <Table
          rowKey="id"
          columns={columns}
          dataSource={data}
          loading={loading}
          locale={{ emptyText: <Empty description="暂无对账记录" /> }}
          scroll={{ x: 1120 }}
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
