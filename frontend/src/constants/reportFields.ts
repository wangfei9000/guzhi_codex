export const PHOTO_CATEGORY_OPTIONS = [
  { value: 'PROPERTY_PHOTO', label: '估价对象照片' },
  { value: 'LOCATION_MAP', label: '位置图' },
  { value: 'CERTIFICATE', label: '权属/权证附件' },
  { value: 'BUSINESS_LICENSE', label: '营业执照' },
  { value: 'RECORD_CERTIFICATE', label: '备案证书' },
  { value: 'VALUER_CERTIFICATE', label: '估价师证书' },
];

export const PHOTO_CATEGORY_LABELS = PHOTO_CATEGORY_OPTIONS.reduce<Record<string, string>>((acc, item) => {
  acc[item.value] = item.label;
  return acc;
}, {});

export const DEFAULT_PHOTO_CATEGORY = 'PROPERTY_PHOTO';
