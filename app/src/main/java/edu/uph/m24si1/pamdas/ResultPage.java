package edu.uph.m24si1.pamdas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ResultPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultpage);

        // Ambil data dari Intent (dikirim dari backend/quiz logic kamu)
        int streak        = getIntent().getIntExtra("streak", 0);
        int totalCorrect  = getIntent().getIntExtra("total_correct", 0);
        int totalWrong    = getIntent().getIntExtra("total_wrong", 0);
        int totalQuestion = getIntent().getIntExtra("total_question", 0);

        ((TextView) findViewById(R.id.tvStreak)).setText(String.valueOf(streak));
        ((TextView) findViewById(R.id.tvTotalCorrect)).setText(String.valueOf(totalCorrect));
        ((TextView) findViewById(R.id.tvTotalWrong)).setText(String.valueOf(totalWrong));
        ((TextView) findViewById(R.id.tvTotalQuestion)).setText(String.valueOf(totalQuestion));

        findViewById(R.id.btnFinish).setOnClickListener(v -> {
            Intent intent = new Intent(ResultPage.this, MainPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}