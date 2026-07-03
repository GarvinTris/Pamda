package edu.uph.m24si1.pamdas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ResultQuizPage extends AppCompatActivity {

    private TextView tvStreak, tvTotalCorrect, tvTotalWrong, tvTotalQuestion;
    private Button btnFinish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.resultpage); // Using resultpage.xml as requested

        initViews();
        displayResults();

        btnFinish.setOnClickListener(v -> {
            Intent intent = new Intent(ResultQuizPage.this, MainPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private void initViews() {
        tvStreak = findViewById(R.id.tvStreak);
        tvTotalCorrect = findViewById(R.id.tvTotalCorrect);
        tvTotalWrong = findViewById(R.id.tvTotalWrong);
        tvTotalQuestion = findViewById(R.id.tvTotalQuestion);
        btnFinish = findViewById(R.id.btnFinish);
    }

    private void displayResults() {
        int streak = getIntent().getIntExtra("STREAK", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 300);
        
        tvStreak.setText(String.valueOf(streak));
        tvTotalQuestion.setText(String.valueOf(totalQuestions));
        // These are placeholders, real data should ideally come from DB or calculated during session
        tvTotalCorrect.setText(String.valueOf(totalQuestions)); 
        tvTotalWrong.setText("0");
    }
}