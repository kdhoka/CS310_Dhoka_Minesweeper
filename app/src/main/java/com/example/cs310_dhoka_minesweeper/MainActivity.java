package com.example.cs310_dhoka_minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int MINE_COUNT = 4;
    private TextView[][] cell_tvs;
    private ArrayList<Integer> mines;
    private boolean flagging = false;
    private int clock;
    private boolean running;
    private Intent intent;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void generateMines(){
        Random rand = new Random();
        mines = new ArrayList<>();
        while(mines.size() < MINE_COUNT){
            int num = rand.nextInt(80);
            if(!mines.contains(num)){
                mines.add(num);
            }
        }
    }


    private int detectMines(@NonNull int[] index){
        if(mines.contains((index[0]*8) + index[1])){
            return -1;
        } else {
            int count = 0;
            for(int i = index[0] - 1; i < index[0] + 2; i++){
                for(int j = index[1] - 1; j < index[1] + 2; j++){
                    if((i >= 0 && i < ROW_COUNT) && (j >= 0 && j < COLUMN_COUNT)){
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

        cell_tvs = new TextView[ROW_COUNT][COLUMN_COUNT];


        // Method (2): add four dynamically created cells
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j=0; j < COLUMN_COUNT; j++) {
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

        clock = 0;
        running = true;
        runTimer();
    }

    private int[] findIndexOfCellTextView(TextView tv) {
        int[] coords = new int[2];
        coords[0] = -1;
        coords[1] = -1;
        for(int i = 0; i < ROW_COUNT; i++){
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
        if(!running){
            startActivity(intent);
        } else {
            TextView tv = (TextView) view;
            int[] n = findIndexOfCellTextView(tv);
            if(flagging){
                if(tv.getCurrentTextColor() == Color.GREEN) {
                    tv.setText(R.string.flag);
                    tv.setTextColor(Color.GRAY);
                    TextView flags = (TextView) findViewById(R.id.textViewFlagCount);
                    flags.setText(String.valueOf(Integer.parseInt(flags.getText().toString())-1));
                } else if(tv.getText().toString().equals(getString(R.string.flag))){
                    tv.setText("");
                    tv.setTextColor(Color.GREEN);
                    TextView flags = (TextView) findViewById(R.id.textViewFlagCount);
                    flags.setText(String.valueOf(Integer.parseInt(flags.getText().toString())+1));
                }
            } else {
                reveal(n);
            }
            if(checkWin()){
                running = false;
                intent = new Intent(this, EndScreen.class);
                intent.putExtra("win",true);
                intent.putExtra("time",clock);
            }
        }
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

    private void reveal(int[] index){
        int i = index[0];
        int j = index[1];
        if(cell_tvs[i][j].getCurrentTextColor() == Color.GREEN){
            int mines = detectMines(index);
            if(mines == -1){
//                cell_tvs[i][j].setText(R.string.mine);
//                cell_tvs[i][j].setTextColor(Color.GRAY);
//                cell_tvs[i][j].setBackgroundColor(Color.RED);
                running = false;
                lose();
            } else if (mines > 0){
                cell_tvs[i][j].setText(String.valueOf(mines));
                cell_tvs[i][j].setTextColor(Color.GRAY);
                cell_tvs[i][j].setBackgroundColor(Color.LTGRAY);
            } else {
                cell_tvs[i][j].setText("");
                cell_tvs[i][j].setTextColor(Color.GRAY);
                cell_tvs[i][j].setBackgroundColor(Color.LTGRAY);
                for(int m = index[0] - 1; m < index[0] + 2; m++){
                    for(int n = index[1] - 1; n < index[1] + 2; n++){
                        if((m >= 0 && m < ROW_COUNT) && (n >= 0 && n < COLUMN_COUNT)){
                            int[] neighbor = new int[2];
                            neighbor[0] = m;
                            neighbor[1] = n;
                            reveal(neighbor);
                        }
                    }
                }
            }
        }
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.textViewTimeCount);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                timeView.setText(String.valueOf(clock));
                if(running){
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private boolean checkWin(){
        int count = 0;
        for(int i = 0; i < ROW_COUNT; i++){
            for(int j = 0; j < COLUMN_COUNT; j++){
                int color = ((ColorDrawable) cell_tvs[i][j].getBackground()).getColor();
                if(color == Color.parseColor("lime")){
                    count++;
                }
                if(color == Color.parseColor("red")){
                    return false;
                }
                if(count > MINE_COUNT){
                    return false;
                }
            }
        }
        return true;
    }

    private void lose(){
        int i, j;
        for(int num = 0; num < MINE_COUNT; num++){
            i = mines.get(num)/COLUMN_COUNT;
            j = mines.get(num)%COLUMN_COUNT;
            cell_tvs[i][j].setText(R.string.mine);
            cell_tvs[i][j].setTextColor(Color.GRAY);
            cell_tvs[i][j].setBackgroundColor(Color.RED);
        }

        intent = new Intent(this, EndScreen.class);
        intent.putExtra("win",false);
        intent.putExtra("time",clock);
    }
}