package cn.hawy.quick.modular.api.param;

import lombok.Data;

/**
 * @PackageName:cn.hawy.quick.modular.api.param
 * @ClassName:deptRateChannelParam
 * @Description:
 * @acthor lwq
 * @date 2021/7/20 15:25
 */
@Data
public class DeptRateChannelParam {
    private Integer id;
    private Long  deptId;
    private String channel;

    private String bankName;

    private String bankCode;

    private String costRate;
    private String cashRate;
    private String cardAuthRate;
}
