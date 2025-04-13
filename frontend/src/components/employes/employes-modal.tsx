import React from 'react';
import ReactDOM from 'react-dom';
import { useForm } from 'react-hook-form';
import { FiX } from 'react-icons/fi';

interface Department {
  id: number;
  name: string;
}

interface EmployeeFormData {
  id_dep: number;
  fullName: string;
  phone: string;
  email: string;
  type_notification: string;
  isAdmin: boolean;
}

interface AddEmployeeModalProps {
  onClose: () => void;
  onAdd: (data: {
    id_dep: number;
    fullName: string;
    phone: string;
    email: string;
    typeNotification: string;
    isAdmin: boolean;
  }) => Promise<void>;
  isLoading: boolean;
  departments: Department[];
}

const notificationTypes = [
  { value: 'TG', label: 'Telegram' },
  { value: 'EMAIL', label: 'Email' },
  { value: 'PHONE', label: 'SMS' },
];

export const AddEmployeeModal: React.FC<AddEmployeeModalProps> = ({
  onClose,
  onAdd,
  isLoading,
  departments,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setValue,
    reset,
  } = useForm<EmployeeFormData>({
    defaultValues: {
      type_notification: 'EMAIL',
      isAdmin: false,
      id_dep: departments[0]?.id || 0,
    },
  });

  const isAdmin = watch('isAdmin');

  const onSubmit = async (data: EmployeeFormData) => {
    await onAdd({
      id_dep: data.id_dep,
      fullName: data.fullName,
      phone: data.phone,
      email: data.email,
      typeNotification: data.type_notification,
      isAdmin: data.isAdmin,
    });
    reset();
  };

  const toggleAdminStatus = () => {
    setValue('isAdmin', !isAdmin);
  };

  return ReactDOM.createPortal(
    <div className="employee-modal-overlay" onClick={onClose}>
      <div className="employee-modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="employee-modal-header">
          <h2>Добавить сотрудника</h2>
          <button className="employee-modal-close-button" onClick={onClose} disabled={isLoading}>
            <FiX />
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-columns">
            <div className="form-column">
              <div className="employee-modal-form-group">
                <label htmlFor="fullName">ФИО</label>
                <input
                  id="fullName"
                  type="text"
                  {...register('fullName', {
                    required: 'ФИО обязательно',
                    minLength: {
                      value: 5,
                      message: 'Минимум 5 символов',
                    },
                  })}
                  placeholder="Введите ФИО сотрудника"
                  disabled={isLoading}
                />
                {errors.fullName && (
                  <span className="employee-modal-error">{errors.fullName.message}</span>
                )}
              </div>

              <div className="employee-modal-form-group">
                <label htmlFor="phone">Телефон</label>
                <input
                  id="phone"
                  type="tel"
                  {...register('phone', {
                    required: 'Телефон обязателен',
                    pattern: {
                      value: /^\+?[0-9\s-]+$/,
                      message: 'Введите корректный номер телефона',
                    },
                  })}
                  placeholder="+7 XXX XXX XX XX"
                  disabled={isLoading}
                />
                {errors.phone && (
                  <span className="employee-modal-error">{errors.phone.message}</span>
                )}
              </div>

              <div className="employee-modal-form-group">
                <label htmlFor="email">Email</label>
                <input
                  id="email"
                  type="email"
                  {...register('email', {
                    required: 'Email обязателен',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Введите корректный email',
                    },
                  })}
                  placeholder="email@example.com"
                  disabled={isLoading}
                />
                {errors.email && (
                  <span className="employee-modal-error">{errors.email.message}</span>
                )}
              </div>
            </div>

            <div className="form-column">
              <div className="employee-modal-form-group">
                <label htmlFor="department">Отдел</label>
                <select
                  id="department"
                  {...register('id_dep', { required: 'Выберите отдел' })}
                  disabled={isLoading}
                  className="department-select">
                  {departments.map((dept) => (
                    <option key={dept.id} value={dept.id}>
                      {dept.name}
                    </option>
                  ))}
                </select>
                {errors.id_dep && (
                  <span className="employee-modal-error">{errors.id_dep.message}</span>
                )}
              </div>

              <div className="employee-modal-form-group">
                <label>Предпочтительный тип связис</label>
                <div className="employee-notification-options">
                  {notificationTypes.map((type) => (
                    <label key={type.value} className="employee-notification-option">
                      <input
                        type="radio"
                        value={type.value}
                        {...register('type_notification', { required: true })}
                        disabled={isLoading}
                      />
                      <span className="employee-option-label">{type.label}</span>
                    </label>
                  ))}
                </div>
              </div>

              <div className="employee-modal-form-group">
                <label>Администратор отдела</label>
                <div
                  className="employee-admin-toggle"
                  onClick={() => !isLoading && toggleAdminStatus()}>
                  <div className={`employee-toggle-switch ${isAdmin ? 'active' : ''}`}>
                    <div className="employee-toggle-knob" />
                  </div>
                  <span className="employee-toggle-label">{isAdmin ? 'Да' : 'Нет'}</span>
                </div>
              </div>
            </div>
          </div>

          <div className="employee-modal-actions">
            <button type="submit" className="employee-submit-button" disabled={isLoading}>
              {isLoading ? 'Добавление...' : 'Добавить сотрудника'}
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body,
  );
};
