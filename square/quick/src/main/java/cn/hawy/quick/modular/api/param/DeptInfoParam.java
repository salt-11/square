package cn.hawy.quick.modular.api.param;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @PackageName:cn.hawy.quick.modular.api.param
 * @ClassName:DeptInfoParam
 * @Description:
 * @acthor lwq
 * @date 2021/7/21 14:53
 */
@Data
public class DeptInfoParam {
    /**
     * 渠道号
     */
    private String id;

    /**
     * 渠道名称
     */
    private String deptName;

    /**
     * 代理商id
     */
    private String agentId;

    /**
     * 渠道公钥
     */
    private String deptPublickey;

    /**
     * 平台私钥
     */
    private String platformPrivatekey;

    /**
     * 平台公钥
     */
    private String platformPublickey;


}
