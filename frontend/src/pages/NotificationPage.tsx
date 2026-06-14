import { useState, useEffect, useCallback } from 'react';
import { List, Button, Tag, message, Empty, Result, Skeleton, Space, Modal, Form, Select, Input } from 'antd';
import { ReloadOutlined, CheckOutlined, SendOutlined } from '@ant-design/icons';
import { fetchNotifications, markAsRead, markAllAsRead, sendNotification } from '@/api/notification';
import { fetchUserOptions } from '@/api/user';
import { useNotificationStore } from '@/store/notificationStore';
import { useAuthStore } from '@/store/authStore';
import type { UserOption } from '@/api/user';
import PageContainer from '@/components/common/PageContainer';

type StatusType = 'loading' | 'error' | 'empty' | 'success';

export default function NotificationPage() {
  const [status, setStatus] = useState<StatusType>('loading');
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);
  const { notifications, setNotifications, markAsRead: markLocalRead, markAllAsRead: markLocalAllRead } =
    useNotificationStore();
  const currentUser = useAuthStore((s) => s.userInfo);

  // Send modal state
  const [sendModalOpen, setSendModalOpen] = useState(false);
  const [users, setUsers] = useState<UserOption[]>([]);
  const [recipientOptions, setRecipientOptions] = useState<UserOption[]>([]);
  const [sending, setSending] = useState(false);
  const [sendForm] = Form.useForm();

  const loadNotifications = useCallback(async () => {
    setStatus('loading');
    try {
      const res = await fetchNotifications({ page: page - 1, size: 10 });
      setNotifications(res.content);
      setTotal(res.totalElements);
      setStatus(res.content.length === 0 ? 'empty' : 'success');
    } catch {
      setStatus('error');
    }
  }, [page, setNotifications]);

  useEffect(() => {
    loadNotifications();
  }, [loadNotifications]);

  // Fetch all users for sender name display
  useEffect(() => {
    fetchUserOptions()
      .then(setUsers)
      .catch(() => setUsers([]));
  }, []);

  const handleMarkAsRead = async (id: number) => {
    try {
      await markAsRead(id);
      markLocalRead(id);
      message.success('已标记为已读');
    } catch {
      message.error('操作失败');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead();
      markLocalAllRead();
      message.success('全部标记为已读');
    } catch {
      message.error('操作失败');
    }
  };

  const handleOpenSend = () => {
    // Exclude current user from recipient list
    setRecipientOptions(users.filter((u) => u.id !== currentUser?.id));
    sendForm.resetFields();
    setSendModalOpen(true);
  };

  const handleSend = async () => {
    const values = await sendForm.validateFields();
    setSending(true);
    try {
      await sendNotification(values);
      message.success('发送成功');
      setSendModalOpen(false);
      loadNotifications();
    } catch {
      message.error('发送失败');
    } finally {
      setSending(false);
    }
  };

  // Build a map of userId -> nickname for display
  const userMap: Record<number, string> = {};
  for (const u of users) {
    userMap[u.id] = u.nickname || u.username;
  }

  const actionBar = (
    <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
      <Button icon={<CheckOutlined />} onClick={handleMarkAllAsRead}>全部标记已读</Button>
      <Button type="primary" icon={<SendOutlined />} onClick={handleOpenSend}>发送通知</Button>
    </div>
  );

  const renderContent = () => {
    switch (status) {
      case 'loading':
        return (
          <>
            {actionBar}
            <Skeleton active paragraph={{ rows: 6 }} />
          </>
        );
      case 'error':
        return (
          <>
            {actionBar}
            <Result
              status="error"
              title="加载失败"
              extra={<Button icon={<ReloadOutlined />} onClick={loadNotifications}>重试</Button>}
            />
          </>
        );
      case 'empty':
        return (
          <>
            {actionBar}
            <Empty description="暂无通知" />
          </>
        );
      case 'success':
        return (
          <>
            {actionBar}
            <List
              dataSource={notifications}
              pagination={{
                current: page,
                total,
                pageSize: 10,
                onChange: (p) => setPage(p),
              }}
              renderItem={(item) => (
                <List.Item
                  key={item.id}
                  style={{ background: item.isRead ? '#fff' : '#f6ffed', padding: '12px 16px', borderRadius: 4 }}
                  actions={[
                    !item.isRead && (
                      <Button
                        key="read"
                        type="link"
                        size="small"
                        onClick={() => handleMarkAsRead(item.id)}
                      >
                        标记已读
                      </Button>
                    ),
                  ]}
                >
                  <List.Item.Meta
                    title={
                      <Space>
                        {item.title}
                        {!item.isRead && <Tag color="blue">未读</Tag>}
                      </Space>
                    }
                    description={
                      <>
                        {item.senderId && (
                          <div style={{ color: '#666', fontSize: 12, marginBottom: 4 }}>
                            来自: {userMap[item.senderId] || `用户${item.senderId}`}
                          </div>
                        )}
                        <div>{item.content}</div>
                        <div style={{ color: '#999', fontSize: 12, marginTop: 4 }}>
                          {item.createdAt?.substring(0, 19).replace('T', ' ')}
                        </div>
                      </>
                    }
                  />
                </List.Item>
              )}
            />
          </>
        );
    }
  };

  return (
    <PageContainer title="通知中心">
      {renderContent()}

      <Modal
        title="发送通知"
        open={sendModalOpen}
        onOk={handleSend}
        onCancel={() => setSendModalOpen(false)}
        confirmLoading={sending}
        destroyOnClose
      >
        <Form form={sendForm} layout="vertical" preserve={false}>
          <Form.Item name="recipientId" label="接收人" rules={[{ required: true, message: '请选择接收人' }]}>
            <Select
              placeholder="选择接收人"
              showSearch
              filterOption={(input, option) =>
                (option?.label as string)?.toLowerCase().includes(input.toLowerCase())
              }
              options={recipientOptions.map((u) => ({
                label: `${u.nickname || u.username} (${u.username})`,
                value: u.id,
              }))}
            />
          </Form.Item>
          <Form.Item name="title" label="标题" rules={[{ required: true, message: '请输入标题' }]}>
            <Input placeholder="通知标题" maxLength={200} />
          </Form.Item>
          <Form.Item name="content" label="内容" rules={[{ required: true, message: '请输入内容' }]}>
            <Input.TextArea rows={4} placeholder="通知内容" />
          </Form.Item>
        </Form>
      </Modal>
    </PageContainer>
  );
}
