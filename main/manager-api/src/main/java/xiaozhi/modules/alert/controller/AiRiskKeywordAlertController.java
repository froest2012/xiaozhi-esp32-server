package xiaozhi.modules.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaozhi.common.page.PageData;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.agent.entity.AgentChatHistoryEntity;
import xiaozhi.modules.alert.dto.AiRiskKeywordAlertCreateDTO;
import xiaozhi.modules.alert.dto.AiRiskKeywordAlertQueryDTO;
import xiaozhi.modules.alert.dto.AiRiskKeywordAlertReportDTO;
import xiaozhi.modules.alert.service.AiRiskKeywordAlertService;

/**
 * @author xiuc001
 */
@RestController
@RequestMapping("/risk/alert")
@Tag(name = "风险词告警管理")
@Slf4j
public class AiRiskKeywordAlertController {

    @Autowired
    private AiRiskKeywordAlertService aiRiskKeywordAlertService;

    @PostMapping("/list")
    @Operation(summary = "分页查询风险词告警列表")
    public Result<PageData<AiRiskKeywordAlertReportDTO>> list(@RequestBody AiRiskKeywordAlertQueryDTO queryDTO) {
        PageData<AiRiskKeywordAlertReportDTO> result = aiRiskKeywordAlertService.getAlertPage(queryDTO.getPageNum(), queryDTO.getPageSize(), ObjectUtils.isEmpty(queryDTO.getChatHistoryId()) ? null : Long.valueOf(queryDTO.getChatHistoryId()), queryDTO.getKeyword(), queryDTO.getStartTime(), queryDTO.getEndTime());
        return new Result<PageData<AiRiskKeywordAlertReportDTO>>().ok(result);
    }

    @PostMapping("/create")
    @Operation(summary = "创建风险词告警")
    public Result<Void> create(@RequestBody @Valid AiRiskKeywordAlertCreateDTO createDTO) {
        aiRiskKeywordAlertService.createAlert(createDTO);
        return new Result<Void>().ok(null);
    }

    //测试AiRiskKeywordAlertService.detectKeywords方法
    @GetMapping("/detect")
    public Result<Void> detect() {
        AgentChatHistoryEntity  chatHistoryEntity = new AgentChatHistoryEntity();
        chatHistoryEntity.setContent("他们一起打架斗殴，自杀");
        aiRiskKeywordAlertService.detectKeywords(chatHistoryEntity);
        return new Result<Void>().ok(null);
    }


}