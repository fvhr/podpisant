package bob.colbaskin.iubip_spring2025.organizations.domain.models

data class Department(
    val id: Int,
    val name: String,
    val description: String,
    val users: List<DepartmentUser>
)

data class DepartmentUser(
    val id: String,
    val fio: String
)
