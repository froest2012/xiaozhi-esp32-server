package xiaozhi.modules.alert.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 智能体聊天记录风险词告警表
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_risk_keyword_alert")
public class AiRiskKeywordAlertEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息记录ID
     */
    @Schema(description = "chat_history_id")
    private Long chatHistoryId;

    /**
     * 风险词，多个用;分割
     */
    @Schema(description = "risk_key")
    private String riskKey;

    /**
     * 创建时间
     */
    @Schema(description = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "updated_at")
    private LocalDateTime updatedAt;
}