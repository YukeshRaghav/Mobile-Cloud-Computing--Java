package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button0, buttonClear;
    TextView price, _20$, _10$, _5$, _1$, _25C, _10C, _5C, _1C;
    String num = "0";
    double result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0 = findViewById(R.id.button_0);
        button1 = findViewById(R.id.button_1);
        button2 = findViewById(R.id.button_2);
        button3 = findViewById(R.id.button_3);
        button4 = findViewById(R.id.button_4);
        button5 = findViewById(R.id.button_5);
        button6 = findViewById(R.id.button_6);
        button7 = findViewById(R.id.button_7);
        button8 = findViewById(R.id.button_8);
        button9 = findViewById(R.id.button_9);
        buttonClear = findViewById(R.id.button_clear);
        _1$ = findViewById(R.id.textView_1);
        _5$ = findViewById(R.id.textView_5);
        _10$ = findViewById(R.id.textView_10);
        _20$ = findViewById(R.id.textView_20);
        _25C = findViewById(R.id.textView_25C);
        _10C = findViewById(R.id.textView_10c);
        _5C = findViewById(R.id.textView_5c);
        _1C = findViewById(R.id.textView_1c);
        price = findViewById(R.id.textView2);

        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.button_0 ||
                viewId == R.id.button_1 ||
                viewId == R.id.button_2 ||
                viewId == R.id.button_3 ||
                viewId == R.id.button_4 ||
                viewId == R.id.button_5 ||
                viewId == R.id.button_6 ||
                viewId == R.id.button_7 ||
                viewId == R.id.button_8 ||
                viewId == R.id.button_9) {
            handleDigitButtonClick(((Button) view).getText().toString());
        } else if (viewId == R.id.button_clear) {
            clearAmount();
        }
    }

    private void handleDigitButtonClick(String digit) {
        num += digit;
        result = Double.parseDouble(num) / 100;
        updatePrice();
        updateChangeTable();
    }

    private void updatePrice() {
        price.setText("Price: " + String.format("%.2f", result));
    }

    private void updateChangeTable() {
        int cents = (int) (result * 100);

        int dollars20 = cents / 2000;
        cents %= 2000;
        int dollars10 = cents / 1000;
        cents %= 1000;
        int dollars5 = cents / 500;
        cents %= 500;
        int dollars1 = cents / 100;
        cents %= 100;
        int quarters = cents / 25;
        cents %= 25;
        int dimes = cents / 10;
        cents %= 10;
        int nickels = cents / 5;
        int pennies = cents % 5;

        _20$.setText("$20: " + dollars20);
        _10$.setText("$10: " + dollars10);
        _5$.setText("$5: " + dollars5);
        _1$.setText("$1: " + dollars1);
        _25C.setText("25C: " + quarters);
        _10C.setText("10C: " + dimes);
        _5C.setText("5C: " + nickels);
        _1C.setText("1C: " + pennies);
    }

    private void clearAmount() {
        num = "0";
        result = 0;
        updatePrice();
        updateChangeTable();
    }
}