import React from 'react';
import ReactDOM from 'react-dom';
import { useForm } from 'react-hook-form';

interface MainModalProps {
  onClose: () => void;
  onCreate: (name: string, description: string) => void;
}

interface OrganizationFormData {
  name: string;
  description: string;
}

export const MainModal: React.FC<MainModalProps> = ({ onClose, onCreate }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<OrganizationFormData>({
    mode: 'onChange',
  });

  const onSubmit = (data: OrganizationFormData) => {
    onCreate(data.name, data.description);
    reset();
    onClose();
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  return ReactDOM.createPortal(
    <div className="org-modal-overlay" onClick={handleClose}>
      <div className="org-modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="org-modal-header">
          <h2>Создать организацию</h2>
          <button
            className="org-modal-close-button"
            onClick={handleClose}
            aria-label="Закрыть модальное окно">
            &times;
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="org-modal-form-group">
            <label htmlFor="org-modal-name">Название</label>
            <input
              id="org-modal-name"
              type="text"
              {...register('name', {
                required: 'Название обязательно',
                pattern: {
                  value: /^[a-zA-Zа-яА-ЯёЁ\s]+$/,
                  message: 'Разрешены только буквы и пробелы',
                },
                minLength: {
                  value: 6,
                  message: 'Минимум 6 символов',
                },
                maxLength: {
                  value: 50,
                  message: 'Максимум 20 символов',
                },
              })}
              placeholder="Введите название организации"
            />
            {errors.name && <span className="org-modal-error">{errors.name.message}</span>}
          </div>

          <div className="org-modal-form-group">
            <label htmlFor="org-modal-description">Описание</label>
            <textarea
              id="org-modal-description"
              {...register('description', {
                required: 'Описание обязательно',
                minLength: {
                  value: 10,
                  message: 'Минимум 10 символов',
                },
                maxLength: {
                  value: 500,
                  message: 'Максимум 500 символов',
                },
              })}
              placeholder="Введите описание организации"
              rows={5}
            />
            {errors.description && (
              <span className="org-modal-error">{errors.description.message}</span>
            )}
          </div>

          <div className="org-modal-actions">
            <button type="submit" className="org-modal-submit-button">
              Создать
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body,
  );
};
