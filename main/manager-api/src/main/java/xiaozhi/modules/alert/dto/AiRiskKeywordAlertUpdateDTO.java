package xiaozhi.modules.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 风险词告警更新DTO
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
public class AiRiskKeywordAlertUpdateDTO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 消息记录ID
     */
    @Schema(description = "消息记录ID")
    private Long chatHistoryId;

    /**
     * 风险词，多个用;分割
     */
    @Schema(description = "风险词长度不能超过1024个字符")
    private String riskKey;
}