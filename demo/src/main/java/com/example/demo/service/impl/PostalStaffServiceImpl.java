//邮局工作人员业务实现类，实现工作人员增删改查等业务逻辑
package com.example.demo.service.impl;

import com.example.demo.entity.PostalStaff;
import com.example.demo.mapper.PostalStaffMapper;
import com.example.demo.service.PostalStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostalStaffServiceImpl implements PostalStaffService {

    @Autowired
    private PostalStaffMapper postalStaffMapper;

    @Override
    public List<PostalStaff> getAll() {
        return postalStaffMapper.findAll();
    }

    @Override
    public PostalStaff getById(Integer id) {
        return postalStaffMapper.findById(id);
    }

    @Override
    public PostalStaff getByEmail(String email) {
        return postalStaffMapper.findByEmail(email);
    }

    @Override
    public List<PostalStaff> getByDepartment(String department) {
        return postalStaffMapper.findByDepartment(department);
    }

    @Override
    public List<PostalStaff> getByStatus(String status) {
        return postalStaffMapper.findByStatus(status);
    }

    @Override
    public PostalStaff login(String email, String password) {
        PostalStaff staff = postalStaffMapper.findByEmail(email);
        if (staff != null && staff.getPassword().equals(password) && "在职".equals(staff.getStatus())) {
            return staff;
        }
        return null;
    }

    @Override
    public int add(PostalStaff postalStaff) {
        if (postalStaff.getStatus() == null) {
            postalStaff.setStatus("在职");
        }
        return postalStaffMapper.insert(postalStaff);
    }

    @Override
    public int update(PostalStaff postalStaff) {
        return postalStaffMapper.update(postalStaff);
    }

    @Override
    public int updateStatus(Integer id, String status) {
        return postalStaffMapper.updateStatus(id, status);
    }

    @Override
    public int delete(Integer id) {
        return postalStaffMapper.deleteById(id);
    }
}
