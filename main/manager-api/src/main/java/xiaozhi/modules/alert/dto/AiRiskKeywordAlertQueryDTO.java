package xiaozhi.modules.alert.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author xiuc001
 */
@Data
@Schema(description = "风险词告警查询DTO")
public class AiRiskKeywordAlertQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6148011125832180012L;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "聊天记录ID")
    private String chatHistoryId;

    @Schema(description = "关键词")
    private String keyword;
    
    @Schema(description = "告警状态")
    private Integer status;
    
    @Schema(description = "风险等级")
    private Integer riskLevel;
    
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}