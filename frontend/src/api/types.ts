export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

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
  organizationId?: number | null;
  organizationName?: string | null;
  organizationType?: string | null;
  roles: RoleInfo[];
}

export interface OrganizationRecord {
  id: number;
  organizationType: string;
  organizationName: string;
  contactName: string;
  contactPhone: string;
  reportTemplateId?: number | null;
  reportTemplateName?: string | null;
  createdAt?: string;
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

export interface NotificationItem {
  id: number;
  userId: number;
  senderId: number | null;
  title: string;
  content: string;
  isRead: boolean;
  createdAt: string;
}

export interface ChatUser {
  id: number;
  username: string;
  nickname: string | null;
  online: boolean;
  unreadCount: number;
  lastMessage: string | null;
  lastSenderId: number | null;
  lastMessageTime: string | null;
}

export interface ChatMessage {
  id: number;
  senderId: number;
  recipientId: number;
  content: string;
  isRead: boolean;
  createdAt: string;
}

export interface ChatPresenceEvent {
  userId: number;
  online: boolean;
}

export interface ScheduleRecord {
  id: number;
  registrationDate: string;
  code: string;
  reportNo: string;
  orderTaker: string;
  agency: string;
  reporter: string;
  reporterPhone: string;
  contact: string;
  contactPhone: string;
  customerService: string;
  projectAddress: string;
  surveyor: string;
  appraiser: string;
  status: string;
  unitPrice: number;
  totalPrice: number;
  createdAt: string;
}

export interface FileRecordItem {
  id: number;
  originalName: string;
  storedName: string;
  filePath: string;
  fileSize: number;
  contentType: string;
  uploadUserId: number;
  createdAt: string;
}

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
  clientName: string;
  clientContact: string;
  clientPhone: string;
  mortgagorName: string;
  mortgagorIdCard: string;
  mortgagorPhone: string;
  borrowerName: string;
  borrowerIdCard: string;
  valuationPurpose: string;
  valuationTime: string;
  expectedPrice: number;
  valuationUnitPrice: number;
  valuationTotalPrice: number;
  valuationType: string;
  buildingArea: number;
  status: string;
  remark: string;
  createdAt: string;
}

export interface CollateralRecord {
  id: number;
  collateralCode: string;
  collateralType: string;
  collateralName: string;
  collateralAddress: string;
  primaryCollateral: boolean;
  actualUse: string;
  occupancyStatus: string;
  decoration: string;
  orientation: string;
  currentFloor: string;
  indoorHeight: string;
  spaceLayout: string;
  facilitiesCondition: string;
  maintenanceCondition: string;
  parcelShape: string;
  terrain: string;
  landLevel: string;
  soilCondition: string;
  landDevelopmentLevel: string;
  landscape: string;
  surroundingEnvironment: string;
  buildingArea: number;
  landArea: number;
  communityName: string;
  building: string;
  unitName: string;
  doorNumber: string;
  buildYear: number;
  constructionLand: number;
  landAcquisition: number;
  floorAreaRatio: number;
  aboveGroundRatio: number;
  civilDefenseArea: number;
  undergroundRatio: number;
  greeningRate: number;
  buildingDensity: number;
  buildingHeight: number;
  floorCount: number;
  householdCount: number;
  parkingCount: number;
  parkingRatio: number;
  completionDate: string;
  propertyRightsYears: number;
  landUseYears: number;
}

export interface ValuationMethodRecord {
  id: number;
  methodCode: string;
  methodName: string;
  weight: number;
  unitPrice: number;
  appraiserSignature: string;
  description: string;
  reportId: number;
}

export interface ReportReviewRecord {
  id: number;
  reportId: number;
  reviewer: string;
  reviewDate: string;
  reviewOpinion: string;
  reviewResult: string;
}

export interface ValuationReportRecord {
  id: number;
  reportCode: string;
  startTime: string;
  endTime: string;
  unitPrice: number;
  totalPrice: number;
  mortgageValue: number;
  priorityCompensationAmount: number;
  priorityCompensationDescription: string;
  valueDate: string;
  reportIssueDate: string;
  validStartDate: string;
  validEndDate: string;
  valuer1Name: string;
  valuer1CertNo: string;
  valuer2Name: string;
  valuer2CertNo: string;
  valuationResult: string;
  areaEvaluation: string;
  surroundingTransactions: string;
  liquidityAnalysis: string;
  floorPlan: string;
  landGrantDeduction: number;
  decorationNewRate: number;
  equipmentNewRate: number;
  reportUrl: string;
  bankSuggestion: string;
  landPlot: string;
  valuationMethods: ValuationMethodRecord[];
  reportReviews: ReportReviewRecord[];
}

export interface SurveyPhotoRecord {
  id: number;
  surveyId: number;
  photoCode: string;
  photoPath: string;
  photoCategory: string;
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

export interface OwnershipInfoRecord {
  id?: number;
  rightHolder?: string;
  rightCertificateNumber?: string;
  registeredAddress?: string;
  borrowerName?: string;
  borrowerIdCard?: string;
  buildingStructure?: string;
  usage?: string;
  actualUse?: string;
  decoration?: string;
  registeredBuildingArea?: number;
  currentFloor?: string;
  totalFloors?: number;
  rightNature?: string;
  rightType?: string;
  rightStatus?: string;
  rightRegistrationDate?: string;
  rightCancellationDate?: string;
  coOwnership?: string;
  landUseYears?: number;
  propertyUnitNumber?: string;
  propertySource?: string;
  sharedLandArea?: number;
  allocatedLandArea?: number;
  buildYear?: number;
  buildYearSource?: string;
  onlineSigningDate?: string;
  contractNumber?: string;
  reportIssueDate?: string;
  valuationTimePoint?: string;
  oldCommunityRenovation?: boolean;
  areaProsperity?: string;
  marketProsperity?: string;
  houseOwnershipCertificate?: string;
  stateLandUseCertificateNumber?: string;
  landUse?: string;
  landUseRightSource?: string;
  landUseStartDate?: string;
  landUseEndDate?: string;
  qiuQuanNumber?: string;
  landUseArea?: number;
  mortgageInfo?: string;
  seizureInfo?: string;
  leaseRestriction?: string;
  otherRightsInfo?: string;
  remark?: string;
}

export interface ProjectDetail {
  project: ProjectRecord;
  collaterals: CollateralRecord[];
  valuationReports: ValuationReportRecord[];
  surveys: SurveyRecord[];
  ownershipInfo: OwnershipInfoRecord | null;
}

export interface SurveyListRecord {
  surveyId: number;
  surveyCode: string;
  code: string;
  surveyStatus: string;
  projectCode: string;
  surveyor: string;
  receptionist: string;
  receptionistPhone: string;
  surveyDate: string;
  startTime: string;
  endTime: string;
  propertyCertVerified: boolean;
  ownershipDispute: string;
  remark: string;
  projectAddress: string;
  projectId: number;
}

export interface SealListRecord {
  sealId: number;
  reportId: number;
  reportCode: string;
  projectCode: string;
  sealedReportUrl: string;
  sealer: string;
  sealDate: string;
  projectStatus: string;
}

export interface ReportListRecord {
  reportId: number;
  reportCode: string;
  projectCode: string;
  startTime: string;
  endTime: string;
  unitPrice: number;
  collateralAddress: string;
  buildingArea: number;
  valuationResult: string;
  projectStatus: string;
  reportUrl: string;
}

export interface ReportTemplateRecord {
  id: number;
  templateName: string;
  templateContent: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface DashboardStats {
  userCount: number;
  projectCount: number;
  fileCount: number;
  unreadNotificationCount: number;
}
