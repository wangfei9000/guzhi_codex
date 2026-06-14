import type { ReactNode } from 'react';

interface Props {
  title: string;
  children: ReactNode;
}

export default function PageContainer({ title, children }: Props) {
  return (
    <div style={{ padding: 24 }}>
      <h2 style={{ marginBottom: 16, fontSize: 20, fontWeight: 600 }}>{title}</h2>
      {children}
    </div>
  );
}
