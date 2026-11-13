package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.UserAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(entities = [UserAccount::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userAccountDao(): UserAccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chemmaster_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = database.userAccountDao()
                        if (dao.getUserCount() == 0) {
                            // Seed default demo user account with both email and mobile
                            dao.insertUser(
                                UserAccount(
                                    fullName = "Ankit Pandey",
                                    email = "ankitpandey28082001@gmail.com",
                                    mobileNumber = "9876543210",
                                    passwordHash = hashPassword("Password@123"),
                                    chemistryLevel = "JEE / NEET Aspirant",
                                    avatarColorHex = "#38BDF8",
                                    createdAt = System.currentTimeMillis() - (86400000L * 30),
                                    lastLoginAt = System.currentTimeMillis(),
                                    quizzesAttempted = 8,
                                    equationsBalanced = 14,
                                    elementsExplored = 45
                                )
                            )
                            dao.insertUser(
                                UserAccount(
                                    fullName = "Dr. Marie Curie",
                                    email = "marie.curie@chem.edu",
                                    mobileNumber = "9123456789",
                                    passwordHash = hashPassword("Radium@1898"),
                                    chemistryLevel = "College / University Professor",
                                    avatarColorHex = "#34D399",
                                    createdAt = System.currentTimeMillis() - (86400000L * 60),
                                    lastLoginAt = System.currentTimeMillis() - 3600000L,
                                    quizzesAttempted = 10,
                                    equationsBalanced = 35,
                                    elementsExplored = 88
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
