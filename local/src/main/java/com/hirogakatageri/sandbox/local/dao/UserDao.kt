package com.hirogakatageri.sandbox.local.dao

import androidx.room.*
import com.hirogakatageri.sandbox.local.model.LocalUserModel
import com.hirogakatageri.sandbox.local.model.SimpleLocalUserModel

@Dao
interface UserDao {

    @Query("SELECT * FROM users ")
    suspend fun getAllUsers(): List<LocalUserModel>

    @Query(
        "SELECT * FROM users " +
                "LIMIT :offset, :limit"
    )
    suspend fun getUsers(offset: Long, limit: Int = 20): List<LocalUserModel>

    @Query(
        "SELECT * FROM users " +
                "WHERE username=:username "
    )
    suspend fun getUser(username: String): LocalUserModel?

    @Query(
        "SELECT * FROM users " +
                "JOIN users_fts " +
                "ON rowid == users_fts.rowid " +
                "WHERE users_fts.username " +
                "MATCH :value || '*' " +
                "GROUP BY rowid "
    )
    suspend fun search(value: String): List<SimpleLocalUserModel>

    @Query(
        "SELECT * FROM users " +
                "WHERE username LIKE '%' || :value || '%' " +
                "OR notes LIKE '%' || :value || '%' " +
                "OR name LIKE '%' || :value || '%' "
    )
    suspend fun searchComplex(value: String): List<LocalUserModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(vararg users: LocalUserModel)

    @Update
    suspend fun updateUsers(vararg users: LocalUserModel)

}