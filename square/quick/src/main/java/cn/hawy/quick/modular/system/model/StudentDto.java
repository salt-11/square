package cn.hawy.quick.modular.system.model;

import lombok.Data;

@Data
public class StudentDto {
    private String studentId;
    private String studentName;
    private String studentSex;
    private String studentPhone;
    private int studentAge;
    private int studentCredit;
    private Long studentMajorId;
    private int studentCreditNow;
    private int studentClass;
    private int studentCreditOld;
}
