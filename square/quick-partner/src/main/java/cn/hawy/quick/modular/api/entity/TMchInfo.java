package cn.hawy.quick.modular.api.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 * 商户表
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
public class TMchInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户号
     */
    @TableId
    private String mchId;

    /**
     * 商户名称
     */
    private String mchName;

    private String mchShortName;

    private String areaCode;

    /**
     * 商户地址
     */
    private String mchAddress;

    /**
     * 状态 1-待审核 2-审核通过 3-审核失败
     */
    private Integer mchStatus;


    /**
     * 渠道号
     */
    private String deptId;

    /**
     * 手机号码
     */
    private String mobile;

    private String email;

    /**
     * 姓名
     */
    private String customerName;

    /**
     * 证件类型 0-身份证
     */
    private Integer customerIdentType;

    private String customerIdentNo;

    /**
     * 结算周期类型:D0
     */
    private String settMode;

    /**
     * 结算周期：0
     */
    private String settCircle;

    /**
     * 卡种类 1-对私 2-对公
     */
    private Integer cardKind;


    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getMchName() {
        return mchName;
    }

    public void setMchName(String mchName) {
        this.mchName = mchName;
    }

    public String getMchShortName() {
        return mchShortName;
    }

    public void setMchShortName(String mchShortName) {
        this.mchShortName = mchShortName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getMchAddress() {
        return mchAddress;
    }

    public void setMchAddress(String mchAddress) {
        this.mchAddress = mchAddress;
    }

    public Integer getMchStatus() {
        return mchStatus;
    }

    public void setMchStatus(Integer mchStatus) {
        this.mchStatus = mchStatus;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getCustomerIdentType() {
        return customerIdentType;
    }

    public void setCustomerIdentType(Integer customerIdentType) {
        this.customerIdentType = customerIdentType;
    }

    public String getCustomerIdentNo() {
        return customerIdentNo;
    }

    public void setCustomerIdentNo(String customerIdentNo) {
        this.customerIdentNo = customerIdentNo;
    }

    public String getSettMode() {
        return settMode;
    }

    public void setSettMode(String settMode) {
        this.settMode = settMode;
    }

    public String getSettCircle() {
        return settCircle;
    }

    public void setSettCircle(String settCircle) {
        this.settCircle = settCircle;
    }

    public Integer getCardKind() {
        return cardKind;
    }

    public void setCardKind(Integer cardKind) {
        this.cardKind = cardKind;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }


    @Override
    public String toString() {
        return "TMchInfo{" +
        "mchId=" + mchId +
        ", mchName=" + mchName +
        ", mchShortName=" + mchShortName +
        ", areaCode=" + areaCode +
        ", mchAddress=" + mchAddress +
        ", mchStatus=" + mchStatus +
        ", deptId=" + deptId +
        ", mobile=" + mobile +
        ", email=" + email +
        ", customerName=" + customerName +
        ", customerIdentType=" + customerIdentType +
        ", customerIdentNo=" + customerIdentNo +
        ", settMode=" + settMode +
        ", settCircle=" + settCircle +
        ", cardKind=" + cardKind +
        ", createTime=" + createTime +
        "}";
    }
}
