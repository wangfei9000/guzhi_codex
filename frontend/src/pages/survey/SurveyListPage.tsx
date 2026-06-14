import { useState, useEffect, useCallback, useRef } from 'react';
import {
  Table, Input, Button, Tag, Space, Result, Skeleton, Empty, message, Select,
} from 'antd';
import { SearchOutlined, ReloadOutlined, CameraOutlined } from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import { fetchSurveys } from '@/api/survey-list';
import { updateSurvey } from '@/api/detail-api';
import { fetchUsersByRole, type UserOption } from '@/api/user';
import type { SurveyListRecord } from '@/api/types';
import SurveyPhotoDrawer from './SurveyPhotoDrawer';

type PageStatus = 'loading' | 'error' | 'empty' | 'success';

interface EditingCell {
  surveyId: number;
  field: string;
}

export default function SurveyListPage() {
  const [status, setStatus] = useState<PageStatus>('loading');
  const [data, setData] = useState<SurveyListRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [editingCell, setEditingCell] = useState<EditingCell | null>(null);
  const [editValue, setEditValue] = useState('');
  const [saving, setSaving] = useState(false);
  const inputRef = useRef<any>(null);

  const [projectCode, setProjectCode] = useState('');
  const [inputCode, setInputCode] = useState('');
  const [surveyorOptions, setSurveyorOptions] = useState<UserOption[]>([]);
  const [photoSurveyId, setPhotoSurveyId] = useState<number | null>(null);
  const [photoProjectId, setPhotoProjectId] = useState(0);
  const [photoProjectCode, setPhotoProjectCode] = useState('');
  const [drawerOpen, setDrawerOpen] = useState(false);

  // Load surveyor options on mount
  useEffect(() => {
    fetchUsersByRole('ROLE_SURVEYOR').then(setSurveyorOptions).catch(() => {});
  }, []);

  const loadData = useCallback(async (code: string, p: number, size: number) => {
    setStatus('loading');
    try {
      const res = await fetchSurveys({
        projectCode: code || undefined,
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
    loadData(projectCode, page, pageSize);
  }, [projectCode, page, pageSize, loadData]);

  // Focus input when editing starts
  useEffect(() => {
    if (editingCell && inputRef.current) {
      inputRef.current.focus();
    }
  }, [editingCell]);

  const handleSearch = () => {
    setProjectCode(inputCode);
    setPage(1);
  };

  const handleReset = () => {
    setInputCode('');
    setProjectCode('');
    setPage(1);
  };

  const handleSurveyorChange = async (record: SurveyListRecord, value: string) => {
    try {
      await updateSurvey(record.surveyId, { surveyor: value } as any);
      setData(prev => prev.map(item =>
        item.surveyId === record.surveyId ? { ...item, surveyor: value } : item,
      ));
      message.success('勘查人已更新');
    } catch {
      message.error('更新失败');
    }
  };

  const handleOpenPhotos = (record: SurveyListRecord) => {
    setPhotoSurveyId(record.surveyId);
    setPhotoProjectId(record.projectId);
    setPhotoProjectCode(record.projectCode);
    setDrawerOpen(true);
  };

  const startEdit = (record: SurveyListRecord, field: string) => {
    const value = (record as any)[field];
    setEditingCell({ surveyId: record.surveyId, field });
    setEditValue(value !== null && value !== undefined ? String(value) : '');
  };

  const cancelEdit = () => {
    setEditingCell(null);
    setEditValue('');
  };

  const saveEdit = async () => {
    if (!editingCell) return;
    setSaving(true);
    try {
      const payload: Record<string, unknown> = {};
      payload[editingCell.field] = editValue;
      await updateSurvey(editingCell.surveyId, payload as any);
      setData(prev => prev.map(item =>
        item.surveyId === editingCell.surveyId
          ? { ...item, [editingCell.field]: editValue }
          : item
      ));
      setEditingCell(null);
    } catch {
      message.error('保存失败');
    } finally {
      setSaving(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      saveEdit();
    } else if (e.key === 'Escape') {
      cancelEdit();
    }
  };

  // Render editable cell
  const renderEditableCell = (text: unknown, record: SurveyListRecord, field: string) => {
    const isEditing = editingCell?.surveyId === record.surveyId && editingCell?.field === field;
    if (isEditing) {
      return (
        <Input
          ref={inputRef}
          value={editValue}
          onChange={(e) => setEditValue(e.target.value)}
          onKeyDown={handleKeyDown}
          onBlur={cancelEdit}
          size="small"
          style={{ width: '100%' }}
          disabled={saving}
        />
      );
    }
    return (
      <div
        onClick={() => startEdit(record, field)}
        style={{ cursor: 'pointer', minHeight: 22, padding: '2px 4px', borderRadius: 2 }}
        title="点击编辑"
      >
        {text !== null && text !== undefined && String(text) !== '' ? String(text) : <span style={{ color: '#ccc' }}>-</span>}
      </div>
    );
  };

  const columns = [
    { title: '勘查编号', dataIndex: 'surveyCode', width: 180 },
    { title: '项目编号', dataIndex: 'projectCode', width: 180 },
    { title: '勘查码', dataIndex: 'code', width: 80, render: (v: string) => v ? <span style={{ fontFamily: 'monospace', fontWeight: 600 }}>{v}</span> : '-' },
    {
      title: '状态',
      dataIndex: 'surveyStatus',
      width: 90,
      render: (s: string) => <Tag color={s === '已查勘' ? 'green' : 'default'}>{s || '未查勘'}</Tag>,
    },
    {
      title: '地址',
      dataIndex: 'projectAddress',
      width: 200,
      ellipsis: true,
    },
    {
      title: '勘查人',
      dataIndex: 'surveyor',
      width: 130,
      render: (v: string, r: SurveyListRecord) => (
        <Select
          value={v || undefined}
          showSearch
          placeholder="选择勘查人"
          size="small"
          style={{ width: '100%' }}
          filterOption={(input, option) =>
            (option?.label as string || '').toLowerCase().includes(input.toLowerCase())
          }
          options={surveyorOptions.map(u => ({
            label: u.nickname || u.username,
            value: u.nickname || u.username,
          }))}
          onChange={(val) => handleSurveyorChange(r, val)}
        />
      ),
    },
    {
      title: '勘查接待人',
      dataIndex: 'receptionist',
      width: 110,
      render: (v: unknown, r: SurveyListRecord) => renderEditableCell(v, r, 'receptionist'),
    },
    {
      title: '接待人电话',
      dataIndex: 'receptionistPhone',
      width: 130,
      render: (v: unknown, r: SurveyListRecord) => renderEditableCell(v, r, 'receptionistPhone'),
    },
    {
      title: '勘查日期',
      dataIndex: 'surveyDate',
      width: 120,
      render: (v: unknown, r: SurveyListRecord) => renderEditableCell(v, r, 'surveyDate'),
    },
    {
      title: '验看房产证',
      dataIndex: 'propertyCertVerified',
      width: 110,
      render: (v: boolean) => <Tag color={v ? 'green' : 'red'}>{v ? '是' : '否'}</Tag>,
    },
    {
      title: '权属争议',
      dataIndex: 'ownershipDispute',
      width: 150,
      ellipsis: true,
      render: (v: unknown, r: SurveyListRecord) => renderEditableCell(v, r, 'ownershipDispute'),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      width: 150,
      ellipsis: true,
      render: (v: unknown, r: SurveyListRecord) => renderEditableCell(v, r, 'remark'),
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right' as const,
      render: (_: unknown, record: SurveyListRecord) => (
        <Button
          type="primary"
          size="small"
          icon={<CameraOutlined />}
          onClick={() => handleOpenPhotos(record)}
        >
          上传照片
        </Button>
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
          style={{ width: 180 }}
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
            extra={<Button icon={<ReloadOutlined />} onClick={() => loadData(projectCode, page, pageSize)}>重试</Button>}
          />
        );
      case 'empty':
        return <>{searchBar}<Empty description="暂无勘查数据" /></>;
      case 'success':
        return (
          <>
            {searchBar}
            <Table
              rowKey="surveyId"
              columns={columns}
              dataSource={data}
              scroll={{ x: 1600 }}
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
    <PageContainer title="外勘列表">
      {renderContent()}
      <SurveyPhotoDrawer
        surveyId={photoSurveyId}
        projectId={photoProjectId}
        projectCode={photoProjectCode}
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
      />
    </PageContainer>
  );
}
