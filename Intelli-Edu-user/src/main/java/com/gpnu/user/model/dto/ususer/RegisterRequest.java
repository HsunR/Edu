package com.gpnu.user.model.dto.ususer;


import com.gpnu.user.model.enums.RegisterType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.io.Serializable;

/**
 * 用户注册请求DTO
 */
@Data
@Schema(description = "用户注册请求DTO")
public class RegisterRequest implements Serializable {

    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20)
    @Schema(description = "姓名，长度在2到20之间", example = "张三")
    private String name;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+...]).{6,20}$",
            message = "密码必须包含字母、数字和特殊字符且长度在6到20之间")
    @Schema(description = "密码必须包含字母、数字和特殊字符且长度在6到20之间", example = "P@ssw0rd")
    private String password;

    @NotNull(message = "注册类型不能为空")
    @Schema(description = "注册类型：1-手机号注册，2-邮箱验证码注册,3-微信OpenID注册",example = "2")
    private RegisterType registerType;

    // --- 手机/邮箱/验证码（保持不变） ---
    @NotNull(message = "手机号不能为空", groups = MobileGroup.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", groups = MobileGroup.class)
    @Schema(description = "手机号，必须是中国大陆的手机号", example = "13800010004")
    private String mobile;

    @NotNull(message = "邮箱不能为空", groups = EmailGroup.class)
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", groups = EmailGroup.class)
    @Schema(description = "邮箱地址，必须符合邮箱格式", example = "12345678@qq.com")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码，注册类型为MOBILE_CODE时是短信验证码，EMAIL_CODE时是邮箱验证码", example = "645632")
    private String code;


    @NotBlank(message = "学号不能为空")
    @Schema(example = "20210001", description = "学号，长度在8到12之间")
    private String studentNo;


    @Schema(description = "年级，如2021级", example = "2021级")
    private String grade;

    @Schema(description = "专业，如计算机科学与技术", example = "计算机科学与技术")
    private String major;

    @Schema(description = "入学年份，如2021", example = "2021")
    private Integer enrollmentYear;

    @Schema(description = "学校名称，如XX大学", example = "广东技术师范大学")
    private String school;



    public interface EmailGroup {}
    public interface MobileGroup {}
}