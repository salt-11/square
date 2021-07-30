package cn.hawy.quick.modular.api.param;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @PackageName:cn.hawy.quick.modular.api.param
 * @ClassName:AgentInfoParam
 * @Description:
 * @acthor lwq
 * @date 2021/7/21 15:40
 */
@Data
public class AgentInfoParam {

    /**
     * 渠道号
     */
    private String id;


    /**
     * 银行卡姓名
     */
    private String name;

    /**
     * 银行卡号
     */
    private String cardNo;
    /**
     * 银行卡号
     */
    private String bankName;

}
