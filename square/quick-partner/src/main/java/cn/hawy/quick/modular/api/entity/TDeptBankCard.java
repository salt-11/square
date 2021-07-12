package cn.hawy.quick.modular.api.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

public class TDeptBankCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String deptId;

    private String bankCode;

    private String bankName;

    private String name;

    private String idNo;

    private String cardNo;

    /**
     * 卡种类 1-对私 2-对公
     */
    private Integer cardKind;

    private Integer cardType;

    private String bankLineNo;

    private String bankBranch;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getCardKind() {
        return cardKind;
    }

    public void setCardKind(Integer cardKind) {
        this.cardKind = cardKind;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getBankLineNo() {
        return bankLineNo;
    }

    public void setBankLineNo(String bankLineNo) {
        this.bankLineNo = bankLineNo;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    @Override
    public String toString() {
        return "TDeptBankCard{" +
                "deptId=" + deptId +
                ", bankCode=" + bankCode +
                ", bankName=" + bankName +
                ", name=" + name +
                ", idNo=" + idNo +
                ", cardNo=" + cardNo +
                ", cardKind=" + cardKind +
                ", cardType=" + cardType +
                ", bankLineNo=" + bankLineNo +
                ", bankBranch=" + bankBranch +
                "}";
    }
}
