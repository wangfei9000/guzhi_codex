import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Avatar, Badge, Button, Empty, Input, List, Skeleton, Space, Tooltip, Typography, message } from 'antd';
import { ReloadOutlined, SendOutlined, UserOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import PageContainer from '@/components/common/PageContainer';
import { fetchChatMessages, fetchChatUsers, markConversationRead, sendChatMessage } from '@/api/chat';
import { useAuthStore } from '@/store/authStore';
import { useChatStore } from '@/store/chatStore';
import type { ChatMessage, ChatUser } from '@/api/types';
import './ChatPage.css';

const { Text } = Typography;
const { TextArea } = Input;

function displayName(user?: Pick<ChatUser, 'nickname' | 'username'> | null) {
  if (!user) return '';
  return user.nickname?.trim() || user.username;
}

function avatarText(user: ChatUser) {
  const name = displayName(user);
  return name ? name.slice(0, 1).toUpperCase() : <UserOutlined />;
}

function formatTime(value?: string | null) {
  if (!value) return '';
  const time = dayjs(value);
  if (!time.isValid()) return '';
  return time.isSame(dayjs(), 'day') ? time.format('HH:mm') : time.format('MM-DD HH:mm');
}

function appendMessage(messages: ChatMessage[], messageItem: ChatMessage) {
  if (messages.some((item) => item.id === messageItem.id)) {
    return messages;
  }
  return [...messages, messageItem].sort(
    (left, right) => dayjs(left.createdAt).valueOf() - dayjs(right.createdAt).valueOf()
  );
}

function sortUsers(users: ChatUser[]) {
  return [...users].sort((left, right) => {
    if (left.online !== right.online) {
      return left.online ? -1 : 1;
    }
    const leftTime = left.lastMessageTime ? dayjs(left.lastMessageTime).valueOf() : 0;
    const rightTime = right.lastMessageTime ? dayjs(right.lastMessageTime).valueOf() : 0;
    if (leftTime !== rightTime) {
      return rightTime - leftTime;
    }
    return displayName(left).localeCompare(displayName(right), 'zh-CN');
  });
}

function updateUserPreview(
  users: ChatUser[],
  chatMessage: ChatMessage,
  currentUserId: number,
  activePeerId: number | null
) {
  const peerId = chatMessage.senderId === currentUserId ? chatMessage.recipientId : chatMessage.senderId;
  return users.map((user) => {
    if (user.id !== peerId) {
      return user;
    }
    const shouldCountUnread = chatMessage.recipientId === currentUserId && activePeerId !== peerId;
    return {
      ...user,
      unreadCount: activePeerId === peerId ? 0 : user.unreadCount + (shouldCountUnread ? 1 : 0),
      lastMessage: chatMessage.content,
      lastSenderId: chatMessage.senderId,
      lastMessageTime: chatMessage.createdAt,
    };
  });
}

export default function ChatPage() {
  const currentUser = useAuthStore((s) => s.userInfo);
  const lastSocketMessage = useChatStore((s) => s.lastMessage);
  const presenceByUserId = useChatStore((s) => s.presenceByUserId);

  const [users, setUsers] = useState<ChatUser[]>([]);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [draft, setDraft] = useState('');
  const [usersLoading, setUsersLoading] = useState(true);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [sending, setSending] = useState(false);

  const selectedUserIdRef = useRef<number | null>(null);
  const handledSocketMessageIds = useRef<Set<number>>(new Set());
  const endRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    selectedUserIdRef.current = selectedUserId;
  }, [selectedUserId]);

  const resolvedUsers = useMemo(() => {
    return sortUsers(
      users.map((user) => ({
        ...user,
        online: presenceByUserId[user.id] ?? user.online,
      }))
    );
  }, [presenceByUserId, users]);

  const selectedUser = useMemo(
    () => resolvedUsers.find((user) => user.id === selectedUserId) ?? null,
    [resolvedUsers, selectedUserId]
  );

  const loadUsers = useCallback(async () => {
    if (!currentUser?.id) {
      setUsersLoading(false);
      return;
    }
    setUsersLoading(true);
    try {
      const list = await fetchChatUsers();
      setUsers(list);
      setSelectedUserId((previous) => {
        if (previous && list.some((user) => user.id === previous && user.id !== currentUser.id)) {
          return previous;
        }
        return list.find((user) => user.id !== currentUser.id)?.id ?? null;
      });
    } catch {
      message.error('聊天用户加载失败');
    } finally {
      setUsersLoading(false);
    }
  }, [currentUser?.id]);

  const markPeerRead = useCallback(async (peerId: number) => {
    try {
      await markConversationRead(peerId);
      setUsers((previous) =>
        previous.map((user) => (user.id === peerId ? { ...user, unreadCount: 0 } : user))
      );
    } catch {
      // Keep the UI usable even if the read receipt fails.
    }
  }, []);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  useEffect(() => {
    if (!selectedUserId || !currentUser?.id) {
      setMessages([]);
      return;
    }

    let cancelled = false;
    setMessagesLoading(true);
    fetchChatMessages(selectedUserId, { page: 0, size: 100 })
      .then((page) => {
        if (cancelled) return;
        setMessages([...page.content].reverse());
        markPeerRead(selectedUserId);
      })
      .catch(() => {
        if (!cancelled) {
          message.error('聊天记录加载失败');
          setMessages([]);
        }
      })
      .finally(() => {
        if (!cancelled) {
          setMessagesLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [currentUser?.id, markPeerRead, selectedUserId]);

  useEffect(() => {
    if (!lastSocketMessage || !currentUser?.id) {
      return;
    }
    if (handledSocketMessageIds.current.has(lastSocketMessage.id)) {
      return;
    }
    handledSocketMessageIds.current.add(lastSocketMessage.id);

    const peerId =
      lastSocketMessage.senderId === currentUser.id
        ? lastSocketMessage.recipientId
        : lastSocketMessage.senderId;
    const activePeerId = selectedUserIdRef.current;

    setUsers((previous) => updateUserPreview(previous, lastSocketMessage, currentUser.id, activePeerId));

    if (activePeerId === peerId) {
      setMessages((previous) => appendMessage(previous, lastSocketMessage));
      if (lastSocketMessage.recipientId === currentUser.id) {
        markPeerRead(peerId);
      }
    }
  }, [currentUser?.id, lastSocketMessage, markPeerRead]);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }, [messages.length, selectedUserId]);

  const handleSelectUser = (user: ChatUser) => {
    if (user.id === currentUser?.id) {
      return;
    }
    setSelectedUserId(user.id);
  };

  const handleSend = async () => {
    const content = draft.trim();
    if (!content || !selectedUser || !currentUser?.id || selectedUser.id === currentUser.id) {
      return;
    }

    setSending(true);
    try {
      const sent = await sendChatMessage({ recipientId: selectedUser.id, content });
      setDraft('');
      setMessages((previous) => appendMessage(previous, sent));
      setUsers((previous) => updateUserPreview(previous, sent, currentUser.id, selectedUser.id));
    } catch {
      message.error('发送失败');
    } finally {
      setSending(false);
    }
  };

  const renderUser = (user: ChatUser) => {
    const isSelf = user.id === currentUser?.id;
    const isSelected = user.id === selectedUserId;
    const online = Boolean(user.online);
    const preview =
      user.lastMessage && user.lastSenderId === currentUser?.id
        ? `我：${user.lastMessage}`
        : user.lastMessage || '暂无消息';

    return (
      <List.Item
        key={user.id}
        className={`chat-user-item${isSelected ? ' is-selected' : ''}${isSelf ? ' is-self' : ''}`}
        onClick={() => handleSelectUser(user)}
      >
        <div className="chat-user-meta">
          <Badge dot color={online ? '#52c41a' : '#d9d9d9'} offset={[-2, 34]}>
            <Avatar
              size={40}
              style={{
                background: online ? '#1677ff' : '#d9d9d9',
                color: online ? '#fff' : '#8c8c8c',
                filter: online ? 'none' : 'grayscale(1)',
              }}
            >
              {avatarText(user)}
            </Avatar>
          </Badge>
          <div className="chat-user-content">
            <div className="chat-user-title-row">
              <Text
                className="chat-user-name"
                style={{ color: online ? '#1f1f1f' : '#8c8c8c', fontWeight: online ? 600 : 400 }}
              >
                {displayName(user)}
                {isSelf ? '（我）' : ''}
              </Text>
              <Text type="secondary" style={{ fontSize: 12, flex: '0 0 auto' }}>
                {formatTime(user.lastMessageTime)}
              </Text>
            </div>
            <div className="chat-user-preview-row">
              <Text className="chat-user-preview" type={online ? 'secondary' : undefined} style={{ color: online ? undefined : '#a8a8a8' }}>
                {preview}
              </Text>
              {!isSelf && user.unreadCount > 0 && (
                <Badge count={user.unreadCount} size="small" style={{ flex: '0 0 auto' }} />
              )}
            </div>
          </div>
        </div>
      </List.Item>
    );
  };

  return (
    <PageContainer title="即时聊天">
      <div className="chat-shell">
        <aside className="chat-sidebar">
          <div className="chat-sidebar-header">
            <Text strong>全部用户</Text>
            <Tooltip title="刷新">
              <Button
                type="text"
                icon={<ReloadOutlined />}
                onClick={loadUsers}
                loading={usersLoading}
              />
            </Tooltip>
          </div>
          <div className="chat-user-list">
            {usersLoading ? (
              <div style={{ padding: 16 }}>
                <Skeleton active avatar paragraph={{ rows: 8 }} />
              </div>
            ) : (
              <List dataSource={resolvedUsers} renderItem={renderUser} locale={{ emptyText: '暂无用户' }} />
            )}
          </div>
        </aside>

        <section className="chat-panel">
          <div className="chat-panel-header">
            {selectedUser ? (
              <Space>
                <Badge dot color={selectedUser.online ? '#52c41a' : '#d9d9d9'} offset={[-2, 34]}>
                  <Avatar
                    size={40}
                    style={{
                      background: selectedUser.online ? '#1677ff' : '#d9d9d9',
                      color: selectedUser.online ? '#fff' : '#8c8c8c',
                    }}
                  >
                    {avatarText(selectedUser)}
                  </Avatar>
                </Badge>
                <div>
                  <Text strong>{displayName(selectedUser)}</Text>
                  <div>
                    <Text type="secondary" style={{ fontSize: 12 }}>
                      {selectedUser.online ? '在线' : '离线'}
                    </Text>
                  </div>
                </div>
              </Space>
            ) : (
              <Text type="secondary">请选择联系人</Text>
            )}
          </div>

          <div className="chat-panel-body">
            {!selectedUser ? (
              <div className="chat-empty-state">
                <Empty description="请选择左侧用户开始聊天" />
              </div>
            ) : messagesLoading ? (
              <Skeleton active paragraph={{ rows: 10 }} />
            ) : messages.length === 0 ? (
              <div className="chat-empty-state">
                <Empty description="暂无聊天记录" />
              </div>
            ) : (
              <>
                {messages.map((item) => {
                  const mine = item.senderId === currentUser?.id;
                  return (
                    <div key={item.id} className={`chat-message ${mine ? 'is-mine' : 'is-peer'}`}>
                      <div className="chat-bubble">{item.content}</div>
                      <div className="chat-message-time">{formatTime(item.createdAt)}</div>
                    </div>
                  );
                })}
                <div ref={endRef} />
              </>
            )}
          </div>

          <div className="chat-input-area">
            <div className="chat-input-row">
              <TextArea
                value={draft}
                onChange={(event) => setDraft(event.target.value)}
                placeholder={selectedUser ? '输入消息' : '请选择联系人'}
                autoSize={{ minRows: 2, maxRows: 5 }}
                maxLength={2000}
                disabled={!selectedUser || sending}
                onPressEnter={(event) => {
                  if (!event.shiftKey) {
                    event.preventDefault();
                    handleSend();
                  }
                }}
              />
              <Button
                type="primary"
                icon={<SendOutlined />}
                loading={sending}
                disabled={!selectedUser || !draft.trim()}
                onClick={handleSend}
              >
                发送
              </Button>
            </div>
          </div>
        </section>
      </div>
    </PageContainer>
  );
}
