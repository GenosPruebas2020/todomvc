package com.autentia.training.selenium.todomvc.main;

import com.autentia.training.selenium.todomvc.drivers.Browser;
import com.autentia.training.selenium.todomvc.drivers.Chrome;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TodoMvcFlowTest {

    private TodoMvcFlow flow;
    private TodoMvcPage page;

    @BeforeEach
    void setUp() {
        // Don't try with Firefox as for the moment doubleClick event is not implemented correctly, causing the edit to do test to fail
        final Browser browser = new Chrome();
        final WebDriver driver = browser.getDriver();

        page = new TodoMvcPage(driver);
        flow = new TodoMvcFlow(page);
    }

    @AfterEach
    void tearDown() {
        page.quit();
    }

    @Test
    @DisplayName("it should load the page correctly")
    void testLoadingCapabilities() {
        assertEquals("TodoMVC", page.getTitle());
    }

    @Test
    @DisplayName("it should create a new todo")
    void testCreateTodo() {
        flow.createTodo("foo");

        assertEquals("foo", page.getActiveTodosText().get(0));
    }

    @Test
    @DisplayName("it should edit an already created todo")
    void testEditTodo() {
        // Does not work on Firefox 53 due to an error implemented on geckodriver regarding dbclick event not firing
        // https://github.com/mozilla/geckodriver/issues/661
        flow.createTodo("foo");
        flow.createTodo("bar");
        flow.editTodo(1, "baz");

        assertEquals("baz", page.getActiveTodosText().get(1));
    }

    @Test
    @DisplayName("it should delete a given todo")
    void testDeleteTodo() {
        flow.createTodo("foo");
        flow.deleteTodo(0);

        assertEquals(0, page.getActiveTodosText().size());
    }

    @Test
    @DisplayName("it should complete a given tddo ")
    void testCompleteTodo() {
        flow.createTodo("foo");
        flow.createTodo("bar");
        flow.createTodo("baz");
        flow.completeTodo(1);

        assertEquals(1, page.getCompletedTodos().size());
    }

    @Nested
    @DisplayName("it should filter between active and completed todos")
    class FilterTodosTest {

        @Test
        @DisplayName("it should filter active todos")
        void testFilterActiveTodos() {
            flow.createTodo("foo");
            flow.createTodo("bar");
            flow.createTodo("baz");
            flow.completeTodo(1);
            flow.filterActiveTodos();

            assertEquals(2, page.getActiveTodosText().size());
        }

        @Test
        @DisplayName("it should filter completed todos")
        void testFilterCompletedTodos() {
            flow.createTodo("foo");
            flow.createTodo("bar");
            flow.createTodo("baz");
            flow.completeTodo(1);
            flow.filterCompletedTodos();

            assertEquals(1, page.getCompletedTodos().size());
        }

    }

    @Test
    @DisplayName("it should clear completed todos")
    void testClearCompletedTodos() {
        flow.createTodo("foo");
        flow.createTodo("bar");
        flow.completeTodo(0);
        flow.clearCompletedTodos();

        assertEquals(0, page.getCompletedTodos().size());
    }

}