package xiaozhi.modules.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class DeviceManualAddDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5416115637986926503L;

    @Schema(description = "智能体ID")
    private String agentId;
    @Schema(description = "设备型号")
    private String board;
    @Schema(description = "固件版本")
    private String appVersion;
    @Schema(description = "设备ID/mac地址")
    private String macAddress;
} 