package cn.hawy.quick.modular.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author hawy
 * @since 2019-07-15
 */
public class TPlatformRateChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String channel;

    private String bankName;

    private String bankCode;

    private String costRate;

    private String cashRate;

    private String channelNo;

    private String channelMerAppId;

    private Integer luod;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
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


    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getChannelMerAppId() {
        return channelMerAppId;
    }

    public void setChannelMerAppId(String channelMerAppId) {
        this.channelMerAppId = channelMerAppId;
    }

    public Integer getLuod() {
        return luod;
    }

    public void setLuod(Integer luod) {
        this.luod = luod;
    }


    @Override
    public String toString() {
        return "TDeptRateChannel{" +
        "id=" + id +
        ", bankName=" + bankName +
        ", costRate=" + costRate +
        ", createTime=" + createTime +
        "}";
    }
}
