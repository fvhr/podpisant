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

      // Предполагаем, что успешный ответ содержит device_id
      if (response.device_id) {
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
      const result = await userCodeLogin(code, deviceId);
      console.log('Login result:', result);

      if (result?.success) {
        navigate('/main');
      } else {
        setError('Неверный код');
      }
    } catch (error) {
      console.error('Auth error:', error);
      setError('Ошибка при авторизации');
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
