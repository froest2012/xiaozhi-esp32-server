package xiaozhi.modules.alert.service;

import java.time.LocalDateTime;
import java.util.List;
import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.agent.entity.AgentChatHistoryEntity;
import xiaozhi.modules.alert.dto.AiRiskKeywordAlertCreateDTO;
import xiaozhi.modules.alert.dto.AiRiskKeywordAlertDTO;
import xiaozhi.modules.alert.dto.AiRiskKeywordAlertReportDTO;
import xiaozhi.modules.alert.entity.AiRiskKeywordAlertEntity;

/**
 * 智能体聊天记录风险词告警表 服务类
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
public interface AiRiskKeywordAlertService extends BaseService<AiRiskKeywordAlertEntity> {

    /**
     * 创建风险词告警
     *
     * @param createDTO 创建参数
     * @return 告警ID
     */
    Long createAlert(AiRiskKeywordAlertCreateDTO createDTO);

    /**
     * 分页查询风险词告警
     *
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param chatHistoryId 消息记录ID
     * @param riskKey 风险词
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    PageData<AiRiskKeywordAlertReportDTO> getAlertPage(
            Integer pageNum, Integer pageSize,
            Long chatHistoryId, String riskKey,
            LocalDateTime startTime, LocalDateTime endTime
    );

    /**
     * 根据告警ID列表查询风险告警记录列表
     *
     * @param ids
     * @return
     */
    List<AiRiskKeywordAlertDTO> listByIds(List<Long> ids);

    /**
     * 检测风险词并保存告警记录
     * @param chatHistoryEntity
     * @return
     */
    void detectKeywords(AgentChatHistoryEntity chatHistoryEntity);
}