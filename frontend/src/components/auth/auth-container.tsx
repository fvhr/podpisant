import { useState } from 'react';
import { CodeForm } from './code-form';
import { EmailForm } from './email-form';
import { useNavigate } from "react-router-dom";
import { userEmailLogin } from "../../api/user.ts";
import { userCodeLogin } from "../../api/user.ts";

export const AuthContainer = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState<'email' | 'code'>('email');
  const [userEmail, setUserEmail] = useState('');
  const [emailError, setEmailError] = useState<string | null>(null);
  const [deviceId, setDeviceId] = useState<string | null>(null);

  const handleEmailSubmit = async (email: string) => {
    try {
      const response = await userEmailLogin(email);

      if (response.status === 404) {
        setEmailError(response.error?.data || 'Пользователь с таким email не найден');
        throw new Error('User not found');
      }

      if (response.status === 'success') {
        setEmailError(null);
        setUserEmail(email);
        setDeviceId(response.device_id);
        setStep('code');
      } else {
        setEmailError(response.error?.data || 'Ошибка сервера');
      }
    } catch (err) {
      console.error(err);
      setEmailError('Пользователя с таким email не найдено');
    }
  };

  const handleCodeSubmit = async (code: string) => {
    if (!deviceId) {
      console.error('device_id отсутствует');
      return;
    }

    try {
      const response = await userCodeLogin(code, deviceId);
      if (!response) {
        setEmailError('Ответ от сервера не получен');
        return;
      }

      console.log(response);

      if (response.status === 200) {
        console.log('Авторизация прошла успешно');
        navigate('/main');
      } else {
        setEmailError('Ошибка при авторизации с кодом');
      }
    } catch (err) {
      console.error('Ошибка при авторизации', err);
      setEmailError('Ошибка при авторизации');
    }
  };

  return (
      <div className="auth-container">
        {step === 'email' ? (
            <EmailForm
                onEmailSubmit={handleEmailSubmit}
                serverError={emailError}
                onErrorClear={() => setEmailError(null)}
            />
        ) : (
            <CodeForm
                email={userEmail}
                device_id={deviceId}
                onCodeSubmit={handleCodeSubmit}
            />
        )}
      </div>
  );
};