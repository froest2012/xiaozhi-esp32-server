package xiaozhi.modules.alert.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xiaozhi.common.constant.Constant;
import xiaozhi.common.page.PageData;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.common.utils.JsonUtils;
import xiaozhi.modules.agent.entity.AgentChatHistoryEntity;
import xiaozhi.modules.alert.dao.AiRiskKeywordAlertDao;
import xiaozhi.modules.alert.dto.AiRiskKeywordAlertCreateDTO;
import xiaozhi.modules.alert.dto.AiRiskKeywordAlertDTO;
import xiaozhi.modules.alert.dto.AiRiskKeywordAlertReportDTO;
import xiaozhi.modules.alert.entity.AiRiskKeywordAlertEntity;
import xiaozhi.modules.alert.service.AiRiskKeywordAlertService;
import xiaozhi.modules.alert.service.TrieKeywordDetectionService;
import xiaozhi.modules.sys.dao.SysDictDataDao;
import xiaozhi.modules.sys.entity.SysDictDataEntity;
import xiaozhi.modules.xin.config.XinRedisKeys;

/**
 * 智能体聊天记录风险词告警表 服务实现类
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Slf4j
@Service
@AllArgsConstructor
public class AiRiskKeywordAlertServiceImpl extends
    BaseServiceImpl<AiRiskKeywordAlertDao, AiRiskKeywordAlertEntity> implements
    AiRiskKeywordAlertService {

    private final SysDictDataDao sysDictDataDao;
    private final AiRiskKeywordAlertDao riskKeywordAlertDao;

    private final TrieKeywordDetectionService trieKeywordDetectionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        List<String> keywords = getCachedRiskKeywords();
        if (CollectionUtil.isEmpty(keywords)) {
            keywords = getRiskKeywords();
            setCachedRiskKeywords(keywords);
        }
        keywords = getCachedRiskKeywords();
        log.info("初始化风险词：{}", keywords);
        trieKeywordDetectionService.buildTrie(Sets.newHashSet(keywords));
    }

    /**
     * 从Redis获取缓存的风险关键词
     */
    private List<String> getCachedRiskKeywords() {
        try {
            Object cached = redisTemplate.opsForValue().get(XinRedisKeys.getRiskKeywordsKey());
            if (cached instanceof List) {
                return (List<String>) cached;
            }
        } catch (Exception e) {
            log.warn("从Redis获取风险关键词缓存失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 设置风险关键词到Redis缓存
     */
    private void setCachedRiskKeywords(List<String> keywords) {
        try {
            redisTemplate.opsForValue().set(XinRedisKeys.getRiskKeywordsKey(), keywords);
        } catch (Exception e) {
            log.warn("设置风险关键词到Redis缓存失败: {}", e.getMessage());
        }
    }

    private List<String> getRiskKeywords() {
        QueryWrapper<SysDictDataEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_type_id", 103);
        List<SysDictDataEntity> riskKeywords = sysDictDataDao.selectList(wrapper);
        return riskKeywords.stream().map(SysDictDataEntity::getDictValue).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAlert(AiRiskKeywordAlertCreateDTO createDTO) {
        AiRiskKeywordAlertEntity entity = new AiRiskKeywordAlertEntity();
        entity.setChatHistoryId(Long.valueOf(createDTO.getChatHistoryId()));
        entity.setRiskKey(createDTO.getRiskKey());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        super.insert(entity);
        return entity.getId();
    }

    @Override
    public PageData<AiRiskKeywordAlertReportDTO> getAlertPage(
            Integer pageNum, Integer pageSize,
            Long chatHistoryId, String riskKey,
            LocalDateTime startTime, LocalDateTime endTime) {

        Map<String, Object> params = Maps.newHashMap();
        params.put(Constant.PAGE, String.valueOf(pageNum));
        params.put(Constant.LIMIT, String.valueOf(pageSize));
        if (chatHistoryId != null) {
            params.put("chatHistoryId", chatHistoryId);
        }
        if  (StringUtils.hasText(riskKey)) {
            params.put("riskKey", riskKey);
        }
        if (startTime != null) {
            params.put("created_at", startTime);
        }
        if (endTime != null) {
            params.put("created_at", endTime);
        }
        IPage<AiRiskKeywordAlertEntity> page = riskKeywordAlertDao.selectPage(
            getPage(params, "created_at", false),
            new QueryWrapper<>());
        if (page == null || page.getRecords() == null) {
            return new PageData<>(new ArrayList<>(), 0);
        }
        return new PageData<>(page.getRecords().stream().map(this::convertToReportDTO).collect(Collectors.toList()), page.getTotal());
    }

    @Override
    public List<AiRiskKeywordAlertDTO> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        QueryWrapper<AiRiskKeywordAlertEntity> wrapper = new QueryWrapper<>();
        wrapper.in("chat_history_id", ids);
        List<AiRiskKeywordAlertEntity> list = riskKeywordAlertDao.selectList(wrapper);
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().map(entity -> {
            AiRiskKeywordAlertDTO dto = new AiRiskKeywordAlertDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void detectKeywords(AgentChatHistoryEntity chatHistoryEntity) {
        List<String> keywords = trieKeywordDetectionService.detectKeywords(chatHistoryEntity.getContent());
        log.info("risk keys : {}", JsonUtils.toJsonString(keywords));
        if (CollectionUtil.isNotEmpty(keywords)) {
            // AiRiskKeywordAlertCreateDTO createDTO = new AiRiskKeywordAlertCreateDTO();
            // createDTO.setChatHistoryId(String.valueOf(chatHistoryEntity.getId()));
            // createDTO.setRiskKey(String.join(";", keywords));
            // createAlert(createDTO);
        }
    }

    /**
     * 转换为报表DTO
     */
    private AiRiskKeywordAlertReportDTO convertToReportDTO(AiRiskKeywordAlertEntity entity) {
        AiRiskKeywordAlertReportDTO dto = new AiRiskKeywordAlertReportDTO();
        BeanUtils.copyProperties(entity, dto);
        
        // 解析风险词
        if (StringUtils.hasText(entity.getRiskKey())) {
            String[] riskKeyArray = entity.getRiskKey().split(";");
            dto.setRiskKeyArray(Lists.newArrayList(riskKeyArray));
            dto.setRiskKeyCount(riskKeyArray.length);
        } else {
            dto.setRiskKeyArray(Lists.newArrayList());
            dto.setRiskKeyCount(0);
        }
        
        return dto;
    }
}