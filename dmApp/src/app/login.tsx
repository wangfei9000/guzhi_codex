import React, { useState } from 'react';
import {
  View, Text, TextInput, TouchableOpacity, StyleSheet,
  KeyboardAvoidingView, Platform, Alert, ActivityIndicator,
} from 'react-native';
import { useRouter, Redirect } from 'expo-router';
import axios from 'axios';
import { useAuth } from '@/store/authStore';
import { API_BASE } from '@/utils/constants';
import type { ApiResponse, TokenInfo, UserInfo } from '@/api/types';

export default function LoginScreen() {
  const { login, isLoggedIn } = useAuth();
  const router = useRouter();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  // Already logged in
  if (isLoggedIn) {
    return <Redirect href="/" />;
  }

  const handleLogin = async () => {
    const user = username.trim();
    const pass = password;
    if (!user || !pass) {
      Alert.alert('提示', '请输入用户名和密码');
      return;
    }
    setLoading(true);
    try {
      // Step 1: login
      const loginUrl = `${API_BASE}/auth/login`;
      const res = await axios.post<ApiResponse<TokenInfo>>(loginUrl, {
        username: user,
        password: pass,
      });

      if (res.data.code !== 200 || !res.data.data?.accessToken) {
        Alert.alert('登录失败', res.data.message || '用户名或密码错误');
        return;
      }

      const token = res.data.data.accessToken;

      // Step 2: fetch user info
      const meUrl = `${API_BASE}/user/me`;
      const userRes = await axios.get<ApiResponse<UserInfo>>(meUrl, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (userRes.data.code !== 200 || !userRes.data.data) {
        Alert.alert('登录失败', '获取用户信息失败');
        return;
      }

      // Step 3: save to storage & state, then auto-navigate
      await login(token, userRes.data.data);
      router.replace('/');

    } catch (e: any) {
      // Show detailed error for debugging
      if (e?.response) {
        // Server responded with error
        const status = e.response.status;
        const msg = e.response.data?.message || '';
        Alert.alert(`登录失败 (${status})`, msg || '服务器返回错误');
      } else if (e?.request) {
        // No response received — network problem
        Alert.alert(
          '网络连接失败',
          `无法连接到服务器\n${API_BASE}\n\n请检查：\n1. 手机网络是否正常\n2. 服务器地址是否正确`,
        );
      } else {
        // Something else went wrong
        Alert.alert('登录失败', e?.message || '未知错误');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <View style={styles.inner}>
        {/* Logo / Title */}
        <View style={styles.header}>
          <Text style={styles.logo}>🏠</Text>
          <Text style={styles.title}>估值管理系统</Text>
          <Text style={styles.subtitle}>Admin Management System</Text>
        </View>

        {/* Form */}
        <View style={styles.form}>
          <Text style={styles.label}>用户名</Text>
          <TextInput
            style={styles.input}
            placeholder="请输入用户名"
            placeholderTextColor="#999"
            value={username}
            onChangeText={setUsername}
            autoCapitalize="none"
            autoCorrect={false}
            editable={!loading}
          />

          <Text style={styles.label}>密码</Text>
          <TextInput
            style={styles.input}
            placeholder="请输入密码"
            placeholderTextColor="#999"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
            editable={!loading}
            onSubmitEditing={handleLogin}
          />

          <TouchableOpacity
            style={[styles.button, loading && styles.buttonDisabled]}
            onPress={handleLogin}
            disabled={loading}
            activeOpacity={0.8}
          >
            {loading ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <Text style={styles.buttonText}>登 录</Text>
            )}
          </TouchableOpacity>
        </View>

        {/* Footer */}
        <Text style={styles.footer}>默认账号: admin / admin123</Text>
      </View>
    </KeyboardAvoidingView>
  );
}

// ---- Styles (unchanged) ----
const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f0f2f5' },
  inner: { flex: 1, justifyContent: 'center', paddingHorizontal: 32 },
  header: { alignItems: 'center', marginBottom: 48 },
  logo: { fontSize: 64, marginBottom: 12 },
  title: { fontSize: 28, fontWeight: '700', color: '#1A56DB', letterSpacing: 2 },
  subtitle: { fontSize: 14, color: '#999', marginTop: 8 },
  form: {
    backgroundColor: '#fff', borderRadius: 12, padding: 24,
    shadowColor: '#000', shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.08, shadowRadius: 8, elevation: 4,
  },
  label: { fontSize: 15, fontWeight: '500', color: '#333', marginBottom: 8, marginTop: 12 },
  input: {
    height: 48, borderWidth: 1, borderColor: '#d9d9d9', borderRadius: 8,
    paddingHorizontal: 14, fontSize: 16, color: '#333', backgroundColor: '#fafafa',
  },
  button: {
    height: 48, backgroundColor: '#1A56DB', borderRadius: 8,
    justifyContent: 'center', alignItems: 'center', marginTop: 28,
  },
  buttonDisabled: { opacity: 0.6 },
  buttonText: { color: '#fff', fontSize: 18, fontWeight: '600', letterSpacing: 4 },
  footer: { textAlign: 'center', color: '#bbb', fontSize: 13, marginTop: 32 },
});
