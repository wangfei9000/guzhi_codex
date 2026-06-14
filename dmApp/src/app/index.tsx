import React, { useState } from 'react';
import {
  View, Text, TextInput, TouchableOpacity, StyleSheet,
  ScrollView, Image, Alert, ActivityIndicator, Switch,
  Platform, Modal, Dimensions,
} from 'react-native';
import { Redirect } from 'expo-router';
import * as ImagePicker from 'expo-image-picker';
import DateTimePicker from '@react-native-community/datetimepicker';
import {
  fetchSurveyByCode,
  fetchProject,
  updateSurvey,
  uploadFile,
  createSurveyPhoto,
  deleteSurveyPhoto,
  getImageUrl,
} from '@/api/survey';
import { isApiError } from '@/api/client';
import { useAuth } from '@/store/authStore';
import type { SurveyRecord, ProjectRecord, SurveyPhotoRecord } from '@/api/types';

const { width: SCREEN_WIDTH } = Dimensions.get('window');
// Photo grid: 2 columns, small thumbnails
const PHOTO_GAP = 8;
const PHOTO_PADDING = 32; // 16*2 card padding
const PHOTO_WIDTH = (SCREEN_WIDTH - PHOTO_PADDING - PHOTO_GAP) / 2;
const PHOTO_HEIGHT = 120;

export default function SurveyScreen() {
  const { isLoggedIn, token, logout } = useAuth();

  // Auth guard: redirect to login if not authenticated
  if (!isLoggedIn) {
    return <Redirect href="/login" />;
  }

  // ---- Search state ----
  const [searchCode, setSearchCode] = useState('');
  const [loading, setLoading] = useState(false);

  // ---- Data state ----
  const [survey, setSurvey] = useState<SurveyRecord | null>(null);
  const [project, setProject] = useState<ProjectRecord | null>(null);

  // ---- Save state ----
  const [saving, setSaving] = useState(false);

  // ---- Upload state ----
  const [uploading, setUploading] = useState(false);

  // ---- Date/Time picker state ----
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [showStartTime, setShowStartTime] = useState(false);
  const [showEndTime, setShowEndTime] = useState(false);

  // ---- Photo preview modal ----
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  // ==================== Search ====================
  const handleSearch = async () => {
    const code = searchCode.trim();
    if (!code) return;

    // Debug: confirm token before request
    if (!token) {
      Alert.alert('Token 缺失', '当前没有登录 Token，请重新登录', [
        { text: '去登录', onPress: () => logout() },
      ]);
      return;
    }

    setLoading(true);
    try {
      const s = await fetchSurveyByCode(code);
      setSurvey(s);
      try {
        const p = await fetchProject((s as any).projectId || 0);
        setProject(p);
      } catch {
        setProject(null);
      }
    } catch (e: any) {
      setSurvey(null);
      setProject(null);

      if (isApiError(e)) {
        if (e.status === 401 || e.status === 403) {
          Alert.alert('认证失败', e.message, [
            { text: '重新登录', onPress: () => logout() },
          ]);
        } else {
          Alert.alert('查询失败', e.message);
        }
      } else {
        // Unexpected error — show raw message for debugging
        Alert.alert('查询异常', e?.message || String(e));
      }
    } finally {
      setLoading(false);
    }
  };

  // ==================== Save ====================
  const handleSave = async () => {
    if (!survey) return;
    setSaving(true);
    try {
      await updateSurvey(survey.id, {
        surveyor: survey.surveyor,
        receptionist: survey.receptionist,
        receptionistPhone: survey.receptionistPhone,
        surveyDate: survey.surveyDate,
        startTime: survey.startTime,
        endTime: survey.endTime,
        propertyCertVerified: survey.propertyCertVerified,
        ownershipDispute: survey.ownershipDispute,
        remark: survey.remark,
        surveyStatus: survey.surveyStatus,
      } as any);
      Alert.alert('成功', '勘查信息保存成功');
    } catch {
      Alert.alert('失败', '保存失败，请重试');
    } finally {
      setSaving(false);
    }
  };

  // ==================== Photo Upload ====================
  const handleUploadPhoto = async () => {
    if (!survey || !project) return;

    // Request permission
    const perm = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (!perm.granted) {
      Alert.alert('权限不足', '需要相册权限才能上传照片');
      return;
    }

    // Show action sheet: camera or gallery
    Alert.alert('选择照片', '', [
      {
        text: '拍照',
        onPress: async () => {
          const camPerm = await ImagePicker.requestCameraPermissionsAsync();
          if (!camPerm.granted) {
            Alert.alert('权限不足', '需要相机权限才能拍照');
            return;
          }
          const result = await ImagePicker.launchCameraAsync({
            mediaTypes: ['images'],
            quality: 0.7,
            allowsEditing: false,
          });
          if (!result.canceled && result.assets[0]) {
            await doUpload(result.assets[0].uri, result.assets[0].fileName || 'photo.jpg');
          }
        },
      },
      {
        text: '从相册选择',
        onPress: async () => {
          const result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ['images'],
            quality: 0.7,
            allowsMultipleSelection: false,
          });
          if (!result.canceled && result.assets[0]) {
            await doUpload(result.assets[0].uri, result.assets[0].fileName || 'photo.jpg');
          }
        },
      },
      { text: '取消', style: 'cancel' },
    ]);
  };

  const doUpload = async (uri: string, fileName: string) => {
    if (!survey || !project) return;
    setUploading(true);
    try {
      const uploaded = await uploadFile(uri, fileName, 'image/jpeg', project.projectCode);
      const created = await createSurveyPhoto((survey as any).projectId || project.id, survey.id, {
        photoPath: uploaded.filePath,
        photoDescription: '',
      } as any);
      setSurvey({
        ...survey,
        photos: [...(survey.photos || []), created],
      });
      Alert.alert('成功', '照片上传成功');
    } catch {
      Alert.alert('失败', '照片上传失败');
    } finally {
      setUploading(false);
    }
  };

  // ==================== Photo Delete ====================
  const handleDeletePhoto = (photo: SurveyPhotoRecord) => {
    if (!survey) return;
    Alert.alert('确认删除', '确定要删除这张照片吗？', [
      { text: '取消', style: 'cancel' },
      {
        text: '删除',
        style: 'destructive',
        onPress: async () => {
          try {
            await deleteSurveyPhoto(photo.id);
            setSurvey({
              ...survey,
              photos: (survey.photos || []).filter((p) => p.id !== photo.id),
            });
          } catch {
            Alert.alert('失败', '删除失败');
          }
        },
      },
    ]);
  };

  // ==================== Field Updates ====================
  const updateField = (field: keyof SurveyRecord, value: any) => {
    if (!survey) return;
    setSurvey({ ...survey, [field]: value });
  };

  // ==================== Parse time for DateTimePicker ====================
  const parseTimeString = (timeStr: string | undefined): Date => {
    if (!timeStr) return new Date();
    const parts = timeStr.split(':');
    const d = new Date();
    d.setHours(parseInt(parts[0]) || 0);
    d.setMinutes(parseInt(parts[1]) || 0);
    return d;
  };

  const formatTime = (date: Date): string => {
    const h = date.getHours().toString().padStart(2, '0');
    const m = date.getMinutes().toString().padStart(2, '0');
    return `${h}:${m}`;
  };

  // ==================== Render ====================
  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.headerTitle}>外勘录入</Text>
      </View>

      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
      >
        {/* Search Bar */}
        <View style={styles.card}>
          <View style={styles.searchRow}>
            <TextInput
              style={styles.searchInput}
              placeholder="输入4位勘查码"
              placeholderTextColor="#999"
              value={searchCode}
              onChangeText={setSearchCode}
              onSubmitEditing={handleSearch}
              returnKeyType="search"
              autoCapitalize="none"
            />
            <TouchableOpacity
              style={styles.searchButton}
              onPress={handleSearch}
              disabled={loading}
              activeOpacity={0.7}
            >
              {loading ? (
                <ActivityIndicator color="#fff" size="small" />
              ) : (
                <Text style={styles.searchButtonText}>查询</Text>
              )}
            </TouchableOpacity>
          </View>
        </View>

        {/* Loading */}
        {loading && (
          <View style={styles.loadingWrap}>
            <ActivityIndicator size="large" color="#1A56DB" />
            <Text style={styles.loadingText}>查询中...</Text>
          </View>
        )}

        {/* Survey Content */}
        {survey && (
          <>
            {/* Project Info */}
            {project && (
              <View style={styles.card}>
                <Text style={styles.cardTitle}>项目信息</Text>
                <View style={styles.infoRow}>
                  <Text style={styles.infoLabel}>项目编号</Text>
                  <Text style={styles.infoValue}>{project.projectCode}</Text>
                </View>
                <View style={styles.infoRow}>
                  <Text style={styles.infoLabel}>项目名称</Text>
                  <Text style={styles.infoValue}>{project.projectName}</Text>
                </View>
                <View style={styles.infoRow}>
                  <Text style={styles.infoLabel}>地址</Text>
                  <Text style={styles.infoValue}>{project.address || '-'}</Text>
                </View>
              </View>
            )}

            {/* Survey Fields */}
            <View style={styles.card}>
              <Text style={styles.cardTitle}>勘查信息</Text>

              {/* Survey Code */}
              <Text style={styles.fieldLabel}>勘查码</Text>
              <Text style={styles.surveyCode}>
                {survey.code || survey.surveyCode}
              </Text>

              {/* Survey Status Switch */}
              <View style={styles.switchRow}>
                <Text style={styles.switchLabel}>勘查状态</Text>
                <View style={styles.switchRight}>
                  <Text style={[
                    styles.statusText,
                    { color: survey.surveyStatus === '已查勘' ? '#52c41a' : '#999' },
                  ]}>
                    {survey.surveyStatus === '已查勘' ? '已查勘' : '未查勘'}
                  </Text>
                  <Switch
                    value={survey.surveyStatus === '已查勘'}
                    onValueChange={(v) => updateField('surveyStatus', v ? '已查勘' : '未查勘')}
                    trackColor={{ false: '#d9d9d9', true: '#b7eb8f' }}
                    thumbColor={survey.surveyStatus === '已查勘' ? '#52c41a' : '#f5f5f5'}
                  />
                </View>
              </View>

              {/* Surveyor (disabled) */}
              <Text style={styles.fieldLabel}>勘查人</Text>
              <TextInput
                style={[styles.input, styles.inputDisabled]}
                value={survey.surveyor || ''}
                editable={false}
              />

              {/* Receptionist */}
              <Text style={styles.fieldLabel}>接待人</Text>
              <TextInput
                style={styles.input}
                value={survey.receptionist || ''}
                onChangeText={(v) => updateField('receptionist', v)}
                placeholder="接待人姓名"
                placeholderTextColor="#ccc"
              />

              {/* Receptionist Phone */}
              <Text style={styles.fieldLabel}>接待人电话</Text>
              <TextInput
                style={styles.input}
                value={survey.receptionistPhone || ''}
                onChangeText={(v) => updateField('receptionistPhone', v)}
                placeholder="接待人电话"
                placeholderTextColor="#ccc"
                keyboardType="phone-pad"
              />

              {/* Survey Date */}
              <Text style={styles.fieldLabel}>勘查日期</Text>
              <TouchableOpacity
                style={styles.input}
                onPress={() => setShowDatePicker(true)}
              >
                <Text style={survey.surveyDate ? styles.inputText : styles.placeholderText}>
                  {survey.surveyDate || '选择日期'}
                </Text>
              </TouchableOpacity>
              {showDatePicker && (
                <DateTimePicker
                  value={survey.surveyDate ? new Date(survey.surveyDate) : new Date()}
                  mode="date"
                  display={Platform.OS === 'ios' ? 'spinner' : 'default'}
                  onChange={(_, d) => {
                    setShowDatePicker(false);
                    if (d) {
                      const y = d.getFullYear();
                      const m = String(d.getMonth() + 1).padStart(2, '0');
                      const day = String(d.getDate()).padStart(2, '0');
                      updateField('surveyDate', `${y}-${m}-${day}`);
                    }
                  }}
                />
              )}

              {/* Start Time & End Time Row */}
              <View style={styles.timeRow}>
                <View style={styles.timeHalf}>
                  <Text style={styles.fieldLabel}>开始时间</Text>
                  <TouchableOpacity
                    style={styles.input}
                    onPress={() => setShowStartTime(true)}
                  >
                    <Text style={survey.startTime ? styles.inputText : styles.placeholderText}>
                      {survey.startTime || '选择时间'}
                    </Text>
                  </TouchableOpacity>
                  {showStartTime && (
                    <DateTimePicker
                      value={parseTimeString(survey.startTime)}
                      mode="time"
                      display={Platform.OS === 'ios' ? 'spinner' : 'default'}
                      onChange={(_, d) => {
                        setShowStartTime(false);
                        if (d) updateField('startTime', formatTime(d));
                      }}
                    />
                  )}
                </View>
                <View style={styles.timeHalf}>
                  <Text style={styles.fieldLabel}>结束时间</Text>
                  <TouchableOpacity
                    style={styles.input}
                    onPress={() => setShowEndTime(true)}
                  >
                    <Text style={survey.endTime ? styles.inputText : styles.placeholderText}>
                      {survey.endTime || '选择时间'}
                    </Text>
                  </TouchableOpacity>
                  {showEndTime && (
                    <DateTimePicker
                      value={parseTimeString(survey.endTime)}
                      mode="time"
                      display={Platform.OS === 'ios' ? 'spinner' : 'default'}
                      onChange={(_, d) => {
                        setShowEndTime(false);
                        if (d) updateField('endTime', formatTime(d));
                      }}
                    />
                  )}
                </View>
              </View>

              {/* Property Cert Verified */}
              <View style={styles.switchRow}>
                <Text style={styles.switchLabel}>验看房产证</Text>
                <Switch
                  value={survey.propertyCertVerified}
                  onValueChange={(v) => updateField('propertyCertVerified', v)}
                  trackColor={{ false: '#d9d9d9', true: '#91d5ff' }}
                  thumbColor={survey.propertyCertVerified ? '#1890ff' : '#f5f5f5'}
                />
              </View>

              {/* Ownership Dispute */}
              <Text style={styles.fieldLabel}>权属争议</Text>
              <TextInput
                style={[styles.input, styles.textArea]}
                value={survey.ownershipDispute || ''}
                onChangeText={(v) => updateField('ownershipDispute', v)}
                placeholder="权属争议"
                placeholderTextColor="#ccc"
                multiline
                numberOfLines={2}
                textAlignVertical="top"
              />

              {/* Remark */}
              <Text style={styles.fieldLabel}>备注</Text>
              <TextInput
                style={[styles.input, styles.textArea]}
                value={survey.remark || ''}
                onChangeText={(v) => updateField('remark', v)}
                placeholder="备注"
                placeholderTextColor="#ccc"
                multiline
                numberOfLines={2}
                textAlignVertical="top"
              />
            </View>

            {/* Save Button */}
            <TouchableOpacity
              style={[styles.saveButton, saving && styles.buttonDisabled]}
              onPress={handleSave}
              disabled={saving}
              activeOpacity={0.8}
            >
              {saving ? (
                <ActivityIndicator color="#fff" />
              ) : (
                <Text style={styles.saveButtonText}>保存勘查信息</Text>
              )}
            </TouchableOpacity>

            {/* Photos */}
            <View style={styles.card}>
              <Text style={styles.cardTitle}>
                勘查照片 ({(survey.photos || []).length})
              </Text>

              {/* Upload Button */}
              <TouchableOpacity
                style={[styles.uploadButton, uploading && styles.buttonDisabled]}
                onPress={handleUploadPhoto}
                disabled={uploading}
                activeOpacity={0.7}
              >
                {uploading ? (
                  <ActivityIndicator color="#1A56DB" />
                ) : (
                  <Text style={styles.uploadButtonText}>📷 选择照片上传</Text>
                )}
              </TouchableOpacity>

              {/* Photo Grid */}
              <View style={styles.photoGrid}>
                {(survey.photos || []).map((photo) => (
                  <TouchableOpacity
                    key={photo.id}
                    style={styles.photoItem}
                    onPress={() => setPreviewUrl(getImageUrl(photo.photoPath))}
                    onLongPress={() => handleDeletePhoto(photo)}
                  >
                    <Image
                      source={{ uri: getImageUrl(photo.photoPath) }}
                      style={styles.photoImage}
                      resizeMode="cover"
                    />
                    {/* Delete indicator */}
                    <TouchableOpacity
                      style={styles.deleteBadge}
                      onPress={() => handleDeletePhoto(photo)}
                      hitSlop={{ top: 8, bottom: 8, left: 8, right: 8 }}
                    >
                      <Text style={styles.deleteBadgeText}>✕</Text>
                    </TouchableOpacity>
                  </TouchableOpacity>
                ))}
              </View>
              <Text style={styles.photoHint}>长按照片可删除</Text>
            </View>
          </>
        )}

        {/* Empty State */}
        {!survey && !loading && (
          <View style={styles.emptyWrap}>
            <Text style={styles.emptyIcon}>🔍</Text>
            <Text style={styles.emptyText}>输入4位勘查码查询</Text>
          </View>
        )}

        {/* Bottom spacing */}
        <View style={{ height: 40 }} />
      </ScrollView>

      {/* Photo Preview Modal */}
      <Modal visible={!!previewUrl} transparent animationType="fade">
        <TouchableOpacity
          style={styles.previewOverlay}
          onPress={() => setPreviewUrl(null)}
          activeOpacity={1}
        >
          {previewUrl && (
            <Image
              source={{ uri: previewUrl }}
              style={styles.previewImage}
              resizeMode="contain"
            />
          )}
          <Text style={styles.previewClose}>点击关闭</Text>
        </TouchableOpacity>
      </Modal>
    </View>
  );
}

// ==================== Styles ====================
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    backgroundColor: '#1A56DB',
    paddingTop: Platform.OS === 'ios' ? 56 : 36,
    paddingBottom: 16,
    paddingHorizontal: 20,
    alignItems: 'center',
  },
  headerTitle: {
    color: '#fff',
    fontSize: 20,
    fontWeight: '700',
    letterSpacing: 2,
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    padding: 12,
  },

  // Card
  card: {
    backgroundColor: '#fff',
    borderRadius: 10,
    padding: 16,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  cardTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 14,
    borderLeftWidth: 3,
    borderLeftColor: '#1A56DB',
    paddingLeft: 10,
  },

  // Search
  searchRow: {
    flexDirection: 'row',
    gap: 10,
  },
  searchInput: {
    flex: 1,
    height: 48,
    borderWidth: 1,
    borderColor: '#d9d9d9',
    borderRadius: 8,
    paddingHorizontal: 14,
    fontSize: 16,
    color: '#333',
    backgroundColor: '#fafafa',
  },
  searchButton: {
    height: 48,
    backgroundColor: '#1A56DB',
    borderRadius: 8,
    paddingHorizontal: 24,
    justifyContent: 'center',
    alignItems: 'center',
  },
  searchButtonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },

  // Loading
  loadingWrap: {
    alignItems: 'center',
    paddingVertical: 40,
  },
  loadingText: {
    marginTop: 12,
    fontSize: 14,
    color: '#999',
  },

  // Info rows
  infoRow: {
    marginBottom: 10,
  },
  infoLabel: {
    fontSize: 13,
    color: '#999',
    marginBottom: 4,
  },
  infoValue: {
    fontSize: 16,
    color: '#333',
    fontWeight: '500',
  },

  // Survey code
  surveyCode: {
    fontSize: 24,
    fontWeight: '700',
    color: '#1A56DB',
    letterSpacing: 6,
    marginBottom: 16,
    textAlign: 'center',
  },

  // Fields
  fieldLabel: {
    fontSize: 13,
    color: '#666',
    marginBottom: 6,
    marginTop: 4,
  },
  input: {
    height: 44,
    borderWidth: 1,
    borderColor: '#d9d9d9',
    borderRadius: 8,
    paddingHorizontal: 14,
    fontSize: 16,
    color: '#333',
    backgroundColor: '#fafafa',
    marginBottom: 4,
    justifyContent: 'center',
  },
  inputDisabled: {
    backgroundColor: '#f0f0f0',
    color: '#999',
  },
  inputText: {
    fontSize: 16,
    color: '#333',
  },
  placeholderText: {
    fontSize: 16,
    color: '#ccc',
  },
  textArea: {
    height: 70,
    paddingTop: 12,
  },

  // Switch
  switchRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 10,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#f0f0f0',
  },
  switchLabel: {
    fontSize: 15,
    color: '#333',
  },
  switchRight: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  statusText: {
    fontSize: 13,
    fontWeight: '500',
  },

  // Time row
  timeRow: {
    flexDirection: 'row',
    gap: 12,
  },
  timeHalf: {
    flex: 1,
  },

  // Save button
  saveButton: {
    height: 50,
    backgroundColor: '#52c41a',
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
    shadowColor: '#52c41a',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.3,
    shadowRadius: 4,
    elevation: 3,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  saveButtonText: {
    color: '#fff',
    fontSize: 18,
    fontWeight: '600',
  },

  // Upload
  uploadButton: {
    height: 44,
    borderWidth: 1,
    borderColor: '#1A56DB',
    borderStyle: 'dashed',
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
    backgroundColor: '#f0f7ff',
  },
  uploadButtonText: {
    color: '#1A56DB',
    fontSize: 15,
    fontWeight: '500',
  },

  // Photo grid — 2 per row, small thumbnails
  photoGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: PHOTO_GAP,
  },
  photoItem: {
    width: PHOTO_WIDTH,
    height: PHOTO_HEIGHT,
    borderRadius: 6,
    overflow: 'hidden',
    backgroundColor: '#f0f0f0',
  },
  photoImage: {
    width: '100%',
    height: '100%',
  },
  deleteBadge: {
    position: 'absolute',
    top: 6,
    right: 6,
    width: 24,
    height: 24,
    borderRadius: 12,
    backgroundColor: 'rgba(255, 77, 79, 0.9)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  deleteBadgeText: {
    color: '#fff',
    fontSize: 12,
    fontWeight: '700',
  },
  photoHint: {
    textAlign: 'center',
    fontSize: 12,
    color: '#ccc',
    marginTop: 8,
  },

  // Empty
  emptyWrap: {
    alignItems: 'center',
    paddingVertical: 80,
  },
  emptyIcon: {
    fontSize: 48,
    marginBottom: 16,
  },
  emptyText: {
    fontSize: 16,
    color: '#999',
  },

  // Preview modal
  previewOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.9)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  previewImage: {
    width: SCREEN_WIDTH - 32,
    height: SCREEN_WIDTH - 32,
  },
  previewClose: {
    color: '#fff',
    fontSize: 14,
    marginTop: 20,
  },
});
