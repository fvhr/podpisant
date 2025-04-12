import React from 'react';
import ReactDOM from 'react-dom';
import { useForm } from 'react-hook-form';
import { FiUpload, FiX } from 'react-icons/fi';

interface DocumentModalProps {
  onClose: () => void;
  onCreate: (name: string, file: File) => void;
}

interface DocumentFormData {
  name: string;
  document: FileList;
}

export const DocumentModal: React.FC<DocumentModalProps> = ({ onClose, onCreate }) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    reset,
  } = useForm<DocumentFormData>();

  const fileName = watch('document')?.[0]?.name;

  const onSubmit = (data: DocumentFormData) => {
    onCreate(data.name, data.document[0]);
    reset();
    onClose();
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  return ReactDOM.createPortal(
    <div className="document-modal-overlay" onClick={handleClose}>
      <div className="document-modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="document-modal-header">
          <h2>Создать документ</h2>
          <button className="document-modal-close-button" onClick={handleClose}>
            <FiX />
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="document-modal-form-group">
            <label className="document-modal-name" htmlFor="document-name">Название документа</label>
            <input
              id="document-name"
              type="text"
              {...register('name', {
                required: 'Название обязательно',
                minLength: {
                  value: 3,
                  message: 'Минимум 3 символа',
                },
                maxLength: {
                  value: 100,
                  message: 'Максимум 100 символов',
                },
              })}
              placeholder="Введите название документа"
            />
            {errors.name && <span className="document-modal-error">{errors.name.message}</span>}
          </div>

          <div className="document-modal-form-group">
            <label htmlFor="document-file" className="file-upload-label">
              <FiUpload className="upload-icon" />
              <span>{fileName || 'Выберите файл'}</span>
              <input
                id="document-file"
                type="file"
                accept=".pdf,.doc,.docx,.xls,.xlsx"
                {...register('document', {
                  required: 'Файл обязателен',
                })}
              />
            </label>
            {errors.document && (
              <span className="document-modal-error">{errors.document.message}</span>
            )}
          </div>

          <div className="document-modal-actions">
            <button type="submit" className="submit-button">
              Создать документ
            </button>
          </div>
        </form>
      </div>
    </div>,
    document.body,
  );
};
