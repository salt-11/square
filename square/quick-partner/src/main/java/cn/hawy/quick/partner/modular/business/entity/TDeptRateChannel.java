package cn.hawy.quick.partner.modular.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author hawy
 * @since 2019-07-15
 */
public class TDeptRateChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String deptId;

    private String channel;

    private String bankName;
    
    private String bankCode;

    private String costRate;

    private String cashRate;

    private String cardAuthRate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
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

	public String getCostRate() {
        return costRate;
    }

    public void setCostRate(String costRate) {
        this.costRate = costRate;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCashRate() {
		return cashRate;
	}

	public void setCashRate(String cashRate) {
		this.cashRate = cashRate;
	}

    public String getCardAuthRate() {
        return cardAuthRate;
    }

    public void setCardAuthRate(String cardAuthRate) {
        this.cardAuthRate = cardAuthRate;
    }

    @Override
    public String toString() {
        return "TDeptRateChannel{" +
        "id=" + id +
        ", deptId=" + deptId +
        ", bankName=" + bankName +
        ", costRate=" + costRate +
        ", createTime=" + createTime +
        "}";
    }
}
