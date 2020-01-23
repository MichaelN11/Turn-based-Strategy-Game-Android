package com.example.michael.strategygame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    Button[] buttonArray;
    int buttonIndex;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        intent = new Intent(this, MainActivity.class);

        buttonArray = new Button[8];
        buttonArray[1] = findViewById(R.id.level1);
        buttonArray[2] = findViewById(R.id.level2);
        buttonArray[3] = findViewById(R.id.level3);
        buttonArray[4] = findViewById(R.id.level4);
        buttonArray[5] = findViewById(R.id.level5);
        buttonArray[6] = findViewById(R.id.level6);
        buttonArray[7] = findViewById(R.id.level7);

        for (int i = 1; i < 8; i++) {
            buttonIndex = i;
            buttonArray[i].setOnClickListener(new View.OnClickListener() {
                int index = buttonIndex;
                @Override
                public void onClick(View v) {
                    intent.putExtra("level", index);
                    startActivity(intent);
                }
            });
        }

    }
}
