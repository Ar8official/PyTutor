package com.olabode.wilson.pytutor.ui.exercise

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.olabode.wilson.pytutor.R
import com.olabode.wilson.pytutor.databinding.FragmentExerciseContentBinding
import com.olabode.wilson.pytutor.extensions.navigateSafe
import com.olabode.wilson.pytutor.extensions.viewBinding

/**
 * Created by Ogheneruona Onobrakpeya on 10/10/20.
 */

class ExerciseFragment: Fragment(R.layout.fragment_exercise_content) {
    private val binding by viewBinding(FragmentExerciseContentBinding::bind)
    private val args: ExerciseFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        excludeLollipopInterpreter()

        val exercise = args.exercise
        binding.toolbar.title = exercise.title
        binding.questionText.text = exercise.question
        binding.solutionView.setCode(
            formattedSourceCode = exercise.solution,
            language = "py",
            showLineNumbers = true
        )

        binding.showSolutionButton.setOnClickListener {
            binding.solutionLayout.isVisible = !binding.solutionLayout.isVisible
        }

        binding.runCode.setOnClickListener {
            navigateSafe(ExerciseFragmentDirections.actionExerciseFragmentToCodeOutputFragment(exercise.solution))
        }

        binding.toolbar.setNavigationOnClickListener {findNavController().navigateUp()}
    }

    private fun excludeLollipopInterpreter() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            binding.runCode.isInvisible = true
        }
    }
}