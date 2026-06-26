package edu.uph.m24si1.pamdas;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mainpage);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // For now, let's just use MainActivity as the starting point.
        // If MainPage was intended to be the main screen, we can redirect or set it in Manifest.
        // Based on activity_main.xml content, it looks like a level selection screen.
        
        findViewById(R.id.btnHsk1).setOnClickListener(v -> startQuiz(1));
        findViewById(R.id.btnHsk2).setOnClickListener(v -> startQuiz(2));
        findViewById(R.id.btnHsk3).setOnClickListener(v -> startQuiz(3));
        findViewById(R.id.btnHsk4).setOnClickListener(v -> startQuiz(4));
    }

    private void startQuiz(int level) {
        Intent intent = new Intent(this, QuizPage.class);
        intent.putExtra("hsk_level", level);
        startActivity(intent);
    }
}
