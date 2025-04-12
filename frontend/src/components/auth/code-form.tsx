import { useForm, useWatch } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';

type CodeFormProps = {
  email: string;
  onCodeSubmit: (code: string) => void;
};

type CodeFormData = {
  code: string[];
};

export const CodeForm = ({ email, onCodeSubmit }: CodeFormProps) => {
  const { register, handleSubmit, setValue, control } = useForm<CodeFormData>({
    defaultValues: {
      code: ['', '', '', '']
    }
  });
  const navigate = useNavigate();

  const codeValues = useWatch({
    control,
    name: 'code'
  });

  const isCodeComplete = codeValues?.every(digit => digit?.length === 1);

  const onSubmit = (data: CodeFormData) => {
    const fullCode = data.code.join('');
    navigate('/main');
    onCodeSubmit(fullCode);
  };

  const handleCodeChange = (e: React.ChangeEvent<HTMLInputElement>, index: number) => {
    const value = e.target.value.replace(/\D/g, ''); // Удаляем все не-цифры
    const digit = value.slice(0, 1); // Берем только первую цифру
    
    setValue(`code.${index}`, digit);

    if (digit && index < 3) {
      const nextInput = document.getElementById(`code-${index + 1}`);
      nextInput?.focus();
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
    if (!/[0-9]|Backspace|Delete|Tab|ArrowLeft|ArrowRight/.test(e.key)) {
      e.preventDefault();
      return;
    }

    if (e.key === 'Backspace' || e.key === 'Delete') {
      if (e.key === 'Backspace' && index > 0 && !(e.target as HTMLInputElement).value) {
        const prevInput = document.getElementById(`code-${index - 1}`);
        prevInput?.focus();
      }
      
      setValue(`code.${index}`, '');
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const pasteData = e.clipboardData.getData('text').replace(/\D/g, '');
    if (pasteData.length === 4) {
      const digits = pasteData.split('');
      digits.forEach((digit, i) => {
        if (i < 4) {
          setValue(`code.${i}`, digit);
        }
      });
      document.getElementById(`code-3`)?.focus();
    }
  };

  return (
    <div className="auth-form">
      <h2>Введите код из письма</h2>
      <p className="email-notice">Код отправлен на {email}</p>

      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="code-inputs">
          {[0, 1, 2, 3].map((index) => (
            <input
              key={index}
              id={`code-${index}`}
              type="text"
              inputMode="numeric"
              pattern="[0-9]*"
              maxLength={1}
              {...register(`code.${index}`, {
                required: true,
                pattern: /^\d$/,
              })}
              onChange={(e) => handleCodeChange(e, index)}
              onKeyDown={(e) => handleKeyDown(e, index)}
              onPaste={handlePaste}
              className="code-input"
              autoFocus={index === 0}
            />
          ))}
        </div>

        <button 
          type="submit" 
          className="submit-btn"
          disabled={!isCodeComplete}
        >
          Подтвердить
        </button>
      </form>
    </div>
  );
};