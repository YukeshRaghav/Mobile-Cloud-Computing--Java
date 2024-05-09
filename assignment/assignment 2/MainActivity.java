package com.example.assignment1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView amountTextView;
    private GridLayout numericKeyboard;
    private Button clearButton;
    private TableLayout changeTable;

    private double currentAmount = 0.00;
    private String money_str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText et1 = (EditText)findViewById(R.)
//        numericKeyboard = findViewById(R.id.numericKeyboard);
//        clearButton = findViewById(R.id.clearButton);
//        changeTable = findViewById(R.id.changeTable);

        setupNumericKeyboard();
        setupClearButton();
    }

    private void setupNumericKeyboard() {
        for (int i = 0; i < numericKeyboard.getChildCount(); i++) {
            View view = numericKeyboard.getChildAt(i);
            if (view instanceof Button) {
                Button digitButton = (Button) view;
                digitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleDigitButtonClick(digitButton.getText().toString());
                    }
                });
            }
        }
    }

    private void setupClearButton() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAmount();
            }
        });
    }

    private void handleDigitButtonClick(String digit) {
        money_str += digit;
        currentAmount = Double.parseDouble(money_str) / 100.0;
        updateAmountTextView();
        calculation();
    }

    private void updateAmountTextView() {
        amountTextView.setText(String.format("Price: %.2f", currentAmount));
    }

    private void calculation() {
        updateChangeTable();
    }

    private void updateChangeTable() {

        TableRow row = new TableRow(this);

        changeTable.addView(row);
    }

    private void clearAmount() {
        money_str = "";
        currentAmount = 0.00;
        updateAmountTextView();
        changeTable.removeAllViews();
    }
}
