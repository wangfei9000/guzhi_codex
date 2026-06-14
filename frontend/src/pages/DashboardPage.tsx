import { useState, useEffect } from 'react';
import { Card, Col, Row, Statistic, Skeleton } from 'antd';
import { UserOutlined, TeamOutlined, FileOutlined, BellOutlined } from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import { fetchDashboardStats } from '@/api/dashboard';

export default function DashboardPage() {
  const [loading, setLoading] = useState(true);
  const [userCount, setUserCount] = useState(0);
  const [projectCount, setProjectCount] = useState(0);
  const [fileCount, setFileCount] = useState(0);
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    let cancelled = false;
    const loadStats = async () => {
      setLoading(true);
      try {
        const stats = await fetchDashboardStats();
        if (!cancelled) {
          setUserCount(stats.userCount);
          setProjectCount(stats.projectCount);
          setFileCount(stats.fileCount);
          setUnreadCount(stats.unreadNotificationCount);
        }
      } catch {
        // keep defaults on error
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    loadStats();
    return () => { cancelled = true; };
  }, []);

  return (
    <PageContainer title="仪表盘">
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            {loading ? (
              <Skeleton.Input active size="small" style={{ width: 120 }} />
            ) : (
              <Statistic title="用户总数" value={userCount} prefix={<UserOutlined />} />
            )}
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            {loading ? (
              <Skeleton.Input active size="small" style={{ width: 120 }} />
            ) : (
              <Statistic title="项目数量" value={projectCount} prefix={<TeamOutlined />} />
            )}
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            {loading ? (
              <Skeleton.Input active size="small" style={{ width: 120 }} />
            ) : (
              <Statistic title="文件数量" value={fileCount} prefix={<FileOutlined />} />
            )}
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            {loading ? (
              <Skeleton.Input active size="small" style={{ width: 120 }} />
            ) : (
              <Statistic title="未读通知" value={unreadCount} prefix={<BellOutlined />} />
            )}
          </Card>
        </Col>
      </Row>
      <Card style={{ marginTop: 16 }}>
        <h3>欢迎使用后台管理系统</h3>
        <p style={{ color: '#999', marginTop: 8 }}>
          这是一个线下估值系统的后台管理界面，您可以在这里查看系统的基本统计数据，并进行相关的管理操作。请使用左侧菜单导航到不同的功能模块。
        </p>
      </Card>
    </PageContainer>
  );
}
