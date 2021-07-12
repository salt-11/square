package cn.hawy.quick.modular.api.channel.ff;


import lombok.Data;

@Data
public class FfResponseDto {

    public String code;

    private Object content;
    
    public String message;
}
