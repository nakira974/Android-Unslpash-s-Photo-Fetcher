package com.example.tpandroid

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tpandroid.data.Urls


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE `photo` (" +
                    "`id` INTEGER," +
                    " `full_resolution_url` TEXT, " +
                    " `raw_resolution_url` TEXT, " +
                    " `regular_resolution_url` TEXT, " +
                    " `small_resolution_url` TEXT, " +
                    " `thumb_resolution_url` TEXT, " +

                    "PRIMARY KEY(`id`))"
        )
    }
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val migrationTransactionQuery = "BEGIN TRANSACTION;\n" +
                "ALTER TABLE `photo` ADD `description` TEXT;\n" +
                "ALTER TABLE `photo` ADD `creator_name` TEXT;\n" +
                "COMMIT;\n"
        database.execSQL(
            migrationTransactionQuery
        )
    }
}

@Database(entities = [Urls::class], version = 4)
public abstract class ApplicationDbContext : RoomDatabase() {
    abstract fun photosRepository(): PhotosRepository

}