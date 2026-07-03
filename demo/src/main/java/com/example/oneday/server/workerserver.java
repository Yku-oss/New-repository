package com.example.oneday.server;

import com.example.oneday.entity.worker;
import java.util.List;

public interface workerserver {
    List<worker> findAll();
    worker getid(Integer id);
    worker getEmail(String email);
    int add(worker worker);
    int update(worker worker);
    int updateStatus(Integer id, String status);
    int delete(Integer id);
}
