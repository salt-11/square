package cn.hawy.quick.modular.api.channel.gyf;


import lombok.Data;

@Data
public class GyfResponseDto {

    public String code;

    public Object data;

    private String msg;
}
