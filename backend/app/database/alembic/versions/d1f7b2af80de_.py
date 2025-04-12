from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

# revision identifiers, used by Alembic.
revision = 'd1f7b2af80de'
down_revision = '9b32f4914bc7'
branch_labels = None
depends_on = None


def upgrade() -> None:
    # 1. Сначала создаем новый ENUM тип
    op.execute("CREATE TYPE docsignstatus AS ENUM ('IN_PROGRESS', 'SIGNED', 'REJECTED')")
    
    # 2. Затем изменяем тип колонки
    op.alter_column('document', 'status',
               type_=postgresql.ENUM('IN_PROGRESS', 'SIGNED', 'REJECTED', name='docsignstatus'),
               postgresql_using='status::text::docsignstatus')
    
    # 3. Удаляем старый ENUM тип (опционально)
    op.execute("DROP TYPE doc_sign_status")


def downgrade() -> None:
    # 1. Создаем старый ENUM тип
    op.execute("CREATE TYPE doc_sign_status AS ENUM ('in_progress', 'signed', 'rejected')")
    
    # 2. Меняем тип колонки обратно
    op.alter_column('document', 'status',
               type_=postgresql.ENUM('in_progress', 'signed', 'rejected', name='doc_sign_status'),
               postgresql_using='status::text::doc_sign_status')
    
    # 3. Удаляем новый ENUM тип
    op.execute("DROP TYPE docsignstatus")