<template>
  <div class="welcome" @keyup.enter="register">
    <el-container style="height: 100%;">
      <!-- 保持相同的头部 -->
      <el-header>
        <div style="display: flex;align-items: center;margin-top: 15px;margin-left: 10px;gap: 10px;">
          <img loading="lazy" alt="" src="@/assets/xiaozhi-logo.png" style="width: 45px;height: 45px;" />
          <img loading="lazy" alt="" src="@/assets/xiaozhi-ai.png" style="height: 18px;" />
        </div>
      </el-header>
      <div class="login-person" :class="{'mobile': isMobile}">
        <img loading="lazy" alt="" src="@/assets/login/register-person.png" style="width: 100%;" />
      </div>
      <el-main style="position: relative;">
        <div class="login-box">
          <!-- 修改标题部分 -->
          <div style="display: flex;align-items: center;gap: 20px;margin-bottom: 39px;padding: 0 30px;">
            <img loading="lazy" alt="" src="@/assets/login/hi.png" style="width: 34px;height: 34px;" />
            <div class="login-text">注册</div>
            <div class="login-welcome">
              WELCOME TO REGISTER
            </div>
          </div>

          <div style="padding: 0 30px;">
            <form @submit.prevent="register">
              <!-- 用户名/手机号输入框 -->
              <div class="input-box" v-if="!enableMobileRegister">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/username.png" />
                <el-input v-model="form.username" placeholder="请输入用户名" />
              </div>

              <!-- 手机号注册部分 -->
              <template v-if="enableMobileRegister">
                <div class="input-box">
                  <div style="display: flex; align-items: center; width: 100%;" :class="{'mobile-flex': isMobile}">
                    <el-select v-model="form.areaCode" :style="isMobile ? 'width: 120px; margin-right: 5px;' : 'width: 220px; margin-right: 10px;'">
                      <el-option v-for="item in mobileAreaList" :key="item.key" :label="isMobile ? item.key : `${item.name} (${item.key})`"
                        :value="item.key" />
                    </el-select>
                    <el-input v-model="form.mobile" placeholder="请输入手机号码" />
                  </div>
                </div>

                <div style="display: flex; align-items: center; margin-top: 20px; width: 100%; gap: 10px;" :class="{'mobile-captcha': isMobile}">
                  <div class="input-box" :style="isMobile ? 'width: calc(100% - 100px); margin-top: 0;' : 'width: calc(100% - 130px); margin-top: 0;'">
                    <img loading="lazy" alt="" class="input-icon" src="@/assets/login/shield.png" />
                    <el-input v-model="form.captcha" placeholder="请输入验证码" style="flex: 1;" />
                  </div>
                  <img loading="lazy" v-if="captchaUrl" :src="captchaUrl" alt="验证码"
                    :style="isMobile ? 'width: 100px; height: 38px; cursor: pointer;' : 'width: 150px; height: 40px; cursor: pointer;'" @click="fetchCaptcha" />
                </div>

                <!-- 手机验证码 -->
                <div style="display: flex; align-items: center; margin-top: 20px; width: 100%; gap: 10px;" :class="{'mobile-captcha': isMobile}">
                  <div class="input-box" :style="isMobile ? 'width: calc(100% - 100px); margin-top: 0;' : 'width: calc(100% - 130px); margin-top: 0;'">
                    <img loading="lazy" alt="" class="input-icon" src="@/assets/login/phone.png" />
                    <el-input v-model="form.mobileCaptcha" placeholder="请输入手机验证码" style="flex: 1;" maxlength="6" />
                  </div>
                  <el-button type="primary" class="send-captcha-btn" :disabled="!canSendMobileCaptcha"
                    @click="sendMobileCaptcha">
                    <span>
                      {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
                    </span>
                  </el-button>
                </div>
              </template>

              <!-- 密码输入框 -->
              <div class="input-box">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/password.png" />
                <el-input v-model="form.password" placeholder="请输入密码" type="password" />
              </div>

              <!-- 新增确认密码 -->
              <div class="input-box">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/password.png" />
                <el-input v-model="form.confirmPassword" placeholder="请确认密码" type="password" />
              </div>

              <!-- 验证码部分保持相同 -->
              <div v-if="!enableMobileRegister"
                style="display: flex; align-items: center; margin-top: 20px; width: 100%; gap: 10px;" :class="{'mobile-captcha': isMobile}">
                <div class="input-box" :style="isMobile ? 'width: calc(100% - 100px); margin-top: 0;' : 'width: calc(100% - 130px); margin-top: 0;'">
                  <img loading="lazy" alt="" class="input-icon" src="@/assets/login/shield.png" />
                  <el-input v-model="form.captcha" placeholder="请输入验证码" style="flex: 1;" />
                </div>
                <img loading="lazy" v-if="captchaUrl" :src="captchaUrl" alt="验证码"
                  :style="isMobile ? 'width: 100px; height: 38px; cursor: pointer;' : 'width: 150px; height: 40px; cursor: pointer;'" @click="fetchCaptcha" />
              </div>

              <!-- 修改底部链接 -->
              <div style="font-weight: 400;font-size: 14px;text-align: left;color: #5778ff;margin-top: 20px;">
                <div style="cursor: pointer;" @click="goToLogin">已有账号？立即登录</div>
              </div>
            </form>
          </div>

          <!-- 修改按钮文本 -->
          <div class="login-btn" @click="register">立即注册</div>

          <!-- 保持相同的协议声明 -->
          <div style="font-size: 14px;color: #979db1;">
            注册即同意
            <div style="display: inline-block;color: #5778FF;cursor: pointer;">《用户协议》</div>
            和
            <div style="display: inline-block;color: #5778FF;cursor: pointer;">《隐私政策》</div>
          </div>
        </div>
      </el-main>

      <!-- 保持相同的页脚 -->
      <el-footer>
        <version-footer />
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import Api from '@/apis/api';
import VersionFooter from '@/components/VersionFooter.vue';
import { getUUID, goToPage, showDanger, showSuccess, validateMobile, isMobileDevice } from '@/utils';
import { mapState } from 'vuex';

export default {
  name: 'register',
  components: {
    VersionFooter
  },
  computed: {
    ...mapState({
      allowUserRegister: state => state.pubConfig.allowUserRegister,
      enableMobileRegister: state => state.pubConfig.enableMobileRegister,
      mobileAreaList: state => state.pubConfig.mobileAreaList
    }),
    canSendMobileCaptcha() {
      return this.countdown === 0 && validateMobile(this.form.mobile, this.form.areaCode);
    },
    isMobile() {
      return this.mobileDeviceDetected;
    }
  },
  data() {
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        captcha: '',
        captchaId: '',
        areaCode: '+86',
        mobile: '',
        mobileCaptcha: ''
      },
      captchaUrl: '',
      countdown: 0,
      timer: null,
      mobileDeviceDetected: false
    }
  },
  mounted() {
    this.$store.dispatch('fetchPubConfig').then(() => {
      if (!this.allowUserRegister) {
        showDanger('当前不允许普通用户注册');
        setTimeout(() => {
          goToPage('/login');
        }, 1500);
      }
    });
    this.fetchCaptcha();
    this.mobileDeviceDetected = isMobileDevice();
    // 监听窗口大小变化，重新检测设备类型
    window.addEventListener('resize', this.checkDeviceType);
  },
  beforeDestroy() {
    // 移除事件监听器
    window.removeEventListener('resize', this.checkDeviceType);
    // 清除定时器
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  methods: {
    checkDeviceType() {
      this.mobileDeviceDetected = isMobileDevice();
    },
    // 复用验证码获取方法
    fetchCaptcha() {
      this.form.captchaId = getUUID();
      Api.user.getCaptcha(this.form.captchaId, (res) => {
        if (res.status === 200) {
          const blob = new Blob([res.data], { type: res.data.type });
          this.captchaUrl = URL.createObjectURL(blob);

        } else {
          console.error('验证码加载异常:', error);
          showDanger('验证码加载失败，点击刷新');
        }
      });
    },

    // 封装输入验证逻辑
    validateInput(input, message) {
      if (!input.trim()) {
        showDanger(message);
        return false;
      }
      return true;
    },

    // 发送手机验证码
    sendMobileCaptcha() {
      if (!validateMobile(this.form.mobile, this.form.areaCode)) {
        showDanger('请输入正确的手机号码');
        return;
      }

      // 验证图形验证码
      if (!this.validateInput(this.form.captcha, '请输入图形验证码')) {
        this.fetchCaptcha();
        return;
      }

      // 清除可能存在的旧定时器
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }

      // 开始倒计时
      this.countdown = 60;
      this.timer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          clearInterval(this.timer);
          this.timer = null;
        }
      }, 1000);

      // 调用发送验证码接口
      Api.user.sendSmsVerification({
        phone: this.form.areaCode + this.form.mobile,
        captcha: this.form.captcha,
        captchaId: this.form.captchaId
      }, (res) => {
        showSuccess('验证码发送成功');
      }, (err) => {
        showDanger(err.data.msg || '验证码发送失败');
        this.countdown = 0;
        this.fetchCaptcha();
      });
    },

    // 注册逻辑
    register() {
      if (this.enableMobileRegister) {
        // 手机号注册验证
        if (!validateMobile(this.form.mobile, this.form.areaCode)) {
          showDanger('请输入正确的手机号码');
          return;
        }
        if (!this.form.mobileCaptcha) {
          showDanger('请输入手机验证码');
          return;
        }
      } else {
        // 用户名注册验证
        if (!this.validateInput(this.form.username, '用户名不能为空')) {
          return;
        }
      }

      // 密码验证
      if (!this.validateInput(this.form.password, '密码不能为空')) {
        return;
      }

      if (this.form.password !== this.form.confirmPassword) {
        showDanger('两次输入的密码不一致');
        return;
      }

      // 验证码验证
      if (!this.validateInput(this.form.captcha, '验证码不能为空')) {
        return;
      }

      // 构建注册请求参数
      const registerParams = {
        username: this.enableMobileRegister ? this.form.areaCode + this.form.mobile : this.form.username,
        password: this.form.password,
        captcha: this.form.captcha,
        captchaId: this.form.captchaId
      };

      // 如果是手机注册，添加手机验证码
      if (this.enableMobileRegister) {
        registerParams.smsCode = this.form.mobileCaptcha;
      }

      // 发送注册请求
      Api.user.register(registerParams, () => {
        showSuccess('注册成功，请登录');
        setTimeout(() => {
          goToPage('/login');
        }, 1500);
      }, (err) => {
        showDanger(err.data.msg || '注册失败');
        this.fetchCaptcha();
      });
    },

    goToLogin() {
      goToPage('/login');
    }
  }
}
</script>

<style lang="scss" scoped>
@import './auth.scss';

.send-captcha-btn {
  height: 38px;
  width: 120px;
  padding: 0;
  background-color: #5778ff;
  border-color: #5778ff;
  color: white;

  &:hover,
  &:focus {
    background-color: #4a6ae8;
    border-color: #4a6ae8;
  }

  &:active {
    background-color: #3d5cd6;
    border-color: #3d5cd6;
  }

  &:disabled {
    background-color: #a0aec0;
    border-color: #a0aec0;
  }
}

/* 移动端特定样式 */
.mobile-flex {
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}

.mobile-captcha {
  flex-direction: column;
  align-items: center;
}

@media screen and (max-width: 768px) {
  .el-select {
    width: 100% !important;
    margin-right: 0 !important;
    margin-bottom: 10px;
  }

  .send-captcha-btn {
    width: 100px;
    font-size: 12px;
    height: 38px;
    line-height: 38px;
  }

  /* 优化移动端布局 */
  .el-header {
    padding: 10px 0;
    height: auto !important;
  }

  .el-main {
    padding: 10px;
  }

  .el-footer {
    padding: 10px 0;
    height: auto !important;
  }

  /* 优化表单内容对齐 */
  :deep(.el-input__inner) {
    height: 38px;
    line-height: 38px;
  }

  .input-icon {
    width: 16px;
    height: 16px;
  }
}
</style>
