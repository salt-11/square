package cn.hawy.quick.modular.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * <p>
 * 商户表
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
public class TMchCardChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    private Integer cardId;

    private String outMchId;

    private String mchRate;

    private Integer status;
    /**
     * 通道
     */
    private String channel;

    private String smsNo;

    /**
     * 协议号
     */
    private String protocol;

    private String isSendIssuer;

    private String notifyUrl;

    private Integer notifyCount;

    private String notifyResult;

    private LocalDateTime notifyTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSmsNo() {
		return smsNo;
	}


	public String getOutMchId() {
		return outMchId;
	}

	public void setOutMchId(String outMchId) {
		this.outMchId = outMchId;
	}

	public String getMchRate() {
		return mchRate;
	}

	public void setMchRate(String mchRate) {
		this.mchRate = mchRate;
	}

	public void setSmsNo(String smsNo) {
		this.smsNo = smsNo;
	}


	public String getIsSendIssuer() {
		return isSendIssuer;
	}

	public void setIsSendIssuer(String isSendIssuer) {
		this.isSendIssuer = isSendIssuer;
	}

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public Integer getNotifyCount() {
        return notifyCount;
    }

    public void setNotifyCount(Integer notifyCount) {
        this.notifyCount = notifyCount;
    }

    public String getNotifyResult() {
        return notifyResult;
    }

    public void setNotifyResult(String notifyResult) {
        this.notifyResult = notifyResult;
    }

    public LocalDateTime getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(LocalDateTime notifyTime) {
        this.notifyTime = notifyTime;
    }

    @Override
    public String toString() {
        return "TMchCardChannel{" +
        "id=" + id +
        ", cardId=" + cardId +
        ", channel=" + channel +
        ", protocol=" + protocol +
        ", createTime=" + createTime +
        "}";
    }
}
