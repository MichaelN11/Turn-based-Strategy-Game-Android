package com.example.michael.strategygame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.michael.strategygame.Units.Artillery;
import com.example.michael.strategygame.Units.Infantry;
import com.example.michael.strategygame.Units.RPG;
import com.example.michael.strategygame.Units.Tank;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Intent menuIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        int level = intent.getIntExtra("level", 1);

        menuIntent = new Intent(this, MenuActivity.class);

        Button levelSelectButton = findViewById(R.id.levelSelect);
        levelSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(menuIntent);
            }
        });

        // Access the game view and instantiate the game engine controller class
        GameView gameView = findViewById(R.id.game);
        Button[] buttons = {findViewById(R.id.fireBtn), findViewById(R.id.endTurnBtn), levelSelectButton};
        TextView winText = findViewById(R.id.victory);
        TextView turnText = findViewById(R.id.turnText);
        GameController game = new GameController(gameView, buttons, winText, turnText);

        LevelData data = LevelData.getLevelData(level);

        game.loadLevel(data.getTerrainString(), data.getNumRows(), data.getNumCols(), data.getUnitList());
    }
}
