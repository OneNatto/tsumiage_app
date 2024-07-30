package com.example.tsumiageapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tsumiageapp.data.db.AppDatabase
import com.example.tsumiageapp.data.db.GoalDao
import com.example.tsumiageapp.data.db.TaskDao
import com.example.tsumiageapp.data.model.Goal
import com.example.tsumiageapp.data.model.GoalTypeEnum
import com.example.tsumiageapp.data.model.Task
import com.example.tsumiageapp.data.repository.CategoryRepositoryImpl
import com.example.tsumiageapp.data.repository.GoalRepositoryImpl
import com.example.tsumiageapp.data.repository.TaskRepositoryImpl
import com.example.tsumiageapp.ui.category.CategoryRepository
import com.example.tsumiageapp.ui.goal.GoalRepository
import com.example.tsumiageapp.ui.goal.GoalViewModel
import com.example.tsumiageapp.ui.home.HomeViewModel
import com.example.tsumiageapp.ui.report.ReportViewModel
import com.example.tsumiageapp.ui.report.Screen
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
class GoalViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    lateinit var db: AppDatabase

    lateinit var goalRepository: GoalRepository

    lateinit var categoryRepository: CategoryRepository

    lateinit var goalDao: GoalDao

    lateinit var viewModel: GoalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        goalDao = db.goalDao()
        goalRepository = GoalRepositoryImpl(goalDao)

        categoryRepository = CategoryRepositoryImpl(db.categoryDao())

        viewModel = GoalViewModel(goalRepository,categoryRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        db.close()
    }

    @Test
    fun `get Goal test`() = runTest(testDispatcher) {
        val today = LocalDate.now()
        val expectedGoal = Goal(id = 1,title = "テスト", categoryType = "CategoryType", categoryName = "CategoryName", time =  10, goalType = GoalTypeEnum.TODAY.name, addDate = today.toString(), isDone = false)
        goalRepository.addGoal(expectedGoal)

        viewModel.getTodayGoalList()

        assertEquals(expectedGoal, viewModel.todayGoalList.value.first())
    }
}