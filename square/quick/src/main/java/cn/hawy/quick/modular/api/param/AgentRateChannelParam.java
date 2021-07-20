package cn.hawy.quick.modular.api.param;

import lombok.Data;

/**
 * @PackageName:cn.hawy.quick.modular.api.param
 * @ClassName:AgentRateChannelParam
 * @Description:
 * @acthor lwq
 * @date 2021/7/20 14:46
 */
@Data
public class AgentRateChannelParam {
    private Integer id;
    private String agentId;
    private String channel;

    private String bankName;

    private String bankCode;

    private String costRate;
}
