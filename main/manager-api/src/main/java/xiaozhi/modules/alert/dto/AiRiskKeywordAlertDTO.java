package xiaozhi.modules.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险词告警DTO
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
public class AiRiskKeywordAlertDTO {

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
    @Schema(description = "风险词，多个用;分割，风险词长度不能超过1024个字符")
    private String riskKey;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}