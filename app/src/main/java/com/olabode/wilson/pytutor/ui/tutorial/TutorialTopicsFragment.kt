package com.olabode.wilson.pytutor.ui.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.olabode.wilson.pytutor.R
import com.olabode.wilson.pytutor.databinding.FragmentTutorialTopicsBinding
import com.olabode.wilson.pytutor.extensions.hide
import com.olabode.wilson.pytutor.extensions.show
import com.olabode.wilson.pytutor.ui.tutorial.adapters.TutorialTopicAdapter
import com.olabode.wilson.pytutor.ui.tutorial.viewmodel.TutorialTopicViewModel
import com.olabode.wilson.pytutor.utils.EventObserver
import com.olabode.wilson.pytutor.extensions.navigateSafe
import com.olabode.wilson.pytutor.utils.states.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialTopicsFragment : Fragment(R.layout.fragment_tutorial_topics) {

    private var _binding: FragmentTutorialTopicsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TutorialTopicViewModel by viewModels()
    private lateinit var adapter: TutorialTopicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTutorialTopicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.topicsRecycler.adapter = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TutorialTopicAdapter { topic, message ->
            topic?.let {
                navigateSafe(
                    TutorialTopicsFragmentDirections
                        .actionTutorialTopicsFragmentToViewTutorialsFragment(
                            title = topic.title,
                            topic = topic
                        )
                )
            }

            message?.let { viewModel.showSnackBar(it) }

        }
        binding.topicsRecycler.adapter = adapter

        initTopics()

        viewModel.showSnackBar.observe(viewLifecycleOwner, EventObserver {
            showSnackBar(it, binding.coordinatorLayout)
        })

        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun showSnackBar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showPersistentSnackBar(message: String, view: View, action: () -> Unit) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) {
                action.invoke()
            }.show()
    }

    private fun initTopics() {
        viewModel.fetchTopics().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is DataState.Success -> {
                    binding.mainPage.show()
                    binding.noInternetState.root.hide()
                    binding.progressBar.hide()
                }
                is DataState.Error -> {
                    binding.mainPage.hide()
                    binding.progressBar.hide()
                    binding.noInternetState.root.show()
                    showPersistentSnackBar(result.message, binding.coordinatorLayout) {
                        initTopics()
                    }
                }

                is DataState.Loading -> {
                    binding.noInternetState.root.hide()
                    binding.progressBar.show()
                }
            }
        })

        viewModel.topics.observe(viewLifecycleOwner, Observer { topics ->
            topics?.let {
                adapter.submitList(topics.sortedBy { it.orderKey })
            }
        })
    }
}