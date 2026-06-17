import { useState } from 'react';
import { Alert, Button, Card, Form, Input, Space, Tag, message } from 'antd';
import { ClearOutlined, MessageOutlined, SendOutlined } from '@ant-design/icons';
import PageContainer from '@/components/common/PageContainer';
import { generateAssistantResponse } from '@/api/assistant';

const { TextArea } = Input;

export default function MyAssistantPage() {
  const [prompt, setPrompt] = useState('');
  const [answer, setAnswer] = useState('');
  const [model, setModel] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleGenerate = async () => {
    const content = prompt.trim();
    if (!content) {
      message.warning('请输入内容');
      return;
    }

    setLoading(true);
    setError('');
    try {
      const data = await generateAssistantResponse(content);
      setAnswer(data.response || '');
      setModel(data.model || '');
    } catch (err) {
      const errorMessage =
        (err as { response?: { data?: { message?: string } } }).response?.data?.message || '生成失败';
      setError(errorMessage);
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setPrompt('');
    setAnswer('');
    setModel('');
    setError('');
  };

  return (
    <PageContainer title="我的助理">
      <Card title="输入内容" style={{ marginBottom: 16 }}>
        <Form layout="vertical">
          <Form.Item label="请输入问题" required>
            <TextArea
              value={prompt}
              onChange={(event) => setPrompt(event.target.value)}
              placeholder="请输入需要咨询的内容"
              autoSize={{ minRows: 5, maxRows: 10 }}
              disabled={loading}
              maxLength={4000}
              showCount
            />
          </Form.Item>
          <Space wrap>
            <Button
              type="primary"
              icon={<SendOutlined />}
              loading={loading}
              disabled={!prompt.trim()}
              onClick={handleGenerate}
            >
              发送
            </Button>
            <Button icon={<ClearOutlined />} disabled={loading && !answer} onClick={handleClear}>
              清空
            </Button>
          </Space>
        </Form>
      </Card>

      <Card
        title={
          <Space>
            <MessageOutlined />
            <span>输出内容</span>
            {model && <Tag color="blue">{model}</Tag>}
          </Space>
        }
      >
        {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} />}
        <TextArea
          value={loading && !answer ? '正在生成...' : answer}
          placeholder="模型回复会显示在这里"
          readOnly
          autoSize={{ minRows: 10, maxRows: 20 }}
        />
      </Card>
    </PageContainer>
  );
}
