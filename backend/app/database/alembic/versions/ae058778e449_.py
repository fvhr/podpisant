"""empty message

Revision ID: ae058778e449
Revises: adfacb01c58f
Create Date: 2025-04-12 18:26:11.132907

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

# revision identifiers, used by Alembic.
revision: str = 'ae058778e449'
down_revision: Union[str, None] = 'adfacb01c58f'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('user_organization',
    sa.Column('user_id', sa.Uuid(), nullable=False),
    sa.Column('organization_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['organization_id'], ['organization.id'], ),
    sa.ForeignKeyConstraint(['user_id'], ['user.id'], ),
    sa.PrimaryKeyConstraint('user_id', 'organization_id')
    )
    op.create_foreign_key(None, 'department', 'organization', ['organization_id'], ['id'])
    op.create_foreign_key(None, 'document', 'department', ['department_id'], ['id'])
    op.create_foreign_key(None, 'document', 'user', ['creator_id'], ['id'])
    op.add_column('document_signature', sa.Column('document_id', sa.Integer(), nullable=False))
    op.add_column('document_signature', sa.Column('user_id', sa.Uuid(), nullable=False))
    op.drop_constraint('document_signature_stage_signer_id_fkey', 'document_signature', type_='foreignkey')
    op.create_foreign_key(None, 'document_signature', 'document', ['document_id'], ['id'])
    op.create_foreign_key(None, 'document_signature', 'user', ['user_id'], ['id'])
    op.drop_column('document_signature', 'signature_data')
    op.drop_column('document_signature', 'stage_signer_id')
    op.create_foreign_key(None, 'organization', 'user', ['admin_id'], ['id'])
    op.drop_column('stage_signer', 'is_required')
    op.add_column('user_department', sa.Column('tags', postgresql.JSONB(astext_type=sa.Text()), nullable=True))
    op.drop_column('user_department', 'role')
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('user_department', sa.Column('role', sa.VARCHAR(length=50), server_default=sa.text("'employee'::character varying"), autoincrement=False, nullable=False))
    op.drop_column('user_department', 'tags')
    op.add_column('stage_signer', sa.Column('is_required', sa.BOOLEAN(), server_default=sa.text('true'), autoincrement=False, nullable=False))
    op.drop_constraint(None, 'organization', type_='foreignkey')
    op.add_column('document_signature', sa.Column('stage_signer_id', sa.INTEGER(), autoincrement=False, nullable=False))
    op.add_column('document_signature', sa.Column('signature_data', sa.VARCHAR(length=500), autoincrement=False, nullable=True))
    op.drop_constraint(None, 'document_signature', type_='foreignkey')
    op.drop_constraint(None, 'document_signature', type_='foreignkey')
    op.create_foreign_key('document_signature_stage_signer_id_fkey', 'document_signature', 'stage_signer', ['stage_signer_id'], ['id'])
    op.drop_column('document_signature', 'user_id')
    op.drop_column('document_signature', 'document_id')
    op.drop_constraint(None, 'document', type_='foreignkey')
    op.drop_constraint(None, 'document', type_='foreignkey')
    op.drop_constraint(None, 'department', type_='foreignkey')
    op.drop_table('user_organization')
    # ### end Alembic commands ###
