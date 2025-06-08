package xiaozhi.modules.alert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "批量更新告警状态DTO")
public class AiRiskKeywordAlertBatchStatusDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -59220572619350048L;

    @Schema(description = "要更新的ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "ID列表不能为空")
    private List<Long> ids;
    
    @Schema(description = "新状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private Integer status;
}