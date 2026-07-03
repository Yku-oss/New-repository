package com.example.oneday.entity;
//       javaEE    验证       约束   发送邮件类
import jakarta.validation.constraints.Email; //引入javaEE验证包，用于email验证
import jakarta.validation.constraints.NotBlank; //引入接口包
import jakarta.validation.constraints.Size; // 校验字符串或集合等长度是否在指定范围

import java.time.LocalDateTime;


public class worker {
       
        @NotBlank(message = "姓名不能为空")
        private String name;
        @NotBlank(message = "电话不能为空")
        private String phone;
        @Email(message = "邮箱格式不正确")
        private String email;
        
        private Integer id;
        private String position;
        private String password;
        private String department;
        private String status;
        private LocalDateTime createTime;

        public Integer getid(){return id;}
        public void setid(Integer id){this.id = id;}
        public String getname(){return name;}
        public void setname(String name){this.name = name;}
        public String getphone(){return phone;}
        public void setphone(String phone){this.phone = phone;}
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
