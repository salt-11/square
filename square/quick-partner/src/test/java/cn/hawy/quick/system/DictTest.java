package cn.hawy.quick.system;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hawy.quick.base.BaseJunit;
import cn.hawy.quick.modular.system.mapper.DictMapper;
import cn.hawy.quick.modular.system.service.DictService;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典服务测试
 *
 * @author fengshuonan
 * @date 2017-04-27 17:05
 */
public class DictTest extends BaseJunit {

    @Resource
    DictService dictService;

    @Resource
    DictMapper dictMapper;
    
    public static void main(String[] args) {
    	HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "123");
    	String result= HttpUtil.post("http://localhost:8070/goblin/api/mch/mchRegister", paramMap);
    	System.out.println(result);
	}

    @Test
    public void deleteTest() {
    	HashMap<String, Object> paramMap = new HashMap<>();
    	paramMap.put("reqTime",DateUtil.now());
    	paramMap.put("partnerId", "123");
    	paramMap.put("outTradeNo", "20190524001");
    	String result= HttpUtil.post("http://localhost:8070/goblin/api/mch/mchRegister", paramMap);
       System.out.println(result);
    }
}
