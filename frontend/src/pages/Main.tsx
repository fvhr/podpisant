import React from 'react';
import '../styles/app.scss';
import { Header } from '../components';
import { Organizations } from "../components/main";

export const Main: React.FC = () => {
    return (
        <>
            <Header />
            <Organizations />
        </>
    );
};