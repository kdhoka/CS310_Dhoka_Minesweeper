package com.example.cs310_dhoka_minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;
    private TextView[][] cell_tvs;
    private ArrayList<Integer> mines;
    private boolean flagging = false;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void generateMines(){
        Random rand = new Random();
        mines = new ArrayList<>();
        while(mines.size() < 4){
            int num = rand.nextInt(80);
            if(!mines.contains(num)){
                mines.add(num);
            }
        }
    }


    private int detectMines(@NonNull int[] index){
        System.out.println("Detecting mines");
        if(mines.contains((index[0]*8) + index[1])){
            return -1;
        } else {
            int count = 0;
            for(int i = index[0] - 1; i < index[0] + 2; i++){
                for(int j = index[1] - 1; j < index[1] + 2; j++){
                    if((i >= 0 && i <= 7) && (j >= 0 && j <= 7)){
                        if(mines.contains((i*8) + j)){
                            count++;
                        }
                    }
                }
            }
            return count;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateMines();

        cell_tvs = new TextView[10][8];


        // Method (2): add four dynamically created cells
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        for (int i = 0; i<=9; i++) {
            for (int j=0; j<=7; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(32) );
                tv.setTextSize( 22 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.parseColor("lime"));
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs[i][j] = tv;
            }
        }
    }

    private int[] findIndexOfCellTextView(TextView tv) {
        int[] coords = new int[2];
        coords[0] = -1;
        coords[1] = -1;
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < COLUMN_COUNT; j++){
                if(cell_tvs[i][j] == tv){
                    coords[0] = i;
                    coords[1] = j;
                    break;
                }
            }
        }
        return coords;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int[] n = findIndexOfCellTextView(tv);
        tv.setText(String.valueOf(detectMines(n)));
        tv.setTextColor(Color.GRAY);
        tv.setBackgroundColor(Color.LTGRAY);
    }

    public void onClickMode(View view){
        TextView tv = (TextView) view;
        if(!flagging){
            tv.setText(R.string.flag);
            flagging = true;
        } else {
            tv.setText(R.string.pick);
            flagging = false;
        }
    }
}