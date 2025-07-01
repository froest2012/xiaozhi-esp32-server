package xiaozhi.modules.sms.service;

import xiaozhi.modules.alert.dto.SmsAlertDTO;

/**
 * 短信服务的方法定义接口
 *
 * @author zjy
 * @since 2025-05-12
 */
public interface SmsService {

    /**
     * 发送验证码短信
     * @param phone 手机号码
     * @param VerificationCode 验证码
     */
    void sendVerificationCodeSms(String phone, String VerificationCode) ;

    void sendAlertSms(String phone, SmsAlertDTO alertDTO);
}
