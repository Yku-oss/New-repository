//邮局工作人员业务接口，定义工作人员相关业务逻辑方法
package com.example.demo.service;

import com.example.demo.entity.PostalStaff;
import java.util.List;

public interface PostalStaffService {
    List<PostalStaff> getAll();
    PostalStaff getById(Integer id);
    PostalStaff getByEmail(String email);
    List<PostalStaff> getByDepartment(String department);
    List<PostalStaff> getByStatus(String status);
    PostalStaff login(String email, String password);
    int add(PostalStaff postalStaff);
    int update(PostalStaff postalStaff);
    int updateStatus(Integer id, String status);
    int delete(Integer id);
}
