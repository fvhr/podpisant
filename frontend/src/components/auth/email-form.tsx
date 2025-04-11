import { useForm } from 'react-hook-form';

type EmailFormProps = {
  onEmailSubmit: (email: string) => void;
};

type EmailFormData = {
  email: string;
};

export const EmailForm = ({ onEmailSubmit }: EmailFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EmailFormData>();

  const onSubmit = (data: EmailFormData) => {
    onEmailSubmit(data.email);
  };

  return (
    <div className="auth-form">
      <h2>Авторизация</h2>
      
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="form-group">
          <label>Email</label>
          <input
            {...register('email', {
              required: 'Обязательное поле',
              pattern: {
                value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                message: 'Некорректный email',
              },
            })}
            className={errors.email ? 'error' : ''}
          />
          {errors.email && <span className="error-message">{errors.email.message}</span>}
        </div>

        <button type="submit" className="submit-btn">
          Продолжить
        </button>
      </form>
    </div>
  );
};