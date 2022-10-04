package com.example.application.views.main;

import com.example.application.model.ToDo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.awt.*;

public class EditForm extends FormLayout {
    Binder<ToDo> binder = new BeanValidationBinder<>(ToDo.class);
    private ToDo toDo;

    TextField title = new TextField("Task");
    ComboBox<Boolean> done = new ComboBox<>("Done");

    Button saveButton = new Button("Save");
    Button deleteButton = new Button("Delete");
    Button cancelButton = new Button("Cancel");

    public EditForm() {
        binder.bindInstanceFields(this);
        done.setItems(true, false);

        add(
            title,
            done,
            createButtonLayout()
        );
    }

    public String getTitle() {
        return title.getValue();
    }

    public void setToDo(ToDo toDo) {
        this.toDo = toDo;
        binder.readBean(toDo);
    }

    private Component createButtonLayout() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveButton.addClickShortcut(Key.ENTER);
        cancelButton.addClickShortcut(Key.ESCAPE);

        saveButton.addClickListener(event -> validateAndSave());
        deleteButton.addClickListener(event -> fireEvent(new DeleteEvent(this, toDo)));
        cancelButton.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(saveButton, deleteButton, cancelButton);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(toDo);
            fireEvent(new SaveEvent(this, toDo));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class EditFormEvent extends ComponentEvent<EditForm> {
        private ToDo toDo;

        protected EditFormEvent(EditForm source, ToDo toDo) {
            super(source, false);
            this.toDo = toDo;
        }

        public ToDo getToDo() {
            return toDo;
        }
    }

    public static class SaveEvent extends EditFormEvent {
        SaveEvent(EditForm source, ToDo toDo) {
            super(source, toDo);
        }
    }

    public static class DeleteEvent extends EditFormEvent {
        DeleteEvent(EditForm source, ToDo toDo) {
            super(source, toDo);
        }

    }

    public static class CloseEvent extends EditFormEvent {
        CloseEvent(EditForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
