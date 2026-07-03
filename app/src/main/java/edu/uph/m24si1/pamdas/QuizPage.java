package edu.uph.m24si1.pamdas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uph.m24si1.pamdas.data.vocabulary.Progress;
import edu.uph.m24si1.pamdas.data.vocabulary.UserStats;
import edu.uph.m24si1.pamdas.data.vocabulary.Vocabulary;
import edu.uph.m24si1.pamdas.data.vocabulary.VocabularyDao;
import edu.uph.m24si1.pamdas.data.vocabulary.VocabularyDatabase;

public class QuizPage extends AppCompatActivity {

    private TextView tvStage, tvSoalCount, tvSoalDesc, tvMasteryPercent, tvMasteryDesc, tvQuestionWord;
    private Button btnExitSave, btnChoice1, btnChoice2, btnChoice3, btnChoice4;
    private LinearLayout llResultOverlay, llYourAnswer;
    private TextView tvResultStatus, tvResultWord, tvResultPinyin, tvResultDefinition, tvYourAnswer;
    private MaterialCardView cvResultHeader;
    private Button btnContinue;

    private VocabularyDatabase db;
    private VocabularyDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<Vocabulary> stageVocabularies = new ArrayList<>();
    private List<Progress> stageProgress = new ArrayList<>();
    private int currentStage = 1;
    private Vocabulary currentQuestion;
    private Progress currentProgress;

    private int totalItems = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.quizpage);

        initViews();
        initDatabase();
        
        currentStage = getIntent().getIntExtra("STAGE", 1);
        tvStage.setText("Stage " + currentStage + " / 15");

        loadData();

        btnExitSave.setOnClickListener(v -> finish());
        btnContinue.setOnClickListener(v -> {
            llResultOverlay.setVisibility(View.GONE);
            nextQuestion();
        });
    }

    private void initViews() {
        tvStage = findViewById(R.id.tv_stage);
        tvSoalCount = findViewById(R.id.tv_soal_count);
        tvSoalDesc = findViewById(R.id.tv_soal_desc);
        tvMasteryPercent = findViewById(R.id.tv_mastery_percent);
        tvMasteryDesc = findViewById(R.id.tv_mastery_desc);
        tvQuestionWord = findViewById(R.id.tv_question_word);
        btnExitSave = findViewById(R.id.btn_exit_save);
        
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        
        btnChoice1 = findViewById(R.id.btn_choice1);
        btnChoice2 = findViewById(R.id.btn_choice2);
        btnChoice3 = findViewById(R.id.btn_choice3);
        btnChoice4 = findViewById(R.id.btn_choice4);
        
        llResultOverlay = findViewById(R.id.ll_result_overlay);
        llYourAnswer = findViewById(R.id.ll_your_answer);
        tvYourAnswer = findViewById(R.id.tv_your_answer);
        cvResultHeader = findViewById(R.id.cv_result_header);
        tvResultStatus = findViewById(R.id.tv_result_status);
        tvResultWord = findViewById(R.id.tv_result_word);
        tvResultPinyin = findViewById(R.id.tv_result_pinyin);
        tvResultDefinition = findViewById(R.id.tv_result_definition);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void initDatabase() {
        db = Room.databaseBuilder(getApplicationContext(),
                VocabularyDatabase.class, "pamda_db")
                .fallbackToDestructiveMigration()
                .build();
        dao = db.vocabularyDao();
    }

    private void loadData() {
        executor.execute(() -> {
            stageVocabularies = dao.getVocabularyByStage(currentStage);
            
            if (stageVocabularies.size() < 20) {
                generateDummyVocab(currentStage, 20 - stageVocabularies.size());
                stageVocabularies = dao.getVocabularyByStage(currentStage);
            }

            totalItems = stageVocabularies.size();
            stageProgress.clear();
            
            for (Vocabulary v : stageVocabularies) {
                Progress p = dao.getProgressForVocabulary(v.id);
                if (p == null) {
                    p = new Progress(v.id, 0, false, 0, System.currentTimeMillis());
                    dao.insertProgress(p);
                }
                stageProgress.add(p);
            }

            updateStreak();
            mainHandler.post(this::updateStats);
            mainHandler.post(this::nextQuestion);
        });
    }

    private void generateDummyVocab(int stage, int count) {
        String[] words = {"人", "大", "天", "太", "夫", "去", "也", "中", "小", "子", "月", "日", "水", "火", "山", "石", "田", "土", "木", "禾",
                         "口", "耳", "目", "手", "足", "门", "马", "牛", "羊", "鸟", "鱼", "草", "花", "叶", "木", "林", "森", "从", "众", "明"};
        String[] pinyins = {"rén", "dà", "tiān", "tài", "fū", "qù", "yě", "zhōng", "xiǎo", "zǐ", "yuè", "rì", "shuǐ", "huǒ", "shān", "shí", "tián", "tǔ", "mù", "hé",
                           "kǒu", "ěr", "mù", "shǒu", "zú", "mén", "mǎ", "niú", "yáng", "niǎo", "yú", "cǎo", "huā", "yè", "mù", "lín", "sēn", "cóng", "zhòng", "míng"};
        String[] defs = {"person", "big", "day", "too", "husband", "go", "also", "middle", "small", "son", "moon", "sun", "water", "fire", "mountain", "stone", "field", "earth", "wood", "grain",
                        "mouth", "ear", "eye", "hand", "foot", "door", "horse", "cow", "sheep", "bird", "fish", "grass", "flower", "leaf", "tree", "forest", "dense forest", "follow", "crowd", "bright"};

        int offset = (stage - 1) * count;
        for (int i = 0; i < count; i++) {
            int index = (offset + i) % words.length;
            dao.insert(new Vocabulary(words[index] + (offset + i), pinyins[index], defs[index], stage));
        }
    }

    private void updateStreak() {
        UserStats stats = dao.getUserStats();
        long now = System.currentTimeMillis();
        if (stats == null) {
            stats = new UserStats(1, now);
            dao.insertUserStats(stats);
        } else {
            long diff = now - stats.lastActiveDate;
            long dayInMillis = 24 * 60 * 60 * 1000;
            if (diff > dayInMillis && diff < 2 * dayInMillis) {
                stats.currentStreak++;
            } else if (diff >= 2 * dayInMillis) {
                stats.currentStreak = 1;
            }
            stats.lastActiveDate = now;
            dao.updateUserStats(stats);
        }
    }

    private void updateStats() {
        int masteredCount = 0;
        int shownCount = 0;
        for (Progress p : stageProgress) {
            if (p.firstTryCorrect || p.mastery >= 3) masteredCount++;
            if (p.reviewCount > 0) shownCount++;
        }

        double rate = totalItems > 0 ? (double) masteredCount / totalItems * 100 : 0;
        
        tvMasteryPercent.setText((int)rate + "%");
        tvMasteryDesc.setText((totalItems - masteredCount) + " soal belum selesai");
        
        tvSoalCount.setText(shownCount + "/" + totalItems);
        tvSoalDesc.setText(shownCount + " soal sudah keluar");
    }

    private void nextQuestion() {
        if (stageVocabularies.isEmpty()) return;

        List<Vocabulary> unmastered = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();

        for (int i = 0; i < stageVocabularies.size(); i++) {
            Progress p = stageProgress.get(i);
            if (!p.firstTryCorrect && p.mastery < 5) {
                unmastered.add(stageVocabularies.get(i));
                weights.add(6 - p.mastery);
            }
        }

        if (unmastered.isEmpty()) {
            checkStageCompletion();
            return;
        }

        int totalWeight = 0;
        for (int w : weights) totalWeight += w;

        Random rand = new Random();
        int r = rand.nextInt(totalWeight);
        int cumulativeWeight = 0;
        for (int i = 0; i < unmastered.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (r < cumulativeWeight) {
                currentQuestion = unmastered.get(i);
                currentProgress = findProgressFor(currentQuestion);
                break;
            }
        }

        tvQuestionWord.setText(currentQuestion.getDisplayHanzi());
        setupChoices();
    }

    private void checkStageCompletion() {
        int masteredCount = 0;
        for (Progress p : stageProgress) {
            if (p.firstTryCorrect || p.mastery >= 3) masteredCount++;
        }
        double rate = (double) masteredCount / totalItems * 100;
        
        boolean allShown = true;
        for (Progress p : stageProgress) { if (p.reviewCount == 0) { allShown = false; break; } }

        if (allShown && rate >= 80) {
             handleStageUp();
        } else {
            executor.execute(() -> {
                for (Progress p : stageProgress) {
                    if (p.mastery < 5) p.firstTryCorrect = false;
                    dao.updateProgress(p);
                }
                mainHandler.post(this::nextQuestion);
            });
        }
    }

    private void handleStageUp() {
        executor.execute(() -> {
            UserStats stats = dao.getUserStats();
            int streak = stats != null ? stats.currentStreak : 0;
            mainHandler.post(() -> {
                currentStage++;
                if (currentStage > 15) {
                    Intent intent = new Intent(this, ResultQuizPage.class);
                    intent.putExtra("STREAK", streak);
                    intent.putExtra("TOTAL_QUESTIONS", 15 * 20);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Stage Completed! Moving to Stage " + currentStage, Toast.LENGTH_SHORT).show();
                    tvStage.setText("Stage " + currentStage + " / 15");
                    loadData();
                }
            });
        });
    }

    private void setupChoices() {
        List<String> choices = new ArrayList<>();
        choices.add(currentQuestion.pinyin);

        List<Vocabulary> otherVocabs = new ArrayList<>(stageVocabularies);
        otherVocabs.remove(currentQuestion);
        Collections.shuffle(otherVocabs);
        for (int i = 0; i < Math.min(3, otherVocabs.size()); i++) {
            choices.add(otherVocabs.get(i).pinyin);
        }
        
        while (choices.size() < 4) {
            choices.add("Wrong " + choices.size());
        }

        Collections.shuffle(choices);

        Button[] buttons = {btnChoice1, btnChoice2, btnChoice3, btnChoice4};
        for (int i = 0; i < 4; i++) {
            buttons[i].setText(choices.get(i));
            String choice = choices.get(i);
            buttons[i].setOnClickListener(v -> checkAnswer(choice));
        }
    }

    private void checkAnswer(String selectedPinyin) {
        String normalizedSelected = normalizeAnswer(selectedPinyin, "choice");
        String normalizedCorrect = normalizeAnswer(currentQuestion.pinyin, "choice");

        if (normalizedSelected.equals(normalizedCorrect)) {
            handleCorrect();
        } else {
            handleWrong(selectedPinyin);
        }
    }

    private String normalizeAnswer(String value, String quizMode) {
        if (value == null) return "";
        String normalized = value.trim().toLowerCase();

        if (quizMode.equals("keyboard")) {
            return normalized.replace("ā", "a").replace("á", "a").replace("ǎ", "a").replace("à", "a")
                    .replace("ē", "e").replace("é", "e").replace("ě", "e").replace("è", "e")
                    .replace("ī", "i").replace("í", "i").replace("ǐ", "i").replace("ì", "i")
                    .replace("ō", "o").replace("ó", "o").replace("ǒ", "o").replace("ò", "o")
                    .replace("ū", "u").replace("ú", "u").replace("ǔ", "u").replace("ù", "u")
                    .replace("ü", "v").replace("ǖ", "v").replace("ǘ", "v").replace("ǚ", "v").replace("ǜ", "v")
                    .replace(" ", "").replace("-", "");
        }
        return normalized.replace(" ", "");
    }

    private void handleCorrect() {
        executor.execute(() -> {
            if (currentProgress.reviewCount == 0) {
                currentProgress.firstTryCorrect = true;
                currentProgress.mastery = 5;
            } else {
                currentProgress.mastery = Math.min(currentProgress.mastery + 2, 5);
            }
            currentProgress.reviewCount++;
            currentProgress.lastReviewed = System.currentTimeMillis();
            dao.updateProgress(currentProgress);

            mainHandler.post(() -> {
                updateStats();
                showCorrectOverlay();
            });
        });
    }

    private void handleWrong(String yourAnswer) {
        executor.execute(() -> {
            if (currentProgress.reviewCount == 0) {
                currentProgress.mastery = 0;
            } else {
                currentProgress.mastery = Math.max(currentProgress.mastery - 1, 0);
            }
            currentProgress.reviewCount++;
            currentProgress.firstTryCorrect = false;
            currentProgress.lastReviewed = System.currentTimeMillis();
            dao.updateProgress(currentProgress);

            mainHandler.post(() -> {
                updateStats();
                showWrongOverlay(yourAnswer);
            });
        });
    }

    private void showCorrectOverlay() {
        tvResultStatus.setText("✅ Correct!");
        cvResultHeader.setCardBackgroundColor(Color.parseColor("#2E4B2E"));
        llYourAnswer.setVisibility(View.GONE);
        showResultOverlay();
    }

    private void showWrongOverlay(String yourAnswer) {
        tvResultStatus.setText("❌ Wrong!");
        cvResultHeader.setCardBackgroundColor(Color.parseColor("#4B2E2E"));
        llYourAnswer.setVisibility(View.VISIBLE);
        tvYourAnswer.setText(yourAnswer);
        showResultOverlay();
    }

    private void showResultOverlay() {
        tvResultWord.setText(currentQuestion.getDisplayHanzi());
        tvResultPinyin.setText(currentQuestion.pinyin);
        tvResultDefinition.setText(currentQuestion.definition);
        llResultOverlay.setVisibility(View.VISIBLE);
    }

    private Progress findProgressFor(Vocabulary v) {
        for (Progress p : stageProgress) {
            if (p.vocabularyId == v.id) return p;
        }
        return null;
    }
}