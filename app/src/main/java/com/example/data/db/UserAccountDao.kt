package com.example.data.db

import androidx.room.*
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE mobileNumber = :mobile OR mobileNumber LIKE '%' || :mobile LIMIT 1")
    suspend fun getUserByMobile(mobile: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:identifier) OR mobileNumber = :identifier OR mobileNumber LIKE '%' || :identifier LIMIT 1")
    suspend fun getUserByEmailOrMobile(identifier: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE id = :id LIMIT 1")
    fun observeUserById(id: Long): Flow<UserAccount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount): Long

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getUserCount(): Int

    @Query("SELECT * FROM user_accounts ORDER BY lastLoginAt DESC")
    fun getAllUsers(): Flow<List<UserAccount>>
}
