package com.olabode.wilson.pytutor.ui.tutorial.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.olabode.wilson.pytutor.models.Topic
import com.olabode.wilson.pytutor.repository.main.tutorial.TutorialRepository
import com.olabode.wilson.pytutor.repository.main.user.UserRepository
import com.olabode.wilson.pytutor.utils.states.DataState

/**
 *   Created by OLABODE WILSON on 9/22/20.
 */
class CompletedLessonViewModel @ViewModelInject constructor(
        private val userRepository: UserRepository,
        private val tutorialRepository: TutorialRepository
) : ViewModel() {

    fun getNextTopic(topicId: String): LiveData<DataState<Topic>> {
        return tutorialRepository.getNextTopic(topicId).asLiveData()
    }

    fun onCourseCompleted(
            topicId: String,
            rating: Float,
            nextTopicId: String?
    ): LiveData<DataState<String>> {
        return userRepository.updateCourse(topicId, rating, nextTopicId).asLiveData()
    }
}