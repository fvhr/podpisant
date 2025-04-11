import { useForm, useWatch } from 'react-hook-form';

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

  const codeValues = useWatch({
    control,
    name: 'code'
  });

  const isCodeComplete = codeValues?.every(digit => digit?.length === 1);

  const onSubmit = (data: CodeFormData) => {
    const fullCode = data.code.join('');
    onCodeSubmit(fullCode);
  };

  const handleCodeChange = (e: React.ChangeEvent<HTMLInputElement>, index: number) => {
    const value = e.target.value;
    
    if (!/^\d*$/.test(value)) return;
    
    const digit = value.slice(0, 1);
    setValue(`code.${index}`, digit);

    if (digit && index < 3) {
      const nextInput = document.getElementById(`code-${index + 1}`);
      nextInput?.focus();
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
    if (e.key === 'Backspace' || e.key === 'Delete') {
      if (e.key === 'Backspace' && index > 0 && !(e.target as HTMLInputElement).value) {
        const prevInput = document.getElementById(`code-${index - 1}`);
        prevInput?.focus();
      }
      
      setValue(`code.${index}`, '');
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
              maxLength={1}
              {...register(`code.${index}`, {
                required: true,
                pattern: /^\d$/,
              })}
              onChange={(e) => handleCodeChange(e, index)}
              onKeyDown={(e) => handleKeyDown(e, index)}
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