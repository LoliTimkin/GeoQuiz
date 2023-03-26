package com.bignerdranch.android.geomain

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.ActivityOptions.*
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val INDEX_KEY = "index"
private const val REQUEST_CODE_CHEAT = 0
private const val MAX_COUNT_CHEAT = 3

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var counterCheatTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    var count: Int = 0;

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate(Bundle?) called")
        setContentView(R.layout.activity_main)


        val currentIndex = savedInstanceState?.getInt(INDEX_KEY, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)
        counterCheatTextView = findViewById(R.id.cheat_counter)

        updateQuestion()

        trueButton.setOnClickListener { view: View ->
            //currentIndex = (currentIndex + 1) % questionBank.size
            checkAnswer(true)
            quizViewModel.moveToNext()
            updateQuestion()
        }
        falseButton.setOnClickListener { view: View ->
            //currentIndex = (currentIndex + 1) % questionBank.size
            quizViewModel.moveToNext()
            checkAnswer(false)
            updateQuestion()
        }

        cheatButton.setOnClickListener{ view: View ->
            // начало CheatActivity
            //val intent = Intent(this, CheatActivity::class.java)
            updateCheatCounter()
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options =
                    ActivityOptions
                .makeClipRevealAnimation(view,0,0,view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
                Log.d(TAG,"startActivityForResult")
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }
        questionTextView.setOnClickListener {
            //currentIndex = (currentIndex + 1) % questionBank.size
            quizViewModel.moveToNext()
            updateQuestion()
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        //updateQuestion()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "OnStart() called")
    }

    override fun onResume() {

        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(INDEX_KEY, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "oonActivityResult() called")

        if (resultCode != Activity.RESULT_OK) {
            //return
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        } else  if (resultCode != Activity.RESULT_CANCELED) {quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false}

        else if (resultCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
        ///else  quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, true) ?: true
    }

    private fun updateQuestion() {
        //Log.d(TAG, "Updating question text", Exception())
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        trueButton.isClickable=true
        falseButton.isClickable=true
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer: Boolean = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show()
        // далее идёт доработка приложения
        if (quizViewModel.currentIndex <= quizViewModel.questionBank.size && userAnswer == quizViewModel.currentQuestionAnswer) {
            count++
        }
        if (userAnswer == true) falseButton.isClickable=false else trueButton.isClickable=false
        if (quizViewModel.currentIndex == quizViewModel.questionBank.size)
            Toast.makeText(this, "Количество правильных ответов: $count",Toast.LENGTH_SHORT)
    }

    private fun updateCheatCounter() {
        quizViewModel.counterCheat =  quizViewModel.counterCheat + 1
        var count = quizViewModel.counterCheat
        if ( MAX_COUNT_CHEAT <= quizViewModel.counterCheat ) cheatButton.isClickable=false
        counterCheatTextView.setText("$count")
    }



}