package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Vocabulary.class, Progress.class, UserStats.class}, version = 3)
public abstract class VocabularyDatabase extends RoomDatabase {
    public abstract VocabularyDao vocabularyDao();
}