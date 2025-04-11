import { useState } from 'react';
import { CodeForm } from './code-form';
import { EmailForm } from './email-form';

export const AuthContainer = () => {
  const [step, setStep] = useState<'email' | 'code'>('email');
  const [userEmail, setUserEmail] = useState('');

  const handleEmailSubmit = (email: string) => {
    console.log('Email submitted:', email);
    setUserEmail(email);
    setStep('code');
  };

  const handleCodeSubmit = (code: string) => {
    console.log('Code submitted:', code);
  };

  return (
    <div className="auth-container">
      {step === 'email' ? (
        <EmailForm onEmailSubmit={handleEmailSubmit} />
      ) : (
        <CodeForm email={userEmail} onCodeSubmit={handleCodeSubmit} />
      )}
    </div>
  );
};
