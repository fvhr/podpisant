import React from 'react';
import ReactDOM from 'react-dom';
import { useForm } from 'react-hook-form';
import { FiChevronDown, FiChevronUp, FiX } from 'react-icons/fi';
import { useParams } from 'react-router-dom';
import { addStage } from '../../api/stage';

interface Employee {
  id: number;
  fio: string;
  phone: string;
  email: string;
  type_notification: string;
  is_dep_admin: boolean;
  id_dep: number;
}

interface StageFormData {
  name: string;
  stageNumber: number;
  deadline: string;
  employees: string[];
  user_ids: string[];
}

interface CreateStageModalProps {
  onClose: () => void;
  onCreate: () => void;
  employees: Employee[];
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
      stageNumber: 1,
      employees: [],
    },
  });

  const [isEmployeesOpen, setIsEmployeesOpen] = React.useState(false);
  const selectedEmployees = watch('employees') || [];
  const { docId } = useParams();
  const documentId = Number(docId);

  const toggleEmployee = (employee: string) => {
    const newSelection = selectedEmployees.includes(employee)
      ? selectedEmployees.filter((e) => e !== employee)
      : [...selectedEmployees, employee];
    setValue('employees', newSelection);
  };

  console.log(selectedEmployees);

  const onSubmit = async (data: StageFormData) => {
    try {
      const stageData = {
        name: data.name,
        stageNumber: data.stageNumber,
        deadline: new Date(data.deadline),
        user_ids: selectedEmployees,
        employees: data.employees, // Сохраняем для формы
      };

      await addStage(
        documentId,
        stageData.name,
        stageData.stageNumber,
        stageData.deadline,
        stageData.user_ids,
      );
      onCreate();
      onClose();
    } catch (error) {
      console.error('Ошибка при создании этапа:', error);
    }
  };

  const getContainerHeight = () => {
    const itemHeight = 44;
    const maxVisibleItems = 4;
    const maxHeight = itemHeight * maxVisibleItems;

    return employees.length > maxVisibleItems ? `${maxHeight}px` : 'auto';
  };

  return ReactDOM.createPortal(
    <div className="doc-modal-overlay" onClick={onClose}>
      <div className="doc-modal-container" onClick={(e) => e.stopPropagation()}>
        <div className="doc-modal-header">
          <h2 className="doc-modal-title">Создать новый этап</h2>
          <button className="doc-modal-close-btn" onClick={onClose}>
            <FiX />
          </button>
        </div>

        <form className="doc-modal-form" onSubmit={handleSubmit(onSubmit)}>
          <div className="doc-form-field">
            <label className="doc-form-label">Название этапа*</label>
            <input
              className="doc-form-input"
              {...register('name', { required: 'Обязательное поле' })}
              placeholder="Введите название этапа"
            />
            {errors.name && <span className="doc-form-error">{errors.name.message}</span>}
          </div>

          <div className="doc-form-row">
            <div className="doc-form-field">
              <label className="doc-form-label">Номер этапа*</label>
              <select
                className="doc-form-input"
                {...register('stageNumber', { required: true, min: 1, max: 10 })}>
                {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((num) => (
                  <option key={num} value={num}>
                    Этап {num}
                  </option>
                ))}
              </select>
            </div>

            <div className="doc-form-field">
              <label className="doc-form-label">Дедлайн*</label>
              <input
                type="date"
                className="doc-form-input"
                {...register('deadline', { required: 'Укажите дедлайн' })}
                min={new Date().toISOString().split('T')[0]}
              />
              {errors.deadline && <span className="doc-form-error">{errors.deadline.message}</span>}
            </div>
          </div>

          <div className="doc-form-field">
            <label className="doc-form-label">Ответственные*</label>
            <div
              className="doc-multi-select-header"
              onClick={() => setIsEmployeesOpen(!isEmployeesOpen)}>
              <span>
                {selectedEmployees.length > 0
                  ? `Выбрано: ${selectedEmployees.length}`
                  : 'Выберите сотрудников'}
              </span>
              {isEmployeesOpen ? <FiChevronUp /> : <FiChevronDown />}
            </div>

            {isEmployeesOpen && (
              <div
                className="doc-multi-select-container"
                style={{ maxHeight: getContainerHeight() }}>
                {employees?.map((employee) => (
                  <label key={employee.id} className="doc-select-option">
                    <input
                      type="checkbox"
                      checked={selectedEmployees.includes(employee.id.toString())}
                      onChange={() => toggleEmployee(employee.id.toString())}
                      className="doc-employee-checkbox"
                    />
                    <span className="doc-employee-name">{employee.fio}</span>
                  </label>
                ))}
              </div>
            )}
            <input
              type="hidden"
              {...register('employees', {
                validate: (value) => value.length > 0 || 'Выберите хотя бы одного сотрудника',
              })}
            />
            {errors.employees && <span className="doc-form-error">{errors.employees.message}</span>}
          </div>

          <div className="doc-form-actions">
            <button type="submit" className="doc-form-submit-btn">
              Создать этап
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body,
  );
};
