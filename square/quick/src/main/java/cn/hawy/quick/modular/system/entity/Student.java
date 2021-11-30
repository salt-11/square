package cn.hawy.quick.modular.system.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName("sys_student")
@Data
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "STUDENT_ID", type = IdType.ID_WORKER)
    private String studentId;

    @TableField("STUDENT_NAME")
    private String studentName;

    @TableField("STUDENT_SEX")
    private String studentSex;

    @TableField("STUDENT_PHONE")
    private String studentPhone;

    @TableField("STUDENT_AGE")
    private int studentAge;

    @TableField("STUDENT_CREDIT")
    private int studentCredit;

    @TableField("STUDENT_MAJOR_ID")
    private Long studentMajorId;

    @TableField("STUDENT_CREDIT_NOW")
    private int studentCreditNow;

    @TableField("STUDENT_CLASS")
    private int studentClass;

    @TableField("STUDENT_CREDIT_OLD")
    private int studentCreditOld;
}
