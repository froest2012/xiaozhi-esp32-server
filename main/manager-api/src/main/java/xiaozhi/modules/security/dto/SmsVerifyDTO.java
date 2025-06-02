package xiaozhi.modules.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/**
 * 登录表单
 * @author xiuc001
 */
@Data
@Schema(description = "短信验证表单")
public class SmsVerifyDTO implements Serializable {

    @Schema(description = "手机号码")
    @NotBlank(message = "{sysuser.username.require}")
    private String username;

    @Schema(description = "手机验证码")
    @NotBlank(message = "{sysuser.username.require}")
    private String mobileCaptcha;

}