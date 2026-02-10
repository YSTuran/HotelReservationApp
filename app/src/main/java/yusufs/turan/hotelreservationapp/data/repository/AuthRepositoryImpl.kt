package yusufs.turan.hotelreservationapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import yusufs.turan.hotelreservationapp.domain.model.AppUser
import yusufs.turan.hotelreservationapp.domain.model.UserRole
import yusufs.turan.hotelreservationapp.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AppUser> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Kullanıcı ID bulunamadı")


            val document = firestore.collection("users").document(uid).get().await()

            val roleString = document.getString("role") ?: "USER"
            val role = try {
                UserRole.valueOf(roleString)
            } catch (e: Exception) {
                UserRole.USER
            }

            val name = document.getString("name") ?: ""

            Result.success(AppUser(uid, email, role, name))

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        role: UserRole
    ): Result<AppUser> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Kayıt oluşturulamadı")

            val userMap = hashMapOf(
                "uid" to uid,
                "email" to email,
                "role" to role.name,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users").document(uid).set(userMap).await()

            Result.success(AppUser(uid, email, role))

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUserRole(): UserRole {
        val uid = firebaseAuth.currentUser?.uid ?: return UserRole.GUEST

        return try {
            val document = firestore.collection("users").document(uid).get().await()
            val roleString = document.getString("role") ?: "USER"
            UserRole.valueOf(roleString)
        } catch (e: Exception) {
            UserRole.USER
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}