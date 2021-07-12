package cn.hawy.quick.modular.api.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;

/**
 * <p>
 * 
 * </p>
 *
 * @author hawy
 * @since 2019-07-31
 */
public class TMchImg implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String imgId;

    private String imgUrl;
    
    private String imgPath;


    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    
    public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	@Override
    public String toString() {
        return "TMchImg{" +
        "imgId=" + imgId +
        ", imgUrl=" + imgUrl +
        "}";
    }
}
