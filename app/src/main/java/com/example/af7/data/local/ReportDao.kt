package com.example.af7.data.local

import androidx.room.*
import com.example.af7.data.model.Report
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY id DESC")
    fun getAllReports(): Flow<List<Report>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Report)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<Report>)

    @Query("UPDATE reports SET resolved = :isResolved WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: Int, isResolved: Boolean)

    @Query("DELETE FROM reports")
    suspend fun clearReports()
}
