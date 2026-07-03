//邮局工作人员管理控制器，处理工作人员相关的HTTP请求（增删改查、登录等）
package com.example.demo.controller;

import com.example.demo.entity.PostalStaff;
import com.example.demo.service.PostalStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/postal-staff")
public class PostalStaffController {

    @Autowired
    private PostalStaffService postalStaffService;

    @GetMapping
    public List<PostalStaff> getAll() {
        return postalStaffService.getAll();
    }

    @GetMapping("/{id}")
    public PostalStaff getById(@PathVariable Integer id) {
        return postalStaffService.getById(id);
    }

    @GetMapping("/department/{department}")
    public List<PostalStaff> getByDepartment(@PathVariable String department) {
        return postalStaffService.getByDepartment(department);
    }

    @GetMapping("/status/{status}")
    public List<PostalStaff> getByStatus(@PathVariable String status) {
        return postalStaffService.getByStatus(status);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        PostalStaff staff = postalStaffService.login(email, password);
        Map<String, Object> result = new HashMap<>();
        if (staff != null) {
            result.put("success", true);
            result.put("staff", staff);
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "邮箱或密码错误");
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping
    public int add(@RequestBody PostalStaff postalStaff) {
        return postalStaffService.add(postalStaff);
    }

    @PutMapping("/{id}")
    public int update(@PathVariable Integer id, @RequestBody PostalStaff postalStaff) {
        postalStaff.setId(id);
        return postalStaffService.update(postalStaff);
    }

    @PutMapping("/{id}/status")
    public int updateStatus(@PathVariable Integer id, @RequestParam String status) {
        return postalStaffService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public int delete(@PathVariable Integer id) {
        return postalStaffService.delete(id);
    }
}
