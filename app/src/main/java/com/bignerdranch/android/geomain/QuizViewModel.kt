package com.bignerdranch.android.geomain

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
class QuizViewModel: ViewModel() {

  //  init {
  //      Log.d(TAG, "ViewModel instance created")
  //  }
  //
  //  override fun onCleared() {
  //      super.onCleared()
  //      Log.d(TAG, "ViewModel instance about to be destroyed")
  //  }
    var currentIndex = 0
    var isCheater = false
    var counterCheat = 0

    val questionBank = listOf(
        Questions(R.string.question_australia, true),
        Questions(R.string.question_oceans, true),
        Questions(R.string.question_mideast, false),
        Questions(R.string.question_africa, false)
    )

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

}