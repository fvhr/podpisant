import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userCodeLogin, userEmailLogin } from '../../api/user.ts';
import { LoginCode } from './login-code.tsx';
import { LoginEmail } from './login-email.tsx';

export const LoginContainer = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState<'email' | 'code'>('email');
  const [userEmail, setUserEmail] = useState('');
  const [error, setError] = useState<string | null>('');
  const [deviceId, setDeviceId] = useState<string>('');

  const handleEmailSubmit = async (email: string) => {
    try {
      const response = await userEmailLogin(email);

      if (response.status === 'success') {
        setError(null);
        setUserEmail(email);
        setDeviceId(response.device_id);
        setStep('code');
      } else {
        setError(response.error?.data || 'Ошибка сервера');
      }
    } catch {
      setError('Пользователя с таким email не найдено');
    }
  };

  const handleCodeSubmit = async (code: string) => {
    try {
      const response = await userCodeLogin(code, deviceId);

      if (response?.status === 200) {
        navigate('/main');
      } else if (response?.status === 400 || response?.status === 404) {
        setError('Неверный код');
      } else {
        setError('Ошибка при авторизации');
      }
    } catch {
      setError('Неверный код');
    }
  };

  return (
    <div className="auth-container">
      {step === 'email' ? (
        <LoginEmail
          onEmailSubmit={handleEmailSubmit}
          serverError={error}
          onErrorClear={() => setError(null)}
        />
      ) : (
        <>
          <LoginCode
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
