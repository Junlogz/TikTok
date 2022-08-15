package com.zjl.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VlogBO {
    private String id;
    @NotBlank(message = "视频号不能为空")
    private String vlogerId;
    @NotBlank(message = "视频地址不能为空")
    private String url;
    private String cover;
    private String title;
//    @NotBlank(message = "视频宽度不能为空")
    private Integer width;
//    @NotBlank(message = "视频高度不能为空")
    private Integer height;
    private Integer likeCounts;
    private Integer commentsCounts;
}