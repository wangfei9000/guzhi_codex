import { useState, useEffect, useCallback } from 'react';
import { Table, Input, Tag, Button, Result, Skeleton, Empty, Space } from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import { fetchSchedules } from '@/api/schedule';
import type { ScheduleRecord } from '@/api/types';

const STATUS_MAP: Record<string, { color: string; label: string }> = {
  pending: { color: 'default', label: '待处理' },
  accepted: { color: 'blue', label: '已接单' },
  surveying: { color: 'orange', label: '查勘中' },
  appraising: { color: 'purple', label: '估价中' },
  completed: { color: 'green', label: '已完成' },
  cancelled: { color: 'red', label: '已取消' },
};

type PageStatus = 'loading' | 'error' | 'empty' | 'success';

export default function ScheduleListPage() {
  const [status, setStatus] = useState<PageStatus>('loading');
  const [data, setData] = useState<ScheduleRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [inputValue, setInputValue] = useState('');

  const loadData = useCallback(async (searchKeyword: string, p: number, size: number) => {
    setStatus('loading');
    try {
      const res = await fetchSchedules({
        keyword: searchKeyword || undefined,
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
    loadData(keyword, page, pageSize);
  }, [keyword, page, pageSize, loadData]);

  const handleSearch = () => {
    setKeyword(inputValue);
    setPage(1);
  };

  const columns = [
    { title: '登记日期', dataIndex: 'registrationDate', width: 110 },
    { title: '编号', dataIndex: 'code', width: 110 },
    { title: '报告编号', dataIndex: 'reportNo', width: 140 },
    { title: '接单人', dataIndex: 'orderTaker', width: 80 },
    { title: '中介机构', dataIndex: 'agency', width: 180 },
    { title: '报单人', dataIndex: 'reporter', width: 80 },
    { title: '报单人电话', dataIndex: 'reporterPhone', width: 130 },
    { title: '联系人', dataIndex: 'contact', width: 80 },
    { title: '联系人电话', dataIndex: 'contactPhone', width: 130 },
    { title: '客服', dataIndex: 'customerService', width: 80 },
    { title: '项目地址', dataIndex: 'projectAddress', width: 220, ellipsis: true },
    { title: '查勘员', dataIndex: 'surveyor', width: 80 },
    { title: '估价师', dataIndex: 'appraiser', width: 80 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 90,
      render: (s: string) => {
        const st = STATUS_MAP[s] || { color: 'default', label: s };
        return <Tag color={st.color}>{st.label}</Tag>;
      },
    },
    {
      title: '单价',
      dataIndex: 'unitPrice',
      width: 100,
      render: (v: number) => `¥${v.toLocaleString()}`,
    },
    {
      title: '总价',
      dataIndex: 'totalPrice',
      width: 120,
      render: (v: number) => (
        <span style={{ fontWeight: 600, color: '#1677ff' }}>¥{v.toLocaleString()}</span>
      ),
    },
  ];

  const searchBar = (
    <div style={{ marginBottom: 16 }}>
      <Space>
        <Input
          prefix={<SearchOutlined style={{ color: '#bfbfbf' }} />}
          placeholder="搜索报告编号或项目地址"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onPressEnter={handleSearch}
          allowClear
          style={{ width: 360 }}
        />
        <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
          搜索
        </Button>
      </Space>
    </div>
  );

  const renderContent = () => {
    switch (status) {
      case 'loading':
        return (
          <>
            {searchBar}
            <Skeleton active paragraph={{ rows: 10 }} />
          </>
        );
      case 'error':
        return (
          <>
            {searchBar}
            <Result
              status="error"
              title="加载失败"
              extra={
                <Button icon={<ReloadOutlined />} onClick={() => loadData(keyword, page, pageSize)}>
                  重试
                </Button>
              }
            />
          </>
        );
      case 'empty':
        return (
          <>
            {searchBar}
            <Empty description="暂无调度数据" />
          </>
        );
      case 'success':
        return (
          <>
            {searchBar}
            <Table
              rowKey="id"
              columns={columns}
              dataSource={data}
              scroll={{ x: 2200 }}
              pagination={{
                current: page,
                total,
                pageSize,
                showSizeChanger: true,
                showTotal: (t) => `共 ${t} 条`,
                onChange: (p, s) => {
                  setPage(p);
                  setPageSize(s);
                },
              }}
            />
          </>
        );
    }
  };

  return (
    <PageContainer title="调度列表">
      {renderContent()}
    </PageContainer>
  );
}
