// ---- API Response ----
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

// ---- Auth ----
export interface TokenInfo {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface UserInfo {
  id: number;
  username: string;
  email: string;
  phone: string;
  nickname: string;
  status: number;
  roles: RoleInfo[];
}

export interface RoleInfo {
  id: number;
  roleName: string;
  roleCode: string;
  description: string;
  permissions: PermissionInfo[];
}

export interface PermissionInfo {
  id: number;
  permName: string;
  permCode: string;
  parentId: number | null;
  type: 'MENU' | 'BUTTON';
  path: string | null;
  icon: string | null;
  sortOrder: number;
  children?: PermissionInfo[];
}

// ---- Project ----
export interface ProjectRecord {
  id: number;
  projectCode: string;
  projectName: string;
  city: string;
  district: string;
  area: string;
  address: string;
  registrar: string;
  registrationDate: string;
  clientContact: string;
  clientPhone: string;
  valuationPurpose: string;
  valuationTime: string;
  expectedPrice: number;
  status: string;
  remark: string;
  createdAt: string;
}

// ---- Survey ----
export interface SurveyPhotoRecord {
  id: number;
  surveyId: number;
  photoCode: string;
  photoPath: string;
  photoDescription: string;
  createdAt: string;
}

export interface SurveyRecord {
  id: number;
  code?: string;
  surveyStatus?: string;
  surveyCode: string;
  surveyor: string;
  receptionist: string;
  receptionistPhone: string;
  surveyDate: string;
  startTime: string;
  endTime: string;
  propertyCertVerified: boolean;
  ownershipDispute: string;
  remark: string;
  photos: SurveyPhotoRecord[];
  projectId?: number;
}
