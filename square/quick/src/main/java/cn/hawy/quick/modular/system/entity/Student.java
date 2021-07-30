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

    @TableId(value = "STUDENT_ID",type = IdType.ID_WORKER)
    private Long studentId;

    @TableField("STUDENT_NAME")
    private String studentName;

    @TableField("STUDENT_SEX")
    private String studentSex;

    @TableField("STUDENT_AGE")
    private Long studentAge;

    @TableField("STUDENT_PHONE")
    private String studentPhone;

    @Override
    public String toString() {
        return "My{" +
                ", studentId=" + studentId +
                ", studentName=" + studentName +
                ", studentSex=" + studentSex +
                ", studentAge=" + studentAge +
                ", studentPhone=" + studentPhone +
                "}";
    }
}
