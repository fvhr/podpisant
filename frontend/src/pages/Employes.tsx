import React from 'react';
import { Header } from '../components';
import { EmployeesComponent } from '../components/employes/employes-component.tsx';

export const Employes: React.FC = () => {
    return (
        <div className="wrapper">
            <Header />
            <EmployeesComponent />
        </div>
    );
};