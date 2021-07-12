package cn.hawy.quick.system;

import cn.hawy.quick.base.BaseJunit;
import cn.hawy.quick.core.util.RSA;
import cn.hawy.quick.modular.system.entity.Dept;
import cn.hawy.quick.modular.system.mapper.DeptMapper;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpUtil;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 字典服务测试
 *
 * @author fengshuonan
 * @date 2017-04-27 17:05
 */
public class DeptTest extends BaseJunit {

    @Resource
    DeptMapper deptMapper;

    public static String PrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCkpiK7FMpkCGH+ZNGVC2Ng6sVw35xI61R2CZiaOVRAXs1lX1pz/T+xyXB1CHUox6SmSvkVHS+fu6kqFqTEpAgcyCbCmmLmqcsWeRVSX0MfIYs3u7wy7IwH9/A2ECCGedAZcJBmJPuoPUcyZ9/O2JTK4hBDrxibg+rjjGiD5A85Rdu7hAUEteeV/fEH8MamyWHps4unVKK6cOa4ZWtMZcMu7Ni3/VdPJ9D8FB8jvqOX6/d18fuqkiZ3rA9wsUvBngJlLx4C+XvkBwgx7rvU2J7pJBF90rtp7GXlgJ4wJ/fxz2pdQ88jCP+M1IKNsMjfIrUHip232d9c0wVEhizP5QwzAgMBAAECggEAHtnieBuVMNlBQ2Gww7S2klprVJMRvbgaw2NY7M0BNG6PHgeX81Pos2+DWuqSyWlfOKmjsokde1i2geRsS0xRGNOIL76t+XqnSza1ABJJiXPHmlHbGc+pDXUv1XGAJrJ8g8fLxtf3L7cLuC4uiZubhciGM6iDe+BkCu8sRkpdb+WQmflc6o3bRB1HWmVj/cyIcJai+JjBmNbGmnr3btv5oAUZ4TifNqgw0ak6W0wn2GrUQouFyj/cJnU8sXNJ76lId01wv/auTRAdBY4FWBm0Jw2Ym175QHwOLCXNZiFkx8r/J1GHrzQZazFS6P8ixPxt/SRgOQU/nXpDEKAyakZhAQKBgQD7VJ3omfnqQt6uDZWyFKwhHiG0Y8LebgMKhL7zEA0FiCYpgTmMcGbq1GddLEmxxUqBlvDjXa45VRYbrDlxvEpPiApQPlr2J81mxOOx99LC0J1IKL3xBnkpniTEGPJcUBC+ZOvIEGIeg9Q5YwFLX8StBB4PYf79+HcpxqPYHs7U8QKBgQCntT4GUikbIumnjNoyW2arwhUvgQghqNCaTKJCqa4eoaH0SzncOtNsCXIixMnEiAbDJI3Yo37k1Nbtynjyzw7RXxr2g+wb6/S1fNLGW8q7jxE3jPWsjAhcS5yx+4zFTQcS/SqEzlymrbfHM1Cu8aADJOWF4lUdCm2GNZo4dIfjYwKBgQCPNkhEonil5DS/OT5zmxP6SxEpmS8RT96rv7iPCDZjUJFuVRRV0jbS2PWNQg88HQ+3iCr7ZAZC5RkT669P/9rYNsyprN5oJYYFaWvVnUGbgQQeehjBns6ryFFq7EwuDfF8jEVOWweOG9ByYtf5+zg23oHCgjSqw0ojo+z42ZVCAQKBgQCDvnoSBxrpiVyZ1alW47xc8yMnZSqZHn94pvjUe20f/QM34Jx8Z+3MMgNqUsLZ063UQHP5mj5FzvlkZajrvUK7sQWCYBHrwUlEBOGjF1rESlhRFjesK3kp3/AhVSW5nJARF7X+DAp2mYMERkh8la2IqsRvj2QjYxy00IjtDoXtVwKBgAPt5jxnxfp7Zwl98wM88KNg0H24/4Z0Ura5azGtCbQhQyWiMjovmWvrk8x80XqNkNhGJM1p4SZXgm6XRJGw9/KJL74QehGlSC838PTzQ9LwmIoy/DRrF0kqHi37jVNo8tFGH2aPCANeUccdY390vweYRwbTUVT30FIa4V1Ydr/7";
    public static String url = "http://localhost:7010/quick/api/vs/bt/";

    @Test
    public void addDeptTest() {
    	Dept dept = new Dept();
        dept.setFullName("测试fullname");
        dept.setSort(5);
        dept.setPid(1L);
        dept.setSimpleName("测试");
        dept.setDescription("测试tips");
        dept.setVersion(1);
        Integer insert = deptMapper.insert(dept);
        assertEquals(insert, new Integer(1));
    }

    @Test
    public void updateTest() {
    	Dept dept = this.deptMapper.selectById(24);
        dept.setDescription("哈哈");
        deptMapper.updateById(dept);
    }

    @Test
    public void deleteTest() {
    	Dept dept = this.deptMapper.selectById(24);
        Integer integer = deptMapper.deleteById(dept);
        assertTrue(integer > 0);
    }

    @Test
    public void cardAuthTest(){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("reqTime", DateUtil.now());
        paramMap.put("partnerId", "2000601");
        paramMap.put("mchId", "1167366540398215170");
        paramMap.put("authType", "4");
        paramMap.put("idType", "1");
        paramMap.put("idNo", "429001198911125912");
        paramMap.put("realname", "肖会");
        paramMap.put("mobile", "18305975931");
        paramMap.put("cardNo", "6225768721583068");
        paramMap.put("cardType", "1");
        paramMap.put("cvv", "694");
        paramMap.put("validDate", "0721");

        String signContent = MapUtil.joinIgnoreNull(MapUtil.sort(paramMap), "&", "=");
        System.out.println(signContent);
        String signature = RSA.sign(signContent, PrivateKey);
        paramMap.put("signature", signature);
        String result= HttpUtil.post(url+"/mch/cardAuth", paramMap);
    }
}
