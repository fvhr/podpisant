import React from 'react';
import '../styles/app.scss';
import { AiFillPlusCircle } from "react-icons/ai";

export const Main: React.FC = () => {
    return (
        <main className="main">
            <button className="main__button">
                <AiFillPlusCircle />
                Добавить организацию
            </button>
        </main>
    );
};