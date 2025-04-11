package bob.colbaskin.iubip_spring2025.utils.user

import androidx.datastore.core.Serializer
import bob.colbaskin.datastore.UserPreferences

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

class UserPreferenceSerializer: Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return withContext(Dispatchers.IO) {
            UserPreferences.parseFrom(input)
        }
    }

    override suspend fun writeTo(
        t: UserPreferences,
        output: OutputStream
    ) {
        return withContext(Dispatchers.IO) {
            t.writeTo(output)
        }
    }
}