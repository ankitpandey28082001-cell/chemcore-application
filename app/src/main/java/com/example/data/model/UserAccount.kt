package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val mobileNumber: String,
    val passwordHash: String,
    val chemistryLevel: String = "JEE / NEET Aspirant",
    val avatarColorHex: String = "#38BDF8",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val quizzesAttempted: Int = 0,
    val equationsBalanced: Int = 0,
    val elementsExplored: Int = 0
)
