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
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<Vocabulary> stageVocabularies = new ArrayList<>();
    private final List<Progress> stageProgress = new ArrayList<>();
    private int currentHskLevel = 1;
    private int currentStage = 1;
    private Vocabulary currentQuestion;
    private Progress currentProgress;

    private int totalItems = 0;
    private int sessionCorrectCount = 0;
    private int sessionWrongCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.quizpage);

        initViews();
        initDatabase();
        
        currentHskLevel = getIntent().getIntExtra("STAGE", 1);
        currentStage = 1;
        tvStage.setText("HSK " + currentHskLevel + " - Stage " + currentStage);

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
                .createFromAsset("Pamda_db.sqlite3") // Use the external database file
                .fallbackToDestructiveMigration()
                .build();
        dao = db.vocabularyDao();
    }

    private void loadData() {
        executor.execute(() -> {
            stageVocabularies = dao.getVocabularyByHskAndStage(currentHskLevel, currentStage);
            
            // If the stage is empty in the database, we might need a fallback or just inform user
            if (stageVocabularies.isEmpty()) {
                mainHandler.post(() -> {
                    Toast.makeText(this, "No vocabulary found for HSK " + currentHskLevel + " Stage " + currentStage, Toast.LENGTH_LONG).show();
                    finish();
                });
                return;
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
            mainHandler.post(() -> {
                tvStage.setText("HSK " + currentHskLevel + " - Stage " + currentStage);
                updateStats();
                nextQuestion();
            });
        });
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
            // Using 3 as MASTERED_SCORE from Python logic
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
                // Check if more stages exist for this HSK level would be ideal
                // For now just continue to next stage
                Toast.makeText(this, "HSK " + currentHskLevel + " Stage Completed! Moving to Stage " + currentStage, Toast.LENGTH_SHORT).show();
                loadData();
            });
        });
    }

    private void setupChoices() {
        List<String> choices = new ArrayList<>();
        choices.add(currentQuestion.pinyin);

        List<String> allPinyins = new ArrayList<>();
        for (Vocabulary v : stageVocabularies) {
            if (!v.pinyin.equals(currentQuestion.pinyin)) {
                allPinyins.add(v.pinyin);
            }
        }
        
        Collections.shuffle(allPinyins);
        
        int added = 0;
        for (String p : allPinyins) {
            if (added >= 3) break;
            if (!choices.contains(p)) {
                choices.add(p);
                added++;
            }
        }
        
        while (choices.size() < 4) {
            String fallback = "Choice " + (choices.size() + 1);
            if (!choices.contains(fallback)) choices.add(fallback);
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
                sessionCorrectCount++;
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
                sessionWrongCount++;
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
        tvResultDefinition.setText(currentQuestion.meaning);
        llResultOverlay.setVisibility(View.VISIBLE);
    }

    private Progress findProgressFor(Vocabulary v) {
        for (Progress p : stageProgress) {
            if (p.vocabularyId == v.id) return p;
        }
        return null;
    }
}