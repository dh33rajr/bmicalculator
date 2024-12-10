package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private TextView resultText, healthyRangeText, healthyWeightText, bmiPrimeText, ponderalIndexText;
    private TextInputEditText heightInput, weightInput, ageInput;
    private RadioGroup genderGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();
        setupButtons();
    }

    private void initializeViews() {
        ageInput = findViewById(R.id.age);
        heightInput = findViewById(R.id.height);
        weightInput = findViewById(R.id.weight);
        genderGroup = findViewById(R.id.gender_group);
        resultText = findViewById(R.id.result_text);
        healthyRangeText = findViewById(R.id.healthy_range);
        healthyWeightText = findViewById(R.id.healthy_weight);
        bmiPrimeText = findViewById(R.id.bmi_prime);
        ponderalIndexText = findViewById(R.id.ponderal_index);
    }

    private void setupButtons() {
        Button calculateButton = findViewById(R.id.calculate_button);
        Button clearButton = findViewById(R.id.clear_button);

        calculateButton.setOnClickListener(v -> calculateBMI());
        clearButton.setOnClickListener(v -> clearInputs());
    }

    private void calculateBMI() {
        String ageStr = ageInput.getText().toString().trim();
        String heightStr = heightInput.getText().toString().trim();
        String weightStr = weightInput.getText().toString().trim();

        if (ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            showError("Please fill all fields correctly ❌");
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            if (age < 2 || age > 120) {
                showError("Age must be between 2 and 120 years ❌");
                return;
            }

            double height = Double.parseDouble(heightStr) / 100; // cm to meters
            double weight = Double.parseDouble(weightStr);
            double bmi = weight / (height * height);
            displayResults(bmi, height, weight);

            // Close the keyboard
            closeKeyboard();

        } catch (NumberFormatException e) {
            showError("Invalid input! Enter numeric values ❌");
        }
    }

    private void displayResults(double bmi, double height, double weight) {
        String category = getBMICategory(bmi);
        String emoji = getBMIEmoji(bmi);
        int textColor = getBMIColor(bmi);

        // Calculate additional metrics
        double bmiPrime = bmi / 25.0;
        double ponderalIndex = weight / Math.pow(height, 3);
        double minHealthyWeight = 18.5 * (height * height);
        double maxHealthyWeight = 25 * (height * height);

        // Display results
        resultText.setTextColor(textColor);
        resultText.setText(String.format("BMI = %.1f kg/m² (%s) %s", bmi, category, emoji));
        healthyRangeText.setText("Healthy BMI range: 18.5 - 25 kg/m²");
        healthyWeightText.setText(String.format("Healthy weight range: %.1f - %.1f kg", 
            minHealthyWeight, maxHealthyWeight));
        bmiPrimeText.setText(String.format("BMI Prime: %.1f", bmiPrime));
        ponderalIndexText.setText(String.format("Ponderal Index: %.1f kg/m³", ponderalIndex));
    }

    private void clearInputs() {
        ageInput.setText("");
        heightInput.setText("");
        weightInput.setText("");
        genderGroup.check(R.id.male); // Reset to male
        clearResults();
    }

    private void clearResults() {
        resultText.setText("");
        healthyRangeText.setText("");
        healthyWeightText.setText("");
        bmiPrimeText.setText("");
        ponderalIndexText.setText("");
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }

    private String getBMIEmoji(double bmi) {
        if (bmi < 18.5) return "😟";
        if (bmi < 25) return "😊";
        if (bmi < 30) return "😐";
        return "😟";
    }

    private int getBMIColor(double bmi) {
        if (bmi < 18.5) return Color.RED;
        if (bmi < 25) return Color.GREEN;
        if (bmi < 30) return Color.parseColor("#FFA500"); // Orange
        return Color.RED;
    }

    private void showError(String message) {
        resultText.setText(message);
        resultText.setTextColor(Color.RED);
        clearResults();
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
