package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_stats")
public class UserStats {
    @PrimaryKey
    public int id = 1; // Single row for user stats
    public int currentStreak;
    public long lastActiveDate;

    public UserStats(int currentStreak, long lastActiveDate) {
        this.currentStreak = currentStreak;
        this.lastActiveDate = lastActiveDate;
    }
}