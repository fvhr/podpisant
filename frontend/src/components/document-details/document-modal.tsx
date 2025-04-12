import React from 'react';
import ReactDOM from 'react-dom';
import { useForm } from 'react-hook-form';
import { FiX } from 'react-icons/fi';

interface StageFormData {
  name: string;
  employees: string[];
}

interface CreateStageModalProps {
  onClose: () => void;
  onCreate: (stage: StageFormData) => void;
  employees: string[];
}

export const CreateStageModal: React.FC<CreateStageModalProps> = ({
  onClose,
  onCreate,
  employees,
}) => {
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<StageFormData>({
    defaultValues: {
      employees: [],
    },
  });

  const selectedEmployees = watch('employees') || [];

  const toggleEmployee = (employee: string) => {
    const newSelection = selectedEmployees.includes(employee)
      ? selectedEmployees.filter((e) => e !== employee)
      : [...selectedEmployees, employee];
    setValue('employees', newSelection);
  };

  const onSubmit = (data: StageFormData) => {
    onCreate(data);
    onClose();
  };

  return ReactDOM.createPortal(
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">Создать новый этап</h2>
          <button className="modal-close-btn" onClick={onClose}>
            <FiX />
          </button>
        </div>

        <form className="modal-form" onSubmit={handleSubmit(onSubmit)}>
          <div className="form-field">
            <label className="form-label">Название этапа*</label>
            <input
              className="form-input"
              {...register('name', { required: 'Обязательное поле' })}
              placeholder="Введите название этапа"
            />
            {errors.name && <span className="form-error">{errors.name.message}</span>}
          </div>

          <div className="form-field">
            <label className="form-label">Ответственные*</label>
            <div className="multi-select-container">
              {employees.map((employee) => (
                <div
                  key={employee}
                  className={`select-option ${
                    selectedEmployees.includes(employee) ? 'selected' : ''
                  }`}
                  onClick={() => toggleEmployee(employee)}>
                  {employee}
                  {selectedEmployees.includes(employee) && <span className="checkmark">✓</span>}
                </div>
              ))}
            </div>
            <input
              type="hidden"
              {...register('employees', {
                validate: (value) => value.length > 0 || 'Выберите хотя бы одного сотрудника',
              })}
            />
            {errors.employees && <span className="form-error">{errors.employees.message}</span>}
          </div>

          <div className="form-actions">
            <button type="submit" className="form-submit-btn">
              Создать этап
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body,
  );
};
