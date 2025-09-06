<template>
  <div class="welcome" :class="themeClass">
    <el-container style="height: 100%;">
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
        <div class="login-box" @keyup.enter="login">
          <div class="login-header">
            <div class="login-icon-wrapper">
              <div class="login-icon">👋</div>
            </div>
            <div class="login-title-group">
              <div class="login-text">登录</div>
              <div class="login-welcome">WELCOME TO LOGIN</div>
            </div>
          </div>
          <div style="padding: 0 30px;">
            <!-- 用户名登录 -->
            <template v-if="!isMobileLogin">
              <div class="input-box">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/username.png" />
                <el-input v-model="form.username" placeholder="请输入用户名" />
              </div>
            </template>

            <!-- 手机号登录 -->
            <template v-else>
              <div class="input-box mobile-input-container" :class="{'mobile-phone-box': isMobile}">
                <img loading="lazy" alt="" class="input-icon" src="@/assets/login/username.png" />
                <div class="phone-input-wrapper" :class="{'mobile-phone-wrapper': isMobile}">
                  <el-select v-model="form.areaCode" class="area-code-select" :class="{'mobile-area-select': isMobile}">
                    <el-option v-for="item in mobileAreaList" :key="item.key" :label="isMobile ? item.key : `${item.name} (${item.key})`"
                      :value="item.key" />
                  </el-select>
                  <el-input v-model="form.mobile" placeholder="请输入手机号码" class="mobile-number-input" />
                </div>
              </div>
            </template>

            <div class="input-box">
              <img loading="lazy" alt="" class="input-icon" src="@/assets/login/password.png" />
              <el-input v-model="form.password" placeholder="请输入密码" type="password" show-password />
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
            <div
              style="font-weight: 400;font-size: 14px;text-align: left;color: #5778ff;display: flex;justify-content: space-between;margin-top: 20px;">
              <div v-if="allowUserRegister" style="cursor: pointer;" @click="goToRegister">新用户注册</div>
              <div style="cursor: pointer;" @click="goToForgetPassword" v-if="enableMobileRegister">忘记密码?</div>
            </div>
          </div>
          <div class="login-btn" @click="login">登录</div>

          <!-- 登录方式切换按钮 -->
          <div class="login-type-container" v-if="enableMobileRegister">
            <el-tooltip content="手机号码登录" placement="bottom">
              <el-button :type="isMobileLogin ? 'primary' : 'default'" icon="el-icon-mobile" circle
                @click="switchLoginType('mobile')"></el-button>
            </el-tooltip>
            <el-tooltip content="用户名登录" placement="bottom">
              <el-button :type="!isMobileLogin ? 'primary' : 'default'" icon="el-icon-user" circle
                @click="switchLoginType('username')"></el-button>
            </el-tooltip>
          </div>

          <div style="font-size: 14px;color: #979db1;margin-top: 10px;">
            登录即同意
            <div style="display: inline-block;color: #5778FF;cursor: pointer;">《用户协议》</div>
            和
            <div style="display: inline-block;color: #5778FF;cursor: pointer;">《隐私政策》</div>
          </div>
        </div>
      </el-main>
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
  name: 'login',
  components: {
    VersionFooter
  },
  computed: {
    ...mapState({
      allowUserRegister: state => state.pubConfig.allowUserRegister,
      enableMobileRegister: state => state.pubConfig.enableMobileRegister,
      mobileAreaList: state => state.pubConfig.mobileAreaList
    }),
    isMobile() {
      return this.mobileDeviceDetected;
    },
    themeClass() {
      return `theme-${this.currentTheme}`;
    }
  },
  data() {
    return {
      activeName: "username",
      form: {
        username: '',
        password: '',
        captcha: '',
        captchaId: '',
        areaCode: '+86',
        mobile: ''
      },
      captchaUuid: '',
      captchaUrl: '',
      isMobileLogin: false,
      mobileDeviceDetected: false,
      currentTheme: localStorage.getItem('backgroundTheme') || 'flow'
    }
  },
  mounted() {
    this.fetchCaptcha();
    this.mobileDeviceDetected = isMobileDevice();
    this.$store.dispatch('fetchPubConfig').then(() => {
      // 根据配置决定默认登录方式
      this.isMobileLogin = this.enableMobileRegister;
    });
    // 监听窗口大小变化，重新检测设备类型
    window.addEventListener('resize', this.checkDeviceType);
  },
  beforeDestroy() {
    // 移除事件监听器
    window.removeEventListener('resize', this.checkDeviceType);
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
        bubbles: '优雅光晕',
        particles: '粒子星空',
        geometric: '现代几何',
        breathing: '呼吸光晕'
      };
      return names[theme] || '未知主题';
    },
    fetchCaptcha() {
      if (this.$store.getters.getToken) {
        if (this.$route.path !== '/home') {
          this.$router.push('/home')
        }
      } else {
        this.captchaUuid = getUUID();

        Api.user.getCaptcha(this.captchaUuid, (res) => {
          if (res.status === 200) {
            const blob = new Blob([res.data], { type: res.data.type });
            this.captchaUrl = URL.createObjectURL(blob);
          } else {
            showDanger('验证码加载失败，点击刷新');
          }
        });
      }
    },

    // 切换登录方式
    switchLoginType(type) {
      this.isMobileLogin = type === 'mobile';
      // 清空表单
      this.form.username = '';
      this.form.mobile = '';
      this.form.password = '';
      this.form.captcha = '';
      this.fetchCaptcha();
    },

    // 封装输入验证逻辑
    validateInput(input, message) {
      if (!input.trim()) {
        showDanger(message);
        return false;
      }
      return true;
    },

    async login() {
      if (this.isMobileLogin) {
        // 手机号登录验证
        if (!validateMobile(this.form.mobile, this.form.areaCode)) {
          showDanger('请输入正确的手机号码');
          return;
        }
        // 拼接手机号作为用户名
        this.form.username = this.form.areaCode + this.form.mobile;
      } else {
        // 用户名登录验证
        if (!this.validateInput(this.form.username, '用户名不能为空')) {
          return;
        }
      }

      // 验证密码
      if (!this.validateInput(this.form.password, '密码不能为空')) {
        return;
      }
      // 验证验证码
      if (!this.validateInput(this.form.captcha, '验证码不能为空')) {
        return;
      }

      this.form.captchaId = this.captchaUuid
      Api.user.login(this.form, ({ data }) => {
        showSuccess('登录成功！');
        this.$store.commit('setToken', JSON.stringify(data.data));
        goToPage('/home');
      }, (err) => {
        showDanger(err.data.msg || '登录失败')
        if (err.data != null && err.data.msg != null && err.data.msg.indexOf('图形验证码') > -1) {
          this.fetchCaptcha()
        }
      })

      // 重新获取验证码
      setTimeout(() => {
        this.fetchCaptcha();
      }, 1000);
    },

    goToRegister() {
      goToPage('/register')
    },
    goToForgetPassword() {
      goToPage('/retrieve-password')
    },
  }
}
</script>
<style lang="scss" scoped>
@import './auth.scss';

.login-type-container {
  margin: 10px 20px;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #4A90A4 0%, #83C5BE 100%);
  border: none;
  border-radius: 8px;
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
}

/* 手机号输入框样式 */
.mobile-input-container {
  display: flex;
  align-items: center;
  gap: 15px;
}

.phone-input-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 10px;
}

.area-code-select {
  width: 140px;
  flex-shrink: 0;
}

.mobile-number-input {
  flex: 1;
}

/* 移动端手机号输入框样式 */
.mobile-phone-box {
  padding: 0 10px;
  gap: 10px;
}

.mobile-phone-wrapper {
  gap: 8px;
}

.mobile-area-select {
  width: 85px !important;
  min-width: 85px;
}

/* 区号选择器通用样式优化 */
.area-code-select :deep(.el-input__inner) {
  background-color: transparent;
  border: none;
}

.mobile-number-input :deep(.el-input__inner) {
  background-color: transparent;
  border: none;
}

/* 移动端特定样式 */
.mobile-captcha {
  flex-direction: column;
  align-items: center;
}

@media screen and (max-width: 768px) {
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

  /* 移动端手机号输入框优化 */
  .mobile-phone-box {
    height: 38px;
    padding: 0 12px;
  }

  .mobile-area-select :deep(.el-input__inner) {
    padding: 0 6px;
    padding-right: 20px;
    font-size: 13px;
    text-align: center;
    font-weight: 500;
  }

  .mobile-number-input :deep(.el-input__inner) {
    padding: 0 10px;
    font-size: 14px;
  }

  /* 区号选择器下拉箭头优化 */
  .mobile-area-select :deep(.el-input__suffix) {
    right: 4px;
    width: 16px;
  }

  .mobile-area-select :deep(.el-input__suffix .el-input__suffix-inner) {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .mobile-area-select :deep(.el-icon-arrow-up) {
    font-size: 11px;
  }

  /* 确保手机号输入框组件在移动端的最小宽度 */
  .mobile-phone-wrapper {
    min-width: 0;
  }

  .mobile-area-select {
    width: 88px !important;
    min-width: 88px;
    max-width: 88px;
  }
}
</style>
