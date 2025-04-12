	import React from 'react';
	import ReactDOM from 'react-dom';
	import { useForm } from 'react-hook-form';
	import { FiX } from 'react-icons/fi';

	interface EmployeeFormData {
		fullName: string;
		phone: string;
		email: string;
		department: string;
	}

	interface AddEmployeeModalProps {
		onClose: () => void;
		onAdd: (employee: EmployeeFormData) => void;
		departments: string[];
	}

	export const AddEmployeeModal: React.FC<AddEmployeeModalProps> = ({
		onClose,
		onAdd,
		departments,
	}) => {
		const {
			register,
			handleSubmit,
			formState: { errors },
		} = useForm<EmployeeFormData>();

		const onSubmit = (data: EmployeeFormData) => {
			onAdd(data);
			onClose();
		};

		return ReactDOM.createPortal(
			<div className="employee-modal-overlay" onClick={onClose}>
				<div className="employee-modal-content" onClick={(e) => e.stopPropagation()}>
					<div className="employee-modal-header">
						<h2>Добавить сотрудника</h2>
						<button className="employee-modal-close-button" onClick={onClose}>
							<FiX />
						</button>
					</div>

					<form onSubmit={handleSubmit(onSubmit)}>
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
							/>
							{errors.phone && <span className="employee-modal-error">{errors.phone.message}</span>}
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
							/>
							{errors.email && <span className="employee-modal-error">{errors.email.message}</span>}
						</div>

						<div className="employee-modal-form-group">
							<label htmlFor="department">Отдел</label>
							<select id="department" {...register('department', { required: 'Выберите отдел' })}>
								<option value="">Выберите отдел</option>
								{departments.map((dept) => (
									<option key={dept} value={dept}>
										{dept}
									</option>
								))}
							</select>
							{errors.department && (
								<span className="employee-modal-error">{errors.department.message}</span>
							)}
						</div>

						<div className="employee-modal-actions">
							<button type="submit" className="submit-button">
								Добавить сотрудника
							</button>
						</div>
					</form>
				</div>
			</div>,
			document.body,
		);
	};
