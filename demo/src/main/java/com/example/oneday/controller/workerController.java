package com.example.oneday.controller;

import com.example.oneday.entity.worker;
import com.example.oneday.server.workerserver;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worker")
public class workerController {

    @Autowired
    private workerserver workerserver;

    @GetMapping
    public List<worker> findAll() {
        return workerserver.findAll();
    }

    @GetMapping("/{id}")
    public worker getid(@PathVariable Integer id) {
        return workerserver.getid(id);
    }

    @GetMapping("/email/{email}")
    public worker getEmail(@PathVariable String email) {
        return workerserver.getEmail(email);
    }

    @PostMapping
    public int add(@Valid @RequestBody worker worker) {
        return workerserver.add(worker);
    }

    @PutMapping
    public int update(@Valid @RequestBody worker worker) {
        return workerserver.update(worker);
    }

    @PutMapping("/status")
    public int updateStatus(@RequestParam Integer id, @RequestParam String status) {
        return workerserver.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public int delete(@PathVariable Integer id) {
        return workerserver.delete(id);
    }
}
