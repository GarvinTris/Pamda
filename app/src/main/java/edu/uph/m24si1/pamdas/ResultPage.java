package edu.uph.m24si1.pamdas;

import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.content.Intent;
public class ResultPage extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savebd){
        super.onCreate(savebd);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mainpage);

    }
}
