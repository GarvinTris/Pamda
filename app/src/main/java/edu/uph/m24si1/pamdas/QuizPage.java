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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.uph.m24si1.pamdas.data.vocabulary.LearningSession;
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

    private List<Vocabulary> deckVocabularies = new ArrayList<>();
    private final List<Vocabulary> stageVocabularies = new ArrayList<>();
    private final List<Progress> stageProgress = new ArrayList<>();
    private int currentHskLevel = 1;
    private int currentStage = 1;
    private Vocabulary currentQuestion;
    private Progress currentProgress;
    private long currentSessionId = -1;

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

        loadDeckAndSession();

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
                .createFromAsset("databases/pamda_db.sqlite3")
                .fallbackToDestructiveMigration()
                .build();
        dao = db.vocabularyDao();
    }

    private void loadDeckAndSession() {
        executor.execute(() -> {
            LearningSession session = dao.getSessionForDeck(currentHskLevel);
            if (session == null) {
                session = new LearningSession(0, 1, getCurrentTimestamp(), getCurrentTimestamp(), currentHskLevel, 10, null);
                currentSessionId = dao.insertSession(session);
            } else {
                currentSessionId = session.id;
                currentStage = session.currentStage;
            }
            loadDeckData();
        });
    }

    private void loadDeckData() {
        executor.execute(() -> {
            deckVocabularies = dao.getVocabularyByDeck(currentHskLevel);
            if (deckVocabularies.isEmpty()) {
                mainHandler.post(() -> {
                    Toast.makeText(this, "No vocabulary found for HSK " + currentHskLevel, Toast.LENGTH_LONG).show();
                    finish();
                });
                return;
            }
            Collections.shuffle(deckVocabularies);
            
            updateStreak();
            mainHandler.post(this::startStage);
        });
    }

    private void startStage() {
        executor.execute(() -> {
            stageVocabularies.clear();
            stageProgress.clear();

            int startIdx = (currentStage - 1) * 20;
            if (startIdx >= deckVocabularies.size()) {
                mainHandler.post(this::finishQuiz);
                return;
            }

            int endIdx = Math.min(startIdx + 20, deckVocabularies.size());
            for (int i = startIdx; i < endIdx; i++) {
                Vocabulary v = deckVocabularies.get(i);
                stageVocabularies.add(v);
                
                Progress p = dao.getProgressForVocabularyAndSession(v.id, currentSessionId);
                if (p == null) {
                    p = new Progress(0, 0, false, 0, getCurrentTimestamp(), currentSessionId, v.id);
                    dao.insertProgress(p);
                    p = dao.getProgressForVocabularyAndSession(v.id, currentSessionId);
                }
                stageProgress.add(p);
            }

            mainHandler.post(() -> {
                tvStage.setText("HSK " + currentHskLevel + " - Stage " + currentStage);
                updateStats();
                nextQuestion();
            });
        });
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
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
                stats.currentStreak = stats.currentStreak + 1;
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

        int totalInStage = stageVocabularies.size();
        double rate = totalInStage > 0 ? (double) masteredCount / totalInStage * 100 : 0;
        
        tvMasteryPercent.setText((int)rate + "%");
        tvMasteryDesc.setText((totalInStage - masteredCount) + " soal belum selesai");
        
        tvSoalCount.setText(shownCount + "/" + totalInStage);
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
        int totalInStage = stageVocabularies.size();
        double rate = totalInStage > 0 ? (double) masteredCount / totalInStage * 100 : 0;
        
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
        currentStage++;
        Toast.makeText(this, "Stage " + (currentStage - 1) + " Completed!", Toast.LENGTH_SHORT).show();
        startStage();
    }

    private void finishQuiz() {
        executor.execute(() -> {
            UserStats stats = dao.getUserStats();
            int streak = (stats != null) ? stats.currentStreak : 0;
            mainHandler.post(() -> {
                Intent intent = new Intent(this, ResultQuizPage.class);
                intent.putExtra("STREAK", streak);
                intent.putExtra("TOTAL_CORRECT", sessionCorrectCount);
                intent.putExtra("TOTAL_WRONG", sessionWrongCount);
                intent.putExtra("TOTAL_QUESTIONS", sessionCorrectCount + sessionWrongCount);
                startActivity(intent);
                finish();
            });
        });
    }

    private void setupChoices() {
        List<String> choices = new ArrayList<>();
        choices.add(currentQuestion.pinyin);

        List<String> distractors = new ArrayList<>();
        for (Vocabulary v : deckVocabularies) {
            if (!v.pinyin.equals(currentQuestion.pinyin)) {
                distractors.add(v.pinyin);
            }
        }
        Collections.shuffle(distractors);
        
        int added = 0;
        for (String p : distractors) {
            if (added >= 3) break;
            if (!choices.contains(p)) {
                choices.add(p);
                added++;
            }
        }
        
        while (choices.size() < 4) {
            choices.add("Choice " + (choices.size() + 1));
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
        String normalizedSelected = normalizeAnswer(selectedPinyin);
        String normalizedCorrect = normalizeAnswer(currentQuestion.pinyin);

        if (normalizedSelected.equals(normalizedCorrect)) {
            handleCorrect();
        } else {
            handleWrong(selectedPinyin);
        }
    }

    private String normalizeAnswer(String value) {
        if (value == null) return "";
        return value.trim().toLowerCase().replace(" ", "");
    }

    private void handleCorrect() {
        executor.execute(() -> {
            if (currentProgress.reviewCount == 0) {
                currentProgress.firstTryCorrect = true;
                currentProgress.mastery = 5;
            } else {
                currentProgress.mastery = Math.min(currentProgress.mastery + 2, 5);
            }
            currentProgress.reviewCount = currentProgress.reviewCount + 1;
            currentProgress.lastReview = getCurrentTimestamp();
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
            currentProgress.reviewCount = currentProgress.reviewCount + 1;
            currentProgress.firstTryCorrect = false;
            currentProgress.lastReview = getCurrentTimestamp();
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
