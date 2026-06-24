import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { useEffect } from 'react';
import AdminLayout from '@/layouts/AdminLayout';
import ProtectedRoute from '@/components/common/ProtectedRoute';
import LoginPage from '@/pages/LoginPage';
import DashboardPage from '@/pages/DashboardPage';
import UserListPage from '@/pages/user/UserListPage';
import OrganizationListPage from '@/pages/organization/OrganizationListPage';
import RoleListPage from '@/pages/role/RoleListPage';
import PermissionPage from '@/pages/role/PermissionPage';
import FileUploadPage from '@/pages/file/FileUploadPage';
import NotificationPage from '@/pages/NotificationPage';
import ChatPage from '@/pages/chat/ChatPage';
import ScheduleListPage from '@/pages/schedule/ScheduleListPage';
import ProjectListPage from '@/pages/project/ProjectListPage';
import ProjectCreatePage from '@/pages/project/ProjectCreatePage';
import ProjectValuationPage from '@/pages/project/ProjectValuationPage';
import OrderListPage from '@/pages/project/OrderListPage';
import ReportListPage from '@/pages/report/ReportListPage';
import ReportTemplateListPage from '@/pages/report/ReportTemplateListPage';
import SealListPage from '@/pages/seal/SealListPage';
import SurveyListPage from '@/pages/survey/SurveyListPage';
import ManualValuationPage from '@/pages/bank/ManualValuationPage';
import AutomaticValuationPage from '@/pages/bank/AutomaticValuationPage';
import ValuationListPage from '@/pages/bank/ValuationListPage';
import BankValuationDetailPage from '@/pages/bank/BankValuationDetailPage';
import RevaluationPage from '@/pages/bank/RevaluationPage';
import RevaluationListPage from '@/pages/bank/RevaluationListPage';
import ReconciliationPage from '@/pages/bank/ReconciliationPage';
import MyAssistantPage from '@/pages/assistant/MyAssistantPage';
import SurveyMobilePage from '@/pages/app/SurveyMobilePage';
import ForbiddenPage from '@/pages/error/ForbiddenPage';

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <AdminLayout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: 'dashboard', element: <DashboardPage /> },
      { path: 'system/user', element: <UserListPage /> },
      { path: 'system/organization', element: <OrganizationListPage /> },
      { path: 'system/role', element: <RoleListPage /> },
      { path: 'system/permission', element: <PermissionPage /> },
      { path: 'file/manage', element: <FileUploadPage /> },
      { path: 'notification', element: <NotificationPage /> },
      { path: 'chat', element: <ChatPage /> },
      { path: 'schedule/list', element: <ScheduleListPage /> },
      { path: 'project/list', element: <ProjectListPage /> },
      { path: 'project/create', element: <ProjectCreatePage /> },
      { path: 'project/:projectId/valuation', element: <ProjectValuationPage /> },
      { path: 'order/list', element: <OrderListPage /> },
      { path: 'report/list', element: <ReportListPage /> },
      { path: 'report/template-list', element: <ReportTemplateListPage /> },
      { path: 'seal/list', element: <SealListPage /> },
      { path: 'survey/list', element: <SurveyListPage /> },
      { path: 'bank/manual-valuation', element: <ManualValuationPage /> },
      { path: 'bank/auto-valuation', element: <AutomaticValuationPage /> },
      { path: 'bank/valuation-list', element: <ValuationListPage /> },
      { path: 'bank/valuation-detail/:projectId', element: <BankValuationDetailPage /> },
      { path: 'bank/revaluation', element: <RevaluationPage /> },
      { path: 'bank/revaluation-list', element: <RevaluationListPage /> },
      { path: 'bank/reconciliation', element: <ReconciliationPage /> },
      { path: 'assistant', element: <MyAssistantPage /> },
    ],
  },
  {
    path: '/app',
    element: (
      <ProtectedRoute>
        <SurveyMobilePage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/403',
    element: <ForbiddenPage />,
  },
  {
    path: '*',
    element: <Navigate to="/dashboard" replace />,
  },
]);

export default function App() {
  const { token, fetchUserInfo, isInitialized } = useAuthStore();

  useEffect(() => {
    if (token && !isInitialized) {
      fetchUserInfo();
    }
  }, [token, isInitialized, fetchUserInfo]);

  return <RouterProvider router={router} />;
}
