package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.AppDatabase
import com.example.data.db.UserAccountDao
import com.example.data.model.UserAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    object Guest : AuthState()
    data class Authenticated(val user: UserAccount) : AuthState()
}

class AuthRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao: UserAccountDao = db.userAccountDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("chemmaster_auth_prefs", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    companion object {
        private const val KEY_ACTIVE_USER_ID = "active_user_id"
        private const val KEY_IS_GUEST = "is_guest"

        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                val inst = AuthRepository(context.applicationContext)
                INSTANCE = inst
                inst
            }
        }
    }

    suspend fun checkExistingSession() = withContext(Dispatchers.IO) {
        // Ensure default seed data exists
        if (dao.getUserCount() == 0) {
            dao.insertUser(
                UserAccount(
                    fullName = "Ankit Pandey",
                    email = "ankitpandey28082001@gmail.com",
                    mobileNumber = "9876543210",
                    passwordHash = AppDatabase.hashPassword("Password@123"),
                    chemistryLevel = "JEE / NEET Aspirant",
                    avatarColorHex = "#38BDF8",
                    createdAt = System.currentTimeMillis() - (86400000L * 30),
                    lastLoginAt = System.currentTimeMillis(),
                    quizzesAttempted = 8,
                    equationsBalanced = 14,
                    elementsExplored = 45
                )
            )
        }

        val isGuest = prefs.getBoolean(KEY_IS_GUEST, false)
        val activeUserId = prefs.getLong(KEY_ACTIVE_USER_ID, -1L)

        if (activeUserId != -1L) {
            val user = dao.getUserById(activeUserId)
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                return@withContext
            }
        }

        if (isGuest) {
            _authState.value = AuthState.Guest
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Authenticates an existing user using either their Email ID or Mobile Number.
     */
    suspend fun loginWithEmailOrMobile(identifier: String, password: String): Result<UserAccount> = withContext(Dispatchers.IO) {
        val cleanIdentifier = identifier.trim()
        if (cleanIdentifier.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter your registered Email ID or Mobile Number."))
        }
        if (password.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter your password."))
        }

        // Clean digits if user entered mobile number with spaces/dashes
        val digitOnly = cleanIdentifier.filter { it.isDigit() }
        val user = if (digitOnly.length >= 7 && !cleanIdentifier.contains("@")) {
            // Search by mobile number (exact or last 10 digits)
            val searchTarget = if (digitOnly.length > 10) digitOnly.takeLast(10) else digitOnly
            dao.getUserByMobile(searchTarget) ?: dao.getUserByEmailOrMobile(cleanIdentifier)
        } else {
            dao.getUserByEmail(cleanIdentifier) ?: dao.getUserByEmailOrMobile(cleanIdentifier)
        }

        if (user == null) {
            return@withContext Result.failure(
                IllegalArgumentException("No ChemMaster account found matching '$cleanIdentifier'. Please check your credentials or register as a new user.")
            )
        }

        val inputHash = AppDatabase.hashPassword(password)
        if (user.passwordHash != inputHash) {
            return@withContext Result.failure(
                IllegalArgumentException("Incorrect password for ${user.fullName}. Please try again.")
            )
        }

        // Update last login timestamp in IST
        val updatedUser = user.copy(lastLoginAt = System.currentTimeMillis())
        dao.updateUser(updatedUser)

        // Save session
        prefs.edit()
            .putLong(KEY_ACTIVE_USER_ID, updatedUser.id)
            .putBoolean(KEY_IS_GUEST, false)
            .apply()

        _authState.value = AuthState.Authenticated(updatedUser)
        Result.success(updatedUser)
    }

    /**
     * Registers a new user.
     */
    suspend fun registerNewUser(
        fullName: String,
        email: String,
        mobileNumber: String,
        password: String,
        confirmPassword: String,
        chemistryLevel: String
    ): Result<UserAccount> = withContext(Dispatchers.IO) {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanMobile = mobileNumber.trim().filter { it.isDigit() }

        if (cleanName.length < 2) {
            return@withContext Result.failure(IllegalArgumentException("Please enter your full name (at least 2 characters)."))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid email address (e.g., student@example.com)."))
        }

        if (cleanMobile.length < 10) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number."))
        }

        if (password.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters long."))
        }

        if (password != confirmPassword) {
            return@withContext Result.failure(IllegalArgumentException("Password and Confirm Password do not match."))
        }

        // Check if email already in use
        val existingEmail = dao.getUserByEmail(cleanEmail)
        if (existingEmail != null) {
            return@withContext Result.failure(IllegalArgumentException("An account with email '$cleanEmail' already exists. Please log in instead."))
        }

        // Check if mobile already in use
        val existingMobile = dao.getUserByMobile(cleanMobile)
        if (existingMobile != null) {
            return@withContext Result.failure(IllegalArgumentException("An account with mobile number '$cleanMobile' already exists. Please log in instead."))
        }

        val avatarColors = listOf("#38BDF8", "#34D399", "#FBBF24", "#F472B6", "#A78BFA", "#F87171")
        val randomAvatar = avatarColors.random()

        val newUser = UserAccount(
            fullName = cleanName,
            email = cleanEmail,
            mobileNumber = cleanMobile,
            passwordHash = AppDatabase.hashPassword(password),
            chemistryLevel = chemistryLevel,
            avatarColorHex = randomAvatar,
            createdAt = System.currentTimeMillis(),
            lastLoginAt = System.currentTimeMillis()
        )

        val insertedId = dao.insertUser(newUser)
        val persistedUser = newUser.copy(id = insertedId)

        // Save session
        prefs.edit()
            .putLong(KEY_ACTIVE_USER_ID, insertedId)
            .putBoolean(KEY_IS_GUEST, false)
            .apply()

        _authState.value = AuthState.Authenticated(persistedUser)
        Result.success(persistedUser)
    }

    fun continueAsGuest() {
        prefs.edit()
            .putLong(KEY_ACTIVE_USER_ID, -1L)
            .putBoolean(KEY_IS_GUEST, true)
            .apply()
        _authState.value = AuthState.Guest
    }

    fun logout() {
        prefs.edit()
            .remove(KEY_ACTIVE_USER_ID)
            .putBoolean(KEY_IS_GUEST, false)
            .apply()
        _authState.value = AuthState.Unauthenticated
    }

    suspend fun incrementStat(equationBalanced: Boolean = false, quizCompleted: Boolean = false, elementViewed: Boolean = false) = withContext(Dispatchers.IO) {
        val current = _authState.value
        if (current is AuthState.Authenticated) {
            val user = current.user
            val updated = user.copy(
                equationsBalanced = if (equationBalanced) user.equationsBalanced + 1 else user.equationsBalanced,
                quizzesAttempted = if (quizCompleted) user.quizzesAttempted + 1 else user.quizzesAttempted,
                elementsExplored = if (elementViewed) user.elementsExplored + 1 else user.elementsExplored
            )
            dao.updateUser(updated)
            _authState.value = AuthState.Authenticated(updated)
        }
    }
}
