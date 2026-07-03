package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "progress_table")
public class Progress {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int vocabularyId;
    public int reviewCount;
    public boolean firstTryCorrect;
    public int mastery; // 0 to 100
    public long lastReviewed;

    public Progress(int vocabularyId, int reviewCount, boolean firstTryCorrect, int mastery, long lastReviewed) {
        this.vocabularyId = vocabularyId;
        this.reviewCount = reviewCount;
        this.firstTryCorrect = firstTryCorrect;
        this.mastery = mastery;
        this.lastReviewed = lastReviewed;
    }
}