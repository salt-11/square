package cn.hawy.quick.modular.api.param;

import lombok.Data;

/**
 * @PackageName:cn.hawy.quick.modular.api.param
 * @ClassName:PlatformRateChannelParam
 * @Description:
 * @acthor lwq
 * @date 2021/7/20 10:59
 */
@Data
public class PlatformRateChannelParam {
    private Integer id;
    private String channel;

    private String bankName;

    private String bankCode;

    private String costRate;
    private String  cashRate;

}
