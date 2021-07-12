package cn.hawy.quick.modular.api.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author hawy
 * @since 2019-07-15
 */
public class TBankCardBin implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String bankCardBin;
    
    private String bankCode;

    private String bankName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankCardBin() {
        return bankCardBin;
    }

    public void setBankCardBin(String bankCardBin) {
        this.bankCardBin = bankCardBin;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	@Override
    public String toString() {
        return "TBankCardBin{" +
        "id=" + id +
        ", bankCardBin=" + bankCardBin +
        ", bankName=" + bankName +
        "}";
    }
}
