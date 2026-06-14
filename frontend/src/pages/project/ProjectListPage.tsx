import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Table, Input, Select, Button, Tag, Space, Result, Skeleton, Empty,
} from 'antd';
import { CalculatorOutlined, SearchOutlined, ReloadOutlined, EyeOutlined, PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import { fetchProjectClientNames, fetchProjects } from '@/api/project';
import type { ProjectRecord } from '@/api/types';
import ProjectDetailDrawer from './ProjectDetailDrawer';

const STATUS_MAP: Record<string, { color: string; label: string }> = {
  '未评估': { color: 'default', label: '未评估' },
  '已评估': { color: 'blue', label: '已评估' },
  '已出报告': { color: 'orange', label: '已出报告' },
  '已结款': { color: 'green', label: '已结款' },
};

type PageStatus = 'loading' | 'error' | 'empty' | 'success';

interface ProjectListPageProps {
  showActions?: boolean;
  title?: string;
  returnPath?: string;
}

export default function ProjectListPage({ showActions = true, title = '项目列表', returnPath = '/project/list' }: ProjectListPageProps = {}) {
  const navigate = useNavigate();
  const [status, setStatus] = useState<PageStatus>('loading');
  const [data, setData] = useState<ProjectRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  const [projectCode, setProjectCode] = useState('');
  const [clientName, setClientName] = useState('');
  const [address, setAddress] = useState('');
  const [statusFilter, setStatusFilter] = useState<string | undefined>(undefined);

  // Hold input state separate from query state for on-demand search
  const [inputCode, setInputCode] = useState('');
  const [inputClientName, setInputClientName] = useState('');
  const [inputAddress, setInputAddress] = useState('');
  const [clientNameOptions, setClientNameOptions] = useState<string[]>([]);
  const [reloadKey, setReloadKey] = useState(0);

  const loadData = useCallback(async (
    code: string, client: string, addr: string, s: string | undefined,
    p: number, size: number,
  ) => {
    setStatus('loading');
    try {
      const res = await fetchProjects({
        projectCode: code || undefined,
        clientName: client || undefined,
        address: addr || undefined,
        status: s,
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
    loadData(projectCode, clientName, address, statusFilter, page, pageSize);
  }, [projectCode, clientName, address, statusFilter, page, pageSize, reloadKey, loadData]);

  useEffect(() => {
    fetchProjectClientNames()
      .then(setClientNameOptions)
      .catch(() => setClientNameOptions([]));
  }, []);

  const handleSearch = () => {
    setProjectCode(inputCode);
    setClientName(inputClientName);
    setAddress(inputAddress);
    setPage(1);
  };

  const [detailProjectId, setDetailProjectId] = useState<number | null>(null);
  const [drawerOpen, setDrawerOpen] = useState(false);

  const handleShowDetail = (id: number) => {
    setDetailProjectId(id);
    setDrawerOpen(true);
  };

  const handleCloseDrawer = () => {
    setDrawerOpen(false);
    setReloadKey(k => k + 1);
  };

  const handleReset = () => {
    setInputCode('');
    setInputClientName('');
    setInputAddress('');
    setStatusFilter(undefined);
    setProjectCode('');
    setClientName('');
    setAddress('');
    setPage(1);
  };

  const columns = [
    { title: '项目编号', dataIndex: 'projectCode', width: 180 },
    { title: '项目名称', dataIndex: 'projectName', width: 180, ellipsis: true },
    { title: '城市', dataIndex: 'city', width: 80 },
    { title: '行政区', dataIndex: 'district', width: 100 },
    { title: '片区', dataIndex: 'area', width: 150 },
    { title: '地址', dataIndex: 'address', width: 220, ellipsis: true },
    { title: '委托单位', dataIndex: 'clientName', width: 160, ellipsis: true },
    { title: '估价时点', dataIndex: 'valuationTime', width: 120 },
    {
      title: '期望价格',
      dataIndex: 'expectedPrice',
      width: 120,
      render: (v: number) => (v ? `¥${v.toLocaleString()}` : '-'),
    },
    {
      title: '估值单价',
      dataIndex: 'valuationUnitPrice',
      width: 110,
      render: (v: number) => (v ? `¥${v.toLocaleString()}` : '-'),
    },
    {
      title: '估值总价',
      dataIndex: 'valuationTotalPrice',
      width: 120,
      render: (v: number) => (v ? `¥${v.toLocaleString()}` : '-'),
    },
    {
      title: '估值类型',
      dataIndex: 'valuationType',
      width: 110,
      render: (v?: string) => <Tag color={v === '自动估值' ? 'blue' : v === '人工估值' ? 'orange' : 'default'}>{v || '-'}</Tag>,
    },
    {
      title: '建筑面积',
      dataIndex: 'buildingArea',
      width: 100,
      render: (v: number) => (v ? `${v.toLocaleString()}㎡` : '-'),
    },

    { title: '登记人', dataIndex: 'registrar', width: 90 },
    { title: '登记日期', dataIndex: 'registrationDate', width: 120 },
    { title: '委托方联系人', dataIndex: 'clientContact', width: 120 },
    { title: '委托方电话', dataIndex: 'clientPhone', width: 130 },
    { title: '估价目的', dataIndex: 'valuationPurpose', width: 100 ,ellipsis: true},
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (s: string) => {
        const st = STATUS_MAP[s] || { color: 'default', label: s };
        return <Tag color={st.color}>{st.label}</Tag>;
      },
    },
    { title: '备注', dataIndex: 'remark', width: 160, ellipsis: true },
    ...(showActions
      ? [{
          title: '操作',
          key: 'action',
          width: 150,
          fixed: 'right' as const,
          render: (_: unknown, record: ProjectRecord) => (
            <Space size="small">
              <Button
                type="link"
                size="small"
                icon={<EyeOutlined />}
                onClick={() => handleShowDetail(record.id)}
              >
                详情
              </Button>
              <Button
                type="link"
                size="small"
                icon={<CalculatorOutlined />}
                onClick={() => navigate(`/project/${record.id}/valuation`)}
              >
                估值
              </Button>
            </Space>
          ),
        }]
      : []),
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
          style={{ width: 140 }}
        />
        <Select
          placeholder="委托单位"
          value={inputClientName || undefined}
          onChange={(v) => setInputClientName(v || '')}
          allowClear
          showSearch
          optionFilterProp="label"
          options={clientNameOptions.map(name => ({ label: name, value: name }))}
          style={{ width: 160 }}
        />
        <Input
          placeholder="地址"
          value={inputAddress}
          onChange={(e) => setInputAddress(e.target.value)}
          onPressEnter={handleSearch}
          allowClear
          style={{ width: 180 }}
        />
        <Select
          placeholder="状态"
          value={statusFilter}
          onChange={(v) => { setStatusFilter(v); setPage(1); }}
          allowClear
          style={{ width: 120 }}
        >
          <Select.Option value="未评估">未评估</Select.Option>
          <Select.Option value="已评估">已评估</Select.Option>
          <Select.Option value="已出报告">已出报告</Select.Option>
          <Select.Option value="已结款">已结款</Select.Option>
        </Select>
        <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
          搜索
        </Button>
        <Button onClick={handleReset}>重置</Button>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate(`/project/create?returnTo=${encodeURIComponent(returnPath)}`)}>
          新增项目
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
                <Button icon={<ReloadOutlined />} onClick={() => loadData(projectCode, clientName, address, statusFilter, page, pageSize)}>
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
            <Empty description="暂无项目数据" />
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
              scroll={{ x: 2000 }}
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
    <PageContainer title={title}>
      {renderContent()}
      {showActions && (
        <ProjectDetailDrawer
          projectId={detailProjectId}
          open={drawerOpen}
          onClose={handleCloseDrawer}
        />
      )}
    </PageContainer>
  );
}
