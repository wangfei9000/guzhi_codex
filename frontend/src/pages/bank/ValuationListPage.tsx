import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Card, DatePicker, Empty, Result, Select, Space, Table, Tag } from 'antd';
import { EyeOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import PageContainer from '@/components/common/PageContainer';
import { fetchBankValuations } from '@/api/project';
import type { ProjectRecord } from '@/api/types';

function formatMoney(value?: number) {
  return value ? `¥${value.toLocaleString()}` : '-';
}

export default function ValuationListPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const [data, setData] = useState<ProjectRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [valuationType, setValuationType] = useState<string | undefined>();
  const [status, setStatus] = useState<string | undefined>();
  const [valuationTime, setValuationTime] = useState<dayjs.Dayjs | null>(null);
  const [query, setQuery] = useState<{
    valuationType?: string;
    status?: string;
    valuationTime?: string;
  }>({});

  const loadData = useCallback(async (nextPage = page, nextPageSize = pageSize) => {
    setLoading(true);
    setError(false);
    try {
      const res = await fetchBankValuations({
        ...query,
        page: nextPage - 1,
        size: nextPageSize,
        sort: 'id,desc',
      });
      setData(res.content);
      setTotal(res.totalElements);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, query]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const columns = [
    { title: '城市', dataIndex: 'city', width: 90 },
    { title: '行政区', dataIndex: 'district', width: 110 },
    { title: '片区', dataIndex: 'area', width: 130 },
    { title: '地址', dataIndex: 'address', width: 240, ellipsis: true },
    { title: '面积', dataIndex: 'buildingArea', width: 100, render: (value?: number) => value ? `${value}㎡` : '-' },
    {
      title: '估值类型',
      dataIndex: 'valuationType',
      width: 120,
      render: (value?: string) => (
        <Tag color={value === '自动估值' ? 'blue' : value === '人工估值' ? 'orange' : 'default'}>
          {value || '-'}
        </Tag>
      ),
    },
    { title: '估价时点', dataIndex: 'valuationTime', width: 120 },
    { title: '单价', dataIndex: 'valuationUnitPrice', width: 120, render: formatMoney },
    { title: '总价', dataIndex: 'valuationTotalPrice', width: 130, render: formatMoney },

    {
      title: '状态',
      dataIndex: 'status',
      width: 110,
      render: (value?: string) => <Tag color={value === '已评估' ? 'green' : 'default'}>{value || '-'}</Tag>,
    },
    { title: '备注', dataIndex: 'remark', width: 180, ellipsis: true },
    {
      title: '详情',
      key: 'detail',
      width: 100,
      fixed: 'right' as const,
      render: (_: unknown, record: ProjectRecord) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => navigate(`/bank/valuation-detail/${record.id}`)}
        >
          详情
        </Button>
      ),
    },
  ];

  const handleSearch = () => {
    setPage(1);
    setQuery({
      valuationType,
      status,
      valuationTime: valuationTime ? valuationTime.format('YYYY-MM-DD') : undefined,
    });
  };

  const handleReset = () => {
    setValuationType(undefined);
    setStatus(undefined);
    setValuationTime(null);
    setPage(1);
    setQuery({});
  };

  const filterBar = (
    <Space wrap style={{ marginBottom: 16 }}>
      <Select
        placeholder="估值类型"
        value={valuationType}
        onChange={setValuationType}
        allowClear
        style={{ width: 140 }}
        options={[
          { label: '自动估值', value: '自动估值' },
          { label: '人工估值', value: '人工估值' },
        ]}
      />
      <Select
        placeholder="状态"
        value={status}
        onChange={setStatus}
        allowClear
        style={{ width: 140 }}
        options={[
          { label: '未评估', value: '未评估' },
          { label: '已评估', value: '已评估' },
          { label: '已出报告', value: '已出报告' },
          { label: '已结款', value: '已结款' },
        ]}
      />
      <DatePicker
        placeholder="估价时点"
        value={valuationTime}
        onChange={setValuationTime}
        style={{ width: 160 }}
      />
      <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
        搜索
      </Button>
      <Button onClick={handleReset}>重置</Button>
    </Space>
  );

  if (error) {
    return (
      <PageContainer title="估值列表">
        {filterBar}
        <Result
          status="error"
          title="加载失败"
          extra={<Button icon={<ReloadOutlined />} onClick={() => loadData(page, pageSize)}>重试</Button>}
        />
      </PageContainer>
    );
  }

  return (
    <PageContainer title="估值列表">
      <Card>
        {filterBar}
        <Table
          rowKey="id"
          columns={columns}
          dataSource={data}
          loading={loading}
          locale={{ emptyText: <Empty description="暂无估值记录" /> }}
          scroll={{ x: 1550 }}
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
