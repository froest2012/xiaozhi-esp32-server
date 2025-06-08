package xiaozhi.modules.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 风险词告警创建DTO
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
public class AiRiskKeywordAlertCreateDTO {

    /**
     * 消息记录ID
     */
    @Schema(description = "消息记录ID")
    private String chatHistoryId;

    /**
     * 风险词，多个用;分割
     */
    @Schema(description = "风险词，多个用;分割，风险词长度不能超过1024个字符")
    private String riskKey;
}