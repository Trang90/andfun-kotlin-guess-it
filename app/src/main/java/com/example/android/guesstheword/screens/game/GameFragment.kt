/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        Log.i("GameFragment", "Called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        //BT 5.8
        binding.gameViewModel=viewModel

        /**BT 5.9*/
/**Specify the current activity as the lifecycle owner of the binding. This is used so that
        the binding can observe LiveData updates*/
        binding.lifecycleOwner = this



        /**BT 5.7*/
//        binding.correctButton.setOnClickListener { viewModel.onCorrect()
//
        /**BT 5.2*/
//            updateScoreText()
//            updateWordText()
//        }

        /**BT 5.7*/
//      binding.skipButton.setOnClickListener { viewModel.onSkip()
//            BT 5.2
//            updateScoreText()
//            updateWordText()
//        }


        /** Sau khi thêm dòng binding.lifecycleOwner thì remove được 2 Observer của word và score*/
//        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
//            binding.scoreText.text = newScore.toString()
//        })

//        viewModel.word.observe(viewLifecycleOwner, Observer { newWord ->
//            binding.wordText.text = newWord
//        })


        /** Sau khi thêm dòng Transformations.map ở gameViewModel thì remove được Observer của currentTime*/
//        viewModel.currentTime.observe(viewLifecycleOwner, Observer { newTime ->
//            binding.timerText.text= DateUtils.formatElapsedTime(newTime)
//        })

        /**boolean holding the current value of eventGameFinished is true. This means the game has finished.*/
       // viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { hasFinished ->
        //    if (hasFinished){
        //        gameFinished()
        //        viewModel.onGameFinishComplete()
        //    }
       // })

        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { isFinished ->
            if (isFinished){
                val currentScore = viewModel.score.value ?: 0
                val action = GameFragmentDirections.actionGameToScore(currentScore)
                findNavController(this).navigate(action)
/**Tell the view model that you've handled the game finished event by calling onGameFinishComplete*/
                viewModel.onGameFinishComplete()
            }
        })

/**Created an observer for the buzz event which calls the buzz method with the
               correct pattern. Remember to call onBuzzComplete!
                Buzzes when triggered with different buzz events*/
                viewModel.eventBuzz.observe(viewLifecycleOwner, Observer { buzzType ->
                    if (buzzType != GameViewModel.BuzzType.NO_BUZZ) {
                        buzz(buzzType.pattern)
                        viewModel.onBuzzComplete()
                    }
                })

/**BT 5.2*/
        //updateScoreText()
       // updateWordText()

        return binding.root

    }

    /**
     * Given a pattern, this method makes sure the device buzzes
     */
    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()
        buzzer?.let {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }

    /**
     * Called when the game is finished
     */
     fun gameFinished() {
        //if viewModel.score.value is not null then passing integer value, otherwise is 0
       val action = GameFragmentDirections.actionGameToScore(viewModel.score.value ?: 0)

        findNavController(this).navigate(action)
    }


    /** Methods for updating the UI **/

    /* BT 5.2
    private fun updateWordText() {
        binding.wordText.text = viewModel.word
    }

    private fun updateScoreText() {
        binding.scoreText.text = viewModel.score.toString()
    }

     */



}
