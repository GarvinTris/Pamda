package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "database_deck",
    indices = {@Index(value = {"name"}, unique = true)}
)
public class Deck {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @NonNull
    public String name;
    
    @NonNull
    public String description;
    
    @ColumnInfo(name = "stage_size")
    public int stageSize;
    
    @ColumnInfo(name = "mastery_target")
    public int masteryTarget;

    public Deck(long id, @NonNull String name, @NonNull String description, int stageSize, int masteryTarget) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stageSize = stageSize;
        this.masteryTarget = masteryTarget;
    }
}
