package cn.hawy.quick.modular.system.model;

import lombok.Data;

@Data
public class StudentDto {
    private Long studentId;
    private String studentName;
    private String studentSex;
    private Long studentAge;
    private String studentPhone;
}
