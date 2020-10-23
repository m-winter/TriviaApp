package com.example.triviaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapp.controller.AppController;
import com.example.triviaapp.data.AnswerListAsyncResponse;
import com.example.triviaapp.data.QuestionBank;
import com.example.triviaapp.model.Question;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private TextView scoreTextView;
    private TextView highScoreTextView;
    private int score = 0;
    private int highScore;
    String scoreString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.scoreTextView);
        highScoreTextView = findViewById(R.id.highScoreTextView);

        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);

        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);

        questionTextView = findViewById(R.id.question_text_view);
        questionCounterTextView = findViewById(R.id.counter_text);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        scoreTextView.setText("Score: " + score);



         questionList =  new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextView.setText(currentQuestionIndex + "/" + questionList.size());
                Log.d("inside", "processFinished: " + questionArrayList);
            }
        });



         SharedPreferences getData = getSharedPreferences("MESSAGE_ID", MODE_PRIVATE);
         SharedPreferences getSecondData = getSharedPreferences("MESSAGE_ID", MODE_PRIVATE);
         currentQuestionIndex = getSecondData.getInt("lasPage", currentQuestionIndex);
         highScore = getData.getInt("highScore", highScore);
         highScoreTextView.setText("Your best: " + highScore);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.prev_button:
                if (currentQuestionIndex > 0){
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;

            case R.id.next_button:

                currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
                updateQuestion();
                break;

            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;

            case R.id.false_button:

                checkAnswer(false);
                updateQuestion();
                break;
        }


        SharedPreferences sharedPreferences = getSharedPreferences("MESSAGE_ID", MODE_PRIVATE);
        SharedPreferences secondsharedPreferences = getSharedPreferences("MESSAGE_ID", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor secondeditor = secondsharedPreferences.edit();

        secondeditor.putInt("lasPage", currentQuestionIndex);
        editor.putInt("highScore", highScore);

        editor.apply();
        secondeditor.apply();


    }

    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (userChooseCorrect == answerIsTrue){
            fadeView();
            toastMessageId = R.string.correct_answer;
            score = score + 100;
            updateScore();
            currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
            updateQuestion();

            if(score > highScore){
                highScore = score;
                updateHighScore();
            }

        }
        else{
            shakeAnimation();
            toastMessageId = R.string.wrong_answer;
            score = 0;
            updateScore();



        }

        Toast.makeText(MainActivity.this, toastMessageId,
                Toast.LENGTH_SHORT).show();
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(currentQuestionIndex + "/" + questionList.size());
    }
    private void updateScore(){
        scoreTextView.setText("Score: " + score);


    }
    private void updateHighScore(){
        highScoreTextView.setText("Your best: " + highScore);
    }




    private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }



    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView= findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

}