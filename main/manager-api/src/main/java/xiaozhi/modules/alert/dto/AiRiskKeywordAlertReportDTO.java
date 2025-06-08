package xiaozhi.modules.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险词告警报表DTO
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
public class AiRiskKeywordAlertReportDTO {

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
    @Schema(description = "风险词")
    private String riskKey;

    /**
     * 风险词数组（解析后的）
     */
    @Schema(description = "风险词数组")
    private List<String> riskKeyArray;

    /**
     * 风险词数量
     */
    @Schema(description = "风险词数量")
    private Integer riskKeyCount;

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