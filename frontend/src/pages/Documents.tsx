import React, { useState } from 'react';
import { DocumentsList, DocumentsMenu } from '../components';
import { Sidebar } from '../components/sidebar';

export const Documents: React.FC = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<'all' | 'signed' | 'canceled' | 'in-progress'>('all');

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const documents = [
    {
      id: 1,
      title: 'Договор аренды помещения',
      status: 'signed',
      date: '2023-10-15T14:30:00',
      fileUrl: '/documents/lease.pdf',
    },
    {
      id: 2,
      title: 'Дополнительное соглашение',
      status: 'in-progress',
      date: '2023-10-16T10:15:00',
      fileUrl: '/documents/addendum.pdf',
    },
    {
      id: 3,
      title: 'Акт выполненных работ',
      status: 'canceled',
      date: '2023-10-17T16:45:00',
      fileUrl: '/documents/act.pdf',
    },
  ];

  return (
    <div className="documents-page">
      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />

      <main className={`content ${isSidebarOpen ? 'with-sidebar' : 'full-width'}`}>
        <DocumentsMenu setActiveTab={setActiveTab} activeTab={activeTab} />
        <DocumentsList documents={documents} activeTab={activeTab} />
      </main>
    </div>
  );
};
