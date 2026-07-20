package edu.uph.m24si1.pamdas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        
        View btn1 = findViewById(R.id.btnHsk1);
        if (btn1 != null) btn1.setOnClickListener(v -> startQuiz(1));

        View btn2 = findViewById(R.id.btnHsk2);
        if (btn2 != null) btn2.setOnClickListener(v -> startQuiz(2));

        View btn3 = findViewById(R.id.btnHsk3);
        if (btn3 != null) btn3.setOnClickListener(v -> startQuiz(3));

        View btn4 = findViewById(R.id.btnHsk4);
        if (btn4 != null) btn4.setOnClickListener(v -> startQuiz(4));

        View btn5 = findViewById(R.id.btnHsk5);
        if (btn5 != null) btn5.setOnClickListener(v -> startQuiz(5));

        View btn6 = findViewById(R.id.btnHsk6);
        if (btn6 != null) btn6.setOnClickListener(v -> startQuiz(6));

        View btn7 = findViewById(R.id.btnHsk7);
        if (btn7 != null) btn7.setOnClickListener(v -> startQuiz(7));
    }

    private void startQuiz(int level) {
        Intent intent = new Intent(this, QuizPage.class);
        intent.putExtra("STAGE", level); // STAGE extra is used for HSK level in QuizPage
        startActivity(intent);
    }
}
