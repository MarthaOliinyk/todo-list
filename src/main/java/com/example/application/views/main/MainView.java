package com.example.application.views.main;

import com.example.application.model.ToDo;
import com.example.application.service.ToDoService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("To Do List")
@Route("/todo")
public class MainView extends VerticalLayout {
    EditForm editForm;
    Grid<ToDo> toDoList = new Grid<>(ToDo.class);
    TextField taskField = new TextField();
    Button addButton = new Button("Add");
    private ToDoService toDoService;

    @Autowired
    public MainView(ToDoService toDoService) {
        this.toDoService = toDoService;
        setSizeFull();

        configureAddButton();
        configureTaskField();
        configureForm();
        toDoList.setSizeFull();

        add(
                new H1("To do list"),
                new HorizontalLayout(
                        taskField,
                        addButton
                ),
                getContent()
        );

        updateList();
        closeEditor();
    }

    private void configureAddButton() {
        addButton.addClickShortcut(Key.ENTER);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(click -> {
            toDoService.addToDo(new ToDo(taskField.getValue()));
            taskField.clear();
            updateList();
        });
    }

    private void configureTaskField() {
        taskField.setPlaceholder("Enter new task...");
        taskField.setClearButtonVisible(true);
        taskField.setWidth("60em");
    }

    private void closeEditor() {
        editForm.setToDo(null);
        editForm.setVisible(false);
    }

    private void updateList() {
        toDoList.setItems(toDoService.findAllToDo());
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(toDoList, editForm);
        content.setFlexGrow(2, toDoList);
        content.setFlexGrow(1, editForm);
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        editForm = new EditForm();
        editForm.setWidth("25em");
        toDoList.asSingleSelect().addValueChangeListener(e -> editToDo(e.getValue()));

        editForm.addListener(EditForm.SaveEvent.class, this::saveToDo);
        editForm.addListener(EditForm.DeleteEvent.class, this::deleteToDo);
        editForm.addListener(EditForm.CloseEvent.class, e -> closeEditor());
    }

    private void deleteToDo(EditForm.DeleteEvent event) {
        toDoService.deleteToDo(event.getToDo());
        updateList();
        closeEditor();
    }

    private void saveToDo(EditForm.SaveEvent event) {
        ToDo toDo = event.getToDo();
        toDoService.updateToDo(toDo.getId(), toDo.getTitle(), toDo.getDone());
        updateList();
        closeEditor();
    }

    private void editToDo(ToDo toDo) {
        if (toDo == null) {
            closeEditor();
        } else {
            editForm.setToDo(toDo);
            editForm.setVisible(true);
        }
    }
}
