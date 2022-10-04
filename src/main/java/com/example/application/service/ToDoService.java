package com.example.application.service;

import com.example.application.model.ToDo;
import com.example.application.repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class ToDoService {

    private ToDoRepository toDoRepository;

    @Autowired
    public ToDoService(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    public List<ToDo> findAllToDo() {
        return toDoRepository.findAll();
    }

    public void addToDo(ToDo toDo) {
        toDoRepository.save(toDo);
    }

    public void deleteToDo(ToDo toDo) {
        toDoRepository.delete(toDo);
    }

    @Transactional
    public void updateToDo(Long id, String title, Boolean done) {
        ToDo toDo = toDoRepository.findById(id).orElseThrow(() -> new IllegalStateException(
                "toDo with id " + id + " does not exist"));

        if (title != null && title.length() > 0 && !Objects.equals(toDo.getTitle(), title)) {
            toDo.setTitle(title);
        }

        if (done != null && !Objects.equals(toDo.getDone(), done)) {
            toDo.setDone(done);
        }
    }
}
