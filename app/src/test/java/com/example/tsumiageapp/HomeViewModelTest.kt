package com.example.tsumiageapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tsumiageapp.data.db.AppDatabase
import com.example.tsumiageapp.data.db.GoalDao
import com.example.tsumiageapp.data.db.TaskDao
import com.example.tsumiageapp.data.model.Task
import com.example.tsumiageapp.data.repository.GoalRepositoryImpl
import com.example.tsumiageapp.data.repository.TaskRepositoryImpl
import com.example.tsumiageapp.ui.goal.GoalRepository
import com.example.tsumiageapp.ui.home.HomeViewModel
import com.example.tsumiageapp.ui.task.TaskRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Config(manifest=Config.NONE)
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    lateinit var db: AppDatabase

    lateinit var taskRepository: TaskRepository

    lateinit var taskDao: TaskDao

    lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        taskDao = db.taskDao()
        taskRepository = TaskRepositoryImpl(taskDao)

        val goalDao = db.goalDao()
        val goalRepository = GoalRepositoryImpl(goalDao)

        viewModel = HomeViewModel(taskRepository,goalRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        db.close()
    }

    @Test
    fun getTodayTasks() = runTest(testDispatcher) {
        val expectedTask = Task(id = 1,title = "テスト", categoryType = "CategoryType", categoryName = "CategoryName", time =  10, date = LocalDate.now().toString())
        taskRepository.addTask(expectedTask)

        viewModel.getTodayTasks()

        assertEquals(expectedTask,viewModel.todayTasksUiState.value.first())
    }
}