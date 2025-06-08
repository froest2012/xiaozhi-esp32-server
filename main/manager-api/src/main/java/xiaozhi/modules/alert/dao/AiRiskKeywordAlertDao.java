package xiaozhi.modules.alert.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import xiaozhi.modules.alert.entity.AiRiskKeywordAlertEntity;

/**
 * 智能体聊天记录风险词告警表 Mapper 接口
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Mapper
public interface AiRiskKeywordAlertDao extends BaseMapper<AiRiskKeywordAlertEntity> {

    /**
     * 分页查询风险词告警记录
     *
     * @param page 分页参数
     * @param chatHistoryId 消息记录ID
     * @param riskKey 风险词
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<AiRiskKeywordAlertEntity> selectAlertPage(
            Page<AiRiskKeywordAlertEntity> page,
            @Param("chatHistoryId") Long chatHistoryId,
            @Param("riskKey") String riskKey,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 根据消息记录ID查询风险词告警
     *
     * @param chatHistoryId 消息记录ID
     * @return 告警记录列表
     */
    List<AiRiskKeywordAlertEntity> selectByChatHistoryId(@Param("chatHistoryId") Long chatHistoryId);

    /**
     * 根据风险词模糊查询
     *
     * @param riskKey 风险词
     * @return 告警记录列表
     */
    List<AiRiskKeywordAlertEntity> selectByRiskKey(@Param("riskKey") String riskKey);
}