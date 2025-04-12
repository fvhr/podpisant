import { useForm, useWatch } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';

type CodeFormProps = {
    email: string,
    onCodeSubmit: (code: string, device_id: string | null) => void,
    device_id: string | null,
};

type CodeFormData = {
    code: string[];
};

export const CodeForm = ({email, onCodeSubmit, device_id}: CodeFormProps) => {
    const {register, handleSubmit, setValue, control} = useForm<CodeFormData>({
        defaultValues: {
            code: ['', '', '', ''],
        },
    });

    const codeValues = useWatch({
        control,
        name: 'code',
    });

    const isCodeComplete = codeValues?.every((digit) => digit?.length === 1);

    const onSubmit = (data: CodeFormData) => {
        const fullCode = data.code.join('');
        onCodeSubmit(fullCode, device_id);
    };

  const handleCodeChange = (e: React.ChangeEvent<HTMLInputElement>, index: number) => {
    const value = e.target.value.replace(/\D/g, ''); // Удаляем все не-цифры
    const digit = value.slice(0, 1); // Берем только первую цифру
    
    setValue(`code.${index}`, digit);
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

                <button type="submit" className="submit-btn" disabled={!isCodeComplete}>
                    Подтвердить
                </button>
            </form>
        </div>
    );
};