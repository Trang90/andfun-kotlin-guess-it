package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

//Copy over the different buzz pattern Long array constants here
private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel : ViewModel(){

/**Make an enum called BuzzType - have a different buzz type for CORRECT, GAME_OVER
    COUNTDOWN_PANIC and NO_BUZZ. Also add a number of seconds to the companion object for when
    These are the three different types of buzzing in the game. Buzz pattern is the number of
     milliseconds each interval of buzzing and non-buzzing takes.*/
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }


    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 100L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 6000L
        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L
    }


    // The current word
    private var _word = MutableLiveData<String>()
    val word : LiveData<String>
    get()=_word

    // The current score
    private var _score = MutableLiveData<Int>()
    val score : LiveData<Int>
        get() = _score

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    private var _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
    get()=_eventGameFinish

    private val timer: CountDownTimer

    private var _currentTime = MutableLiveData<Long>()
    val currentTime : LiveData<Long>
    get()= _currentTime


    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    //Create a properly encapsulated LiveData for a buzz event - its' type should be BuzzType
    // Event that triggers the phone to buzz using different patterns, determined by BuzzType
    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz


    init {
       // Log.i("GameViewModel", "“GameViewModel created!")
        _eventGameFinish.value= false
        resetList()
        nextWord()
        _score.value =0

        // Creates a timer which triggers the end of the game when it finishes
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            // Set the value of buzz event to the correct BuzzType when the buzzer should
            // fire. This should happen when the game is over, when the user gets a correct answer,
            // and on each tick when countdown buzzing starts
            override fun onTick(millisUntilFinished: Long) {
                // TODO implement what should happen each tick of the timer
                _currentTime.value=(millisUntilFinished /ONE_SECOND)

                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                // TODO implement what should happen when the timer finishes
                _currentTime.value = DONE
                _eventGameFinish.value = true
                _eventBuzz.value = BuzzType.GAME_OVER
            }
        }

        timer.start()
    }



    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            /** gameFinished() should happen here*/
            //_eventGameFinish.value=true
    /** khi hết từ thì list sẽ reset và re-shuffled*/
        resetList()
        } else {
            _word.value = wordList.removeAt(0)
        }
    }

    /** Methods for buttons presses **/

     fun onSkip() {
        _score.value = (score.value)?.minus(1)//null safety checks
        nextWord()
    }

     fun onCorrect() {
         _score.value = (score.value)?.plus(1)
        nextWord()
    }

/** we need to tell the ViewModel that we've showed the toast or that the navigation has happened.
    tức là cho biết gameFinished() đã xong, ko cần tiếp tục show Toast hy navi mỗi khi xoay đt nữa*/
    fun onGameFinishComplete(){
     _eventGameFinish.value=false
 }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }

    //To avoid memory leaks, you should always cancel a CountDownTimer if you no longer need it
    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
}