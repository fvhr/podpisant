import React from 'react';
import { MainHeader, MainOrganizations } from '../components';

export const Main: React.FC = () => {
  return (
    <div className="wrapper">
      <MainHeader />
      <MainOrganizations />
    </div>
  );
};
