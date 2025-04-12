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
  const [error, setError] = useState<string | null>(null); 
  const [deviceId, setDeviceId] = useState<string | null>(null);

  const handleEmailSubmit = async (email: string) => {
    try {
      const response = await userEmailLogin(email);

      if (response.status === 404) {
        setError(response.error?.data || 'Пользователь с таким email не найден');
      }

      if (response.status === 'success') {
        setError(null);
        setUserEmail(email);
        setDeviceId(response.device_id);
        setStep('code');
      } else {
        setError(response.error?.data || 'Ошибка сервера');
      }
    } catch  {
      setError('Пользователя с таким email не найдено');
    }
  };

  const handleCodeSubmit = async (code: string) => {
    if (!deviceId) {
      console.error('device_id отсутствует');
      setError('Ошибка устройства');
      return;
    }

    try {
      const response = await userCodeLogin(code, deviceId);

      if (response?.status === 200) {
        navigate('/main');
      } else if (response?.status === 400 || response?.status === 404) {
        setError('Неверный код');
      } else {
        setError('Ошибка при авторизации');
      }
    } catch (err) {
      console.log('Код неверный', err);
      setError('Неверный код');
    }
  };

  return (
    <div className="auth-container">
      {step === 'email' ? (
        <EmailForm
          onEmailSubmit={handleEmailSubmit}
          serverError={error}
          onErrorClear={() => setError(null)}
        />
      ) : (
        <>
          <CodeForm
            email={userEmail}
            device_id={deviceId}
            onCodeSubmit={handleCodeSubmit}
						error={error}
          />
        </>
      )}
    </div>
  );
};