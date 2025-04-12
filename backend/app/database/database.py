import re
from typing import Type, Tuple, Dict, Any, Union, Sequence
from datetime import datetime
from sqlalchemy import text, delete, update, select, RowMapping

from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import declarative_base
from app.connections.pgsql.utils import convert_to_moscow_time
from app.connections.pgsql.session_manager import (
    DatabaseSessionManager,
    db_manager,
)

# Базовый класс для моделей SQLAlchemy
Base = declarative_base()


class DatabaseManager:
    def __init__(self, session_manager: DatabaseSessionManager):
        self.session_manager = session_manager
        self.session_maker = self.session_manager.session

    async def create_my_object(
        self,
        database_model: Type[Base],
        data: Dict[str, Any],
        print_exception: bool = True,
    ) -> Tuple[int, Base | str]:
        """
        Создает объект в базе данных.

        :param database_model: Модель SQLAlchemy.
        :param data: Данные для создания объекта.
        :param print_exception: Вывод ошибки в консоль(для API ставится False)
        :return: Кортеж (код статуса, созданный объект или сообщение об ошибке).
        """
        primary_key_column = database_model.__mapper__.primary_key[0]
        primary_key_name = primary_key_column.name
        if 'id' in data:
            data[primary_key_name] = data['id']
            # так как колонки PK у всех разные по полю id, где это важно будет доступно значение для PK

        # Фильтруем данные, оставляя только те поля, которые есть в модели
        valid_fields = {
            key: value
            for key, value in data.items()
            if key in database_model.__table__.columns
        }

        async with self.session_maker() as session:
            try:
                my_object = database_model(**valid_fields)
                session.add(my_object)
                await session.commit()
                return 200, my_object
            except IntegrityError:
                if print_exception:
                    logger.print_exception()
                await session.rollback()
                return 500, 'Duplicate key value violates unique constraint'
            except Exception as ex:
                await session.rollback()
                if print_exception:
                    logger.print_exception()
                return 500, str(ex)

    async def delete_my_object(
        self, database_model: Type[Base], uuid: str
    ) -> Tuple[int, str]:
        """
        Удаляет объект из базы данных по UUID или ID.

        :param database_model: Модель SQLAlchemy.
        :param uuid: UUID или ID объекта.
        :return: Кортеж (код статуса, сообщение об успехе или ошибке).
        """
        if not (self.is_uuid(uuid) or uuid.isdigit()):
            return 500, "Incorrect UUID or ID"

        try:
            primary_key_column = database_model.__mapper__.primary_key[0]
            async with self.session_maker() as session:
                query = delete(database_model).where(
                    primary_key_column
                    == (int(uuid) if uuid.isdigit() else uuid)
                )
                await session.execute(query)
                await session.commit()
                return 200, "Success"
        except Exception as ex:
            logger.print_exception()
            return 500, str(ex)

    async def update_my_object(
        self,
        database_model: Type[Base],
        params: Dict[str, Any],
        fields: Dict[str, Any],
    ) -> Tuple[int, str]:
        """
        Обновляет объект в базе данных.

        :param database_model: Модель SQLAlchemy.
        :param params: словарь для условия обновления.
        :param fields: Поля и их новые значения.
        :return: Кортеж (код статуса, сообщение об успехе или ошибке).
        """
        try:
            async with self.session_maker() as session:
                query = update(database_model).values(**fields)
                for column, value in params.items():
                    query = query.where(
                        getattr(database_model, column) == value
                    )
                await session.execute(query)
                await session.commit()
                return 200, "Success"
        except Exception as ex:
            logger.print_exception()
            return 500, str(ex)

    async def show_my_object(
        self, database_model: Type[Base], params: Dict[str, Any]
    ) -> Tuple[int, Dict[str, dict]]:
        """
        Возвращает объект из базы данных.

        :param database_model: Модель SQLAlchemy.
        :param params: Колонка для условия поиска.
        :return: Кортеж (код статуса, данные объекта или пустой словарь).
        """
        try:
            primary_key_column = database_model.__mapper__.primary_key[0]
            primary_key_name = primary_key_column.name
            async with self.session_maker() as session:
                query = select(database_model)
                for column, value in params.items():
                    query = query.where(
                        getattr(database_model, column) == value
                    )
                result = await session.execute(query)
                result = result.scalars()
                return 200, {
                    str(getattr(obj, primary_key_name)): {
                        column.name: getattr(obj, column.name)
                        for column in database_model.__table__.columns
                    }
                    for obj in result
                }
        except Exception:
            logger.print_exception()
            return 500, {}

    async def show_my_objects(
        self, database_model: Type[Base]
    ) -> Dict[str, Any]:
        """
        Возвращает все объекты из таблицы.

        :param database_model: Модель SQLAlchemy.
        :return: Словарь с данными объектов.
        """
        try:
            primary_key_column = database_model.__mapper__.primary_key[0]
            primary_key_name = primary_key_column.name
            async with self.session_maker() as session:
                query = select(database_model)
                result = await session.execute(query)
                result = result.scalars()
                return {
                    str(getattr(obj, primary_key_name)): {
                        column.name: getattr(obj, column.name)
                        for column in database_model.__table__.columns
                    }
                    for obj in result
                }
        except Exception:
            logger.print_exception()
            return {}

    @staticmethod
    def is_uuid(string: str) -> bool:
        """
        Проверяет, является ли строка корректным UUID.

        :param string: Строка для проверки.
        :return: True, если строка является UUID, иначе False.
        """
        pattern = (
            r'^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$'
        )
        return bool(string and re.match(pattern, string))

    async def return_data(
        self, fs_table_name: str, refactor_date: bool = True
    ) -> Dict[str, Any]:
        """
        Возвращает данные из таблицы с преобразованием времени в московское.

        :param fs_table_name: Имя таблицы.
        :param refactor_date: Если дата не с моей базы конвентировать в московскую
        :return: Словарь с данными.
        """
        async with self.session_maker() as session:
            pk_column = await self.get_pk_name(fs_table_name)
            query = text(f"SELECT * FROM {fs_table_name}")
            response = await session.execute(query)
            result = response.fetchall()
            columns = response.keys()
            data_dict = {}
            for row in result:
                row_dict = {}
                for col, value in zip(columns, row):
                    if isinstance(value, datetime) and refactor_date:
                        value = convert_to_moscow_time(value)
                    elif value == 'false':
                        value = False
                    elif value == 'true':
                        value = True
                    row_dict[col] = value
                pk = row_dict.pop(pk_column)
                data_dict[str(pk)] = row_dict
                data_dict[str(pk)]['id'] = str(pk)
            return data_dict

    async def get_pk_name(self, table_name: str) -> str:
        """
        Возвращает имя первичного ключа таблицы.

        :param table_name: Имя таблицы.
        :return: Имя колонки первичного ключа.
        """
        async with self.session_maker() as session:
            # Запрос для получения информации о первичных ключах
            pk_query = text(
                f"""SELECT c.column_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.constraint_column_usage AS ccu USING (constraint_schema, constraint_name)
        JOIN information_schema.columns AS c ON c.table_schema = tc.constraint_schema
          AND tc.table_name = c.table_name AND ccu.column_name = c.column_name
        WHERE constraint_type = 'PRIMARY KEY' and tc.table_name = '{table_name}';
                    """
            )
            pk_response = await session.execute(pk_query)
            pk_column = pk_response.scalar()
        return pk_column

    async def execute_query(
        self, query: str
    ) -> Union[Tuple[int, Sequence[RowMapping]], Tuple[int, str]]:
        """
        Выполняет SQL-запрос и возвращает результат.

        :param query: SQL-запрос.
        :return: Кортеж (код статуса, результат или сообщение об ошибке).
        """
        try:
            async with self.session_maker() as session:
                query = text(query)
                result = await session.execute(query)
                await session.commit()
                if result.returns_rows:
                    rows = result.mappings().all()
                    return 200, rows
                else:
                    return 200, "Query executed successfully"
        except IntegrityError:
            await session.rollback()
            return 500, 'Duplicate key value violates unique constraint'
        except Exception as ex:
            await session.rollback()
            logger.print_exception()
            return 500, str(ex)


database_manager = DatabaseManager(db_manager)
