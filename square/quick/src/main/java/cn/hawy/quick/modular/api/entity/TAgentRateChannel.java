package cn.hawy.quick.modular.api.entity;

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
public class TAgentRateChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String agentId;

    private String channel;

    private String bankName;
    
    private String bankCode;

    private String costRate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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



    @Override
    public String toString() {
        return "TAgentRateChannel{" +
        "id=" + id +
        ", agentId=" + agentId +
        ", bankName=" + bankName +
        ", costRate=" + costRate +
        ", createTime=" + createTime +
        "}";
    }
}
