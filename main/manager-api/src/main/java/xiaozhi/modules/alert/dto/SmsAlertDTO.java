package xiaozhi.modules.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * sms发送告警DTO
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
public class SmsAlertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4277034115519317980L;
    /**
     * 设备ID
     */
    @Schema(description = "告警内容")
    private String macAddress;

    /**
     * 风险词，多个用;分割
     */
    @Schema(description = "风险词，多个用;分割，风险词长度不能超过1024个字符")
    private String riskKey;
}