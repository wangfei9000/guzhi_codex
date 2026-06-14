import { Navigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { Spin } from 'antd';
import type { ReactNode } from 'react';

interface Props {
  children: ReactNode;
  requiredRoles?: string[];
}

export default function ProtectedRoute({ children, requiredRoles }: Props) {
  const { token, isInitialized } = useAuthStore();
  const location = useLocation();

  if (!token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (!isInitialized) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" tip="加载中..." />
      </div>
    );
  }

  if (requiredRoles && requiredRoles.length > 0) {
    const { permissions } = useAuthStore.getState();
    const hasRole = requiredRoles.some((role) => permissions.includes(role));
    if (!hasRole) {
      return <Navigate to="/403" replace />;
    }
  }

  return <>{children}</>;
}
