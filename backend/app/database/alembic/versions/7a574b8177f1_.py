"""empty message

Revision ID: 7a574b8177f1
Revises: ae058778e449
Create Date: 2025-04-12 19:15:21.692839

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '7a574b8177f1'
down_revision: Union[str, None] = 'ae058778e449'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_constraint('document_current_stage_id_fkey', 'document', type_='foreignkey')
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_foreign_key('document_current_stage_id_fkey', 'document', 'doc_sign_stage', ['current_stage_id'], ['id'])
    # ### end Alembic commands ###
