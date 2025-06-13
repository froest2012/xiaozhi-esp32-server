package xiaozhi.modules.mobile.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * @author 楼航
 * @date 6/13/25 23:57:23
 */
@Data
@Schema(description = "智能体聊天记录")
public class ChatHistoryParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 277865082027581191L;

    /**
     * agentId
     */
    @NotNull
    private String agentId;
    /**
     * macAddress
     */
    @NotNull
    private String macAddress;
}
