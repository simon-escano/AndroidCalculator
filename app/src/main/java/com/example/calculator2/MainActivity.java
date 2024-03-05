package com.example.calculator2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StringBuilder equation = new StringBuilder();

        final boolean[] parIsOpen = {false};

        TextView txtEquation = findViewById(R.id.txtEquation);
        TextView txtResult = findViewById(R.id.txtResult);

        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(findViewById(R.id.btnNum0));
        buttons.add(findViewById(R.id.btnNum1));
        buttons.add(findViewById(R.id.btnNum2));
        buttons.add(findViewById(R.id.btnNum3));
        buttons.add(findViewById(R.id.btnNum4));
        buttons.add(findViewById(R.id.btnNum5));
        buttons.add(findViewById(R.id.btnNum6));
        buttons.add(findViewById(R.id.btnNum7));
        buttons.add(findViewById(R.id.btnNum8));
        buttons.add(findViewById(R.id.btnNum9));
        buttons.add(findViewById(R.id.btnDiv));
        buttons.add(findViewById(R.id.btnMul));
        buttons.add(findViewById(R.id.btnSub));
        buttons.add(findViewById(R.id.btnAdd));
        buttons.add(findViewById(R.id.btnDot));

        Button btnAllClr = findViewById(R.id.btnAllClr);
        Button btnClr = findViewById(R.id.btnClr);
        Button btnEqu = findViewById(R.id.btnEqu);
        Button btnPar = findViewById(R.id.btnPar);

        for (Button button : buttons) {
            button.setOnClickListener(v -> {
                CharSequence text = button.getText();
                if (text.equals("+") || text.equals("-") || text.equals("*") || text.equals("/")) {
                    equation.append(" ").append(button.getText()).append(" ");
                } else {
                    equation.append(button.getText());
                }
                txtResult.setText(equation);
                txtEquation.setText(calculatePartial(equation.toString()));
            });
        }

        btnAllClr.setOnClickListener(v -> {
            equation.setLength(0);
            txtEquation.setText("");
            txtResult.setText("");
        });

        btnClr.setOnClickListener(v -> {
            switch (equation.charAt(equation.length() - 1)) {
                case ' ':
                    for (int i = 0; i < 3; i++) {
                        equation.deleteCharAt(equation.length() - 1);
                    }
                    break;
                case '(':
                    parIsOpen[0] = false;
                    equation.deleteCharAt(equation.length() - 1);
                    break;
                case ')':
                    parIsOpen[0] = true;
                    equation.deleteCharAt(equation.length() - 1);
                    break;
                default:
                    equation.deleteCharAt(equation.length() - 1);
            }
            txtResult.setText(equation);
            txtEquation.setText(calculatePartial(equation.toString()));
        });

        btnEqu.setOnClickListener(v -> {
            txtEquation.setText(equation);
            txtResult.setText(calculate(equation.toString()));
        });

        btnPar.setOnClickListener(v -> {
            if (parIsOpen[0]) {
                equation.append(")");
            } else {
                equation.append("(");
            }
            parIsOpen[0] = !parIsOpen[0];
            txtResult.setText(equation);
        });

    }

    public String calculatePartial(String equation) {
        if (calculate(equation).equals("Syntax Error")) return "";
        equation = equation.replaceAll("[()]", "");
        String[] operations = equation.split("\\s+");
        double result = Double.parseDouble(operations[0]);

        for (int i = 1; i < operations.length; i += 2) {
            String operator = operations[i];
            double operand = Double.parseDouble(operations[i + 1]);
            switch (operator) {
                case "+":
                    result += operand;
                    break;
                case "-":
                    result -= operand;
                    break;
                case "*":
                    result *= operand;
                    break;
                case "/":
                    if (operand != 0) {
                        result /= operand;
                    }
                    break;
            }
        }

        String formattedResult = String.valueOf(result);
        if (formattedResult.endsWith(".0")) {
            formattedResult = formattedResult.substring(0, formattedResult.length() - 2);
        }
        return formattedResult;
    }

    public String calculate(String equation) {
        JexlEngine jexl = new JexlBuilder().create();
        try {
            JexlExpression expression = jexl.createExpression(equation);
            JexlContext context = new MapContext();
            Object result = expression.evaluate(context);
            return result.toString();
        } catch (Exception e) {
            return "Syntax Error";
        }
    }
}