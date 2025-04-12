import { useForm } from 'react-hook-form';
import { useState } from "react";

type EmailFormData = {
    email: string;
};

type EmailFormProps = {
    onEmailSubmit: (email: string) => Promise<void>;
    serverError?: string | null;
    onErrorClear: () => void;
};

export const EmailForm = ({
                              onEmailSubmit,
                              serverError,
                              onErrorClear,
                          }: EmailFormProps) => {
    const [isSubmitting, setIsSubmitting] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors },
        reset,
    } = useForm<EmailFormData>();

    const onSubmit = async (data: EmailFormData) => {
        setIsSubmitting(true);
        onErrorClear();
        try {
            await onEmailSubmit(data.email);
            reset();
        } catch (err) {
            console.log('Error', err);
        } finally {
            setIsSubmitting(false);
        }
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
                        className={errors.email || serverError ? 'error' : ''}
                        onChange={() => serverError && onErrorClear()}
                    />

                    {errors.email && (
                        <div className="error-message">{errors.email.message}</div>
                    )}

                    {serverError && (
                        <div className="error-message">
                            {serverError}
                        </div>
                    )}
                </div>

                <button
                    type="submit"
                    className="submit-btn"
                    disabled={isSubmitting}
                >
                    {isSubmitting ? 'Отправка...' : 'Продолжить'}
                </button>
            </form>
        </div>
    );
};