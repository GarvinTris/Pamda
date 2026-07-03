package edu.uph.m24si1.pamdas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnHsk1).setOnClickListener(v -> onHskSelected(1));
        findViewById(R.id.btnHsk2).setOnClickListener(v -> onHskSelected(2));
        findViewById(R.id.btnHsk3).setOnClickListener(v -> onHskSelected(3));
        findViewById(R.id.btnHsk4).setOnClickListener(v -> onHskSelected(4));
    }

    private void onHskSelected(int level) {
        Intent intent = new Intent(this, QuizPage.class);
        intent.putExtra("STAGE", level);
        startActivity(intent);
    }
}