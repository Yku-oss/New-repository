package com.example.oneday.server.impl;

import com.example.oneday.entity.worker;
import com.example.oneday.mapper.workermappings;
import com.example.oneday.server.workerserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class workerserverImpl implements workerserver {

    @Autowired
    private workermappings workermappings;

    @Override
    public List<worker> findAll() {
        return workermappings.findAll();
    }

    @Override
    public worker getid(Integer id) {
        return workermappings.getid(id);
    }

    @Override
    public worker getEmail(String email) {
        return workermappings.getEmail(email);
    }

    @Override
    public int add(worker worker) {
        return workermappings.insert(worker);
    }

    @Override
    public int update(worker worker) {
        return workermappings.update(worker);
    }

    @Override
    public int updateStatus(Integer id, String status) {
        return workermappings.updateStatus(id, status);
    }

    @Override
    public int delete(Integer id) {
        return workermappings.deleteById(id);
    }
}
