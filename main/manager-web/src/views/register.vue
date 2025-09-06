<template>
  <div class="welcome" :class="themeClass" @keyup.enter="register">
    <el-container style="height: 100%;">
      <!-- 保持相同的头部 -->
      <el-header>
        <div style="display: flex;align-items: center;justify-content: space-between;margin-top: 15px;margin-left: 20px;margin-right: 20px;gap: 10px;">
          <div class="app-title" style="font-size: 24px; font-weight: 700; letter-spacing: 1px; transition: all 0.3s ease;">AI小新-智控台</div>
          <div class="theme-selector">
            <el-dropdown @command="changeBackgroundTheme" trigger="hover">
              <div class="theme-button">
                🎨 <i class="el-icon-arrow-down"></i>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="flow" :class="{'active-theme': currentTheme === 'flow'}">
                  🌊 流动渐变
                </el-dropdown-item>
                <el-dropdown-item command="bubbles" :class="{'active-theme': currentTheme === 'bubbles'}">
                  ☁️ 优雅光晕
                </el-dropdown-item>
                <el-dropdown-item command="particles" :class="{'active-theme': currentTheme === 'particles'}">
                  ✨ 粒子星空
                </el-dropdown-item>
                <el-dropdown-item command="geometric" :class="{'active-theme': currentTheme === 'geometric'}">
                  🔷 现代几何
                </el-dropdown-item>
                <el-dropdown-item command="breathing" :class="{'active-theme': currentTheme === 'breathing'}">
                  💫 呼吸光晕
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-main style="position: relative; display: flex; align-items: center; justify-content: center;">
        <div class="login-box">
          <!-- 修改标题部分 -->
          <div class="login-header">
            <div class="login-icon-wrapper">
              <div class="login-icon">🎉</div>
            </div>
            <div class="login-title-group">
              <div class="login-text">注册</div>
              <div class="login-welcome">WELCOME TO REGISTER</div>
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
                  <img loading="lazy" alt="" class="input-icon" src="@/assets/login/phone.png" />
                  <div style="display: flex; align-items: center; width: 100%;" :class="{'mobile-flex': isMobile}">
                    <el-select v-model="form.areaCode" :style="isMobile ? 'width: 90px; margin-right: 0;' : 'width: 120px; margin-right: 8px;'" placeholder="+86">
                      <el-option v-for="item in mobileAreaList" :key="item.key" :label="isMobile ? item.key : `${item.name} (${item.key})`"
                        :value="item.key" />
                    </el-select>
                    <el-input v-model="form.mobile" placeholder="请输入手机号码" style="flex: 1;" />
                  </div>
                </div>

                <div style="display: flex; align-items: center; margin-top: 20px; width: 100%; gap: 10px;" :class="{'mobile-captcha': isMobile}">
                  <div class="input-box" :style="isMobile ? 'width: calc(100% - 100px); margin-top: 0;' : 'width: calc(100% - 130px); margin-top: 0;'">
                    <img loading="lazy" alt="" class="input-icon" src="@/assets/login/shield.png" />
                    <el-input v-model="form.captcha" placeholder="请输入验证码" style="flex: 1;" />
                  </div>
                  <div class="captcha-container">
                    <img loading="lazy" v-if="captchaUrl" :src="captchaUrl" alt="验证码"
                      :style="isMobile ? 'width: 100px; height: 38px; cursor: pointer;' : 'width: 150px; height: 40px; cursor: pointer;'" @click="fetchCaptcha" />
                  </div>
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
                <el-input v-model="form.password" placeholder="请输入密码" type="password" show-password />
              </div>

              <!-- 新增确认密码 -->
              <div class="input-box">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/password.png" />
                <el-input v-model="form.confirmPassword" placeholder="请确认密码" type="password" show-password />
              </div>

              <!-- 验证码部分保持相同 -->
              <div v-if="!enableMobileRegister"
                style="display: flex; align-items: center; margin-top: 20px; width: 100%; gap: 10px;" :class="{'mobile-captcha': isMobile}">
                <div class="input-box" :style="isMobile ? 'width: calc(100% - 100px); margin-top: 0;' : 'width: calc(100% - 130px); margin-top: 0;'">
                  <img loading="lazy" alt="" class="input-icon" src="@/assets/login/shield.png" />
                  <el-input v-model="form.captcha" placeholder="请输入验证码" style="flex: 1;" />
                </div>
                <div class="captcha-container">
                  <img loading="lazy" v-if="captchaUrl" :src="captchaUrl" alt="验证码"
                    :style="isMobile ? 'width: 100px; height: 38px; cursor: pointer;' : 'width: 150px; height: 40px; cursor: pointer;'" @click="fetchCaptcha" />
                </div>
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
    },
    themeClass() {
      return `theme-${this.currentTheme}`;
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
      mobileDeviceDetected: false,
      currentTheme: localStorage.getItem('backgroundTheme') || 'flow'
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
    changeBackgroundTheme(theme) {
      this.currentTheme = theme;
      localStorage.setItem('backgroundTheme', theme);
      this.$message.success(`已切换到${this.getThemeName(theme)}主题`);
    },
    getThemeName(theme) {
      const names = {
        flow: '流动渐变',
        bubbles: '浮动气泡',
        particles: '粒子星空',
        geometric: '几何图案',
        breathing: '呼吸光晕'
      };
      return names[theme] || '未知主题';
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
  background: linear-gradient(135deg, #4A90A4 0%, #83C5BE 100%);
  border: none;
  border-radius: 8px;
  color: white;
  transition: all 0.3s ease;

  &:hover,
  &:focus {
    background: linear-gradient(135deg, #3A7A8A 0%, #6BB6AA 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(74, 144, 164, 0.4);
  }

  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(74, 144, 164, 0.4);
  }

  &:disabled {
    background: #a0aec0;
    border: none;
    transform: none;
    box-shadow: none;
  }
}

/* 移动端样式已移至 auth.scss 全局样式文件 */

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
