//报纸业务接口，定义报纸相关业务逻辑方法
package com.example.demo.service;

import com.example.demo.entity.Newspaper;
import java.util.List;

public interface NewspaperService {
    List<Newspaper> getAll();
    Newspaper getById(Integer id);
    List<Newspaper> getByCategoryId(Integer categoryId);
    List<Newspaper> getRecommended();
    List<Newspaper> getLowStock(Integer threshold);
    int add(Newspaper newspaper);
    int update(Newspaper newspaper);
    int updateStock(Integer id, Integer quantity);
    int delete(Integer id);
}