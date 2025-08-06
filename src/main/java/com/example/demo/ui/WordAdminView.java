package com.example.demo.ui;

import com.example.demo.model.Word;
import com.example.demo.service.WordService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A view for administrators to Create, Read, Update, and Delete (CRUD) words.
 * It displays a grid of words and a form to edit them.
 */
@Route("admin")
@PageTitle("Admin | Word Manager")
public class WordAdminView extends VerticalLayout {

    private final WordService wordService;
    private final Grid<Word> grid = new Grid<>(Word.class);
    private final WordForm form;

    public WordAdminView(@Autowired WordService wordService) {
        this.wordService = wordService;
        addClassName("word-admin-view");
        setSizeFull();

        // Configure the grid to display the words
        configureGrid();

        // Create the form and set up its event listeners
        form = new WordForm();
        form.addListener(WordForm.SaveEvent.class, this::saveWord);
        form.addListener(WordForm.DeleteEvent.class, this::deleteWord);
        form.addListener(WordForm.CloseEvent.class, e -> closeEditor());

        // Arrange the grid and form in a layout
        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        // Add a toolbar with an "Add" button
        add(getToolbar(), content);

        // Fetch the initial list of words
        updateList();

        // Start with the editor form closed
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("word-grid");
        grid.setSizeFull();
        // Define which columns to show and their order
        grid.setColumns("farsi", "english");
        // Allow columns to be auto-sized based on content
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        // When a user selects a row, open the editor form
        grid.asSingleSelect().addValueChangeListener(event ->
            editWord(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        Button addWordButton = new Button("Add new word");
        addWordButton.addClickListener(click -> addWord());

        HorizontalLayout toolbar = new HorizontalLayout(addWordButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    // Clears the selection and opens the form for a new word
    private void addWord() {
        grid.asSingleSelect().clear();
        editWord(new Word());
    }

    // Saves the word (either new or updated) to the database
    private void saveWord(WordForm.SaveEvent event) {
        wordService.saveWord(event.getWord());
        updateList();
        closeEditor();
    }

    // Deletes the selected word from the database
    private void deleteWord(WordForm.DeleteEvent event) {
        wordService.deleteWord(event.getWord());
        updateList();
        closeEditor();
    }

    // Populates the form with the data of the selected word
    public void editWord(Word word) {
        if (word == null) {
            closeEditor();
        } else {
            form.setWord(word);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    // Hides and resets the form
    private void closeEditor() {
        form.setWord(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    // Refreshes the grid with the latest data from the service
    private void updateList() {
        grid.setItems(wordService.findAll());
    }
}