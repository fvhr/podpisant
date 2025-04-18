"""empty message

Revision ID: 6d9863d7df3b
Revises: 74a9e87413ed
Create Date: 2025-04-13 11:57:49.696120

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '6d9863d7df3b'
down_revision: Union[str, None] = '74a9e87413ed'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('stage_signer', sa.Column('rejected_at', sa.DateTime(timezone=True), nullable=True))
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('stage_signer', 'rejected_at')
    # ### end Alembic commands ###
