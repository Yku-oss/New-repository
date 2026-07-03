//邮局工作人员实体类，包含工作人员基本信息（姓名、电话、职位、部门等）
package com.example.demo.entity;

import java.time.LocalDateTime;

public class PostalStaff {
    private Integer id; //Integer是可以为null的，int是不可以为null的
    private String name;
    private String phone;
    private String email;
    private String password;
    private String position;
    private String department;
    private String status;       // 在职/离职
    private LocalDateTime createTime;

    public Integer getId() { return id; } // 存入id
    public void setId(Integer id) { this.id = id; } // 取出id
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
