package edu.uph.m24si1.pamdas.data.vocabulary;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "database_vocabulary",
    foreignKeys = @ForeignKey(
        entity = Deck.class,
        parentColumns = "id",
        childColumns = "deck_id",
        onDelete = ForeignKey.NO_ACTION
    ),
    indices = {@Index(value = {"deck_id"}, name = "database_vocabulary_deck_id_d5d313a8")}
)
public class Vocabulary {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @NonNull
    public String hanzi;
    
    @NonNull
    public String pinyin;
    
    @NonNull
    public String meaning;
    
    @NonNull
    public String source;
    
    @ColumnInfo(name = "deck_id")
    public long deckId;

    public Vocabulary(long id, @NonNull String hanzi, @NonNull String pinyin, @NonNull String meaning, @NonNull String source, long deckId) {
        this.id = id;
        this.hanzi = hanzi;
        this.pinyin = pinyin;
        this.meaning = meaning;
        this.source = source;
        this.deckId = deckId;
    }

    public String getDisplayHanzi() {
        return hanzi.replaceAll("[0-9]", "");
    }
}
