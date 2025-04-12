import React from 'react';
import { Header, Organizations } from '../components';

export const Main: React.FC = () => {
  return (
    <div className="wrapper">
      <Header />
      <Organizations />
    </div>
  );
};
