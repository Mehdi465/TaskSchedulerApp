package com.example.taskscheduler.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var taskDao: TaskDao
    private lateinit var db: TaskDatabase
    private lateinit var context: Context


    private val task1 = Task(name = "Morning Jog", priority = Priority.MEDIUM)
    private val task2 = Task(name = "Project Meeting", priority = Priority.HIGH)
    private val task3 = Task(name = "Read Book", priority = Priority.LOW)


    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertTaskAndGetById() = runBlocking {
        val insertedRowId = taskDao.insert(task1)
        assertTrue("Insert should return a valid row ID", insertedRowId > 0L)

        val retrievedTask = taskDao.getTask(insertedRowId.toInt()).first()

        assertNotNull("Retrieved task should not be null", retrievedTask)
        assertEquals("Task ID does not match", insertedRowId.toInt(), retrievedTask!!.id)
        assertEquals("Task name does not match", task1.name, retrievedTask.name)
        assertEquals("Task priority does not match", task1.priority, retrievedTask.priority)
    }

    @Test
    @Throws(Exception::class)
    fun insertMultipleTasksAndGetAll() = runBlocking {
        taskDao.insert(task1)
        taskDao.insert(task2)
        taskDao.insert(task3)

        val allTasks = taskDao.getAllTasks().first()
        assertEquals("Should be 3 tasks in the database", 3, allTasks.size)

        assertEquals(task1.name, allTasks[0].name)
        assertEquals(task2.name, allTasks[1].name)
        assertEquals(task3.name, allTasks[2].name)
    }

    @Test
    @Throws(Exception::class)
    fun updateTaskAndGetById() = runBlocking {
        val insertedRowId = taskDao.insert(task1)
        assertTrue(insertedRowId > 0L)

        val taskToUpdate = Task(
            id = insertedRowId.toInt(),
            name = "Evening Walk",
            priority = Priority.LOW
        )
        taskDao.update(taskToUpdate)

        val retrievedTask = taskDao.getTask(insertedRowId.toInt()).first()
        assertNotNull(retrievedTask)
        assertEquals("Updated task name does not match", "Evening Walk", retrievedTask!!.name)
        assertEquals("Updated task priority does not match", Priority.LOW, retrievedTask.priority)
    }

    @Test
    @Throws(Exception::class)
    fun deleteTaskAndVerifyNotPresent() = runBlocking {
        val rowId1 = taskDao.insert(task1)
        val rowId2 = taskDao.insert(task2)
        assertTrue(rowId1 > 0L)
        assertTrue(rowId2 > 0L)


        val taskToDelete = Task(id = rowId1.toInt(), name = task1.name, priority = task1.priority)
        taskDao.delete(taskToDelete)

        val task1AfterDelete = taskDao.getTask(rowId1.toInt()).first()
        assertNull("Deleted task should not be found", task1AfterDelete)

        val task2AfterDelete = taskDao.getTask(rowId2.toInt()).first()
        assertNotNull("Other tasks should still exist", task2AfterDelete)

        val allTasks = taskDao.getAllTasks().first()
        assertEquals("There should be 1 task remaining", 1, allTasks.size)
        assertEquals(task2.name, allTasks[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun getTask_whenTaskDoesNotExist_returnsNull() = runBlocking {
        val nonExistentTaskId = 999
        val retrievedTask = taskDao.getTask(nonExistentTaskId).first()
        assertNull("Querying for a non-existent task should return null", retrievedTask)
    }
}