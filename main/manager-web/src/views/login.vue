<template>
  <auth-layout>
    <div class="login-form">
      <!-- 页面标题 -->
      <div class="form-header">
        <h2 class="form-title">
          <a-icon type="login" class="title-icon" />
          欢迎登录
        </h2>
        <p class="form-subtitle">登录您的小智控制台账户</p>
      </div>

      <!-- 登录表单 -->
      <a-form
        :form="form"
        @submit.prevent="handleLogin"
        class="login-form-content"
      >
        <!-- 登录方式切换 -->
        <div class="login-type-switch" v-if="enableMobileRegister">
          <a-radio-group v-model="loginType" size="small">
            <a-radio-button value="username">
              <a-icon type="user" />
              用户名
            </a-radio-button>
            <a-radio-button value="mobile">
              <a-icon type="mobile" />
              手机号
            </a-radio-button>
          </a-radio-group>
        </div>

        <!-- 用户名/手机号输入 -->
        <a-form-item>
          <a-input
            v-if="loginType === 'username'"
            v-decorator="[
              'username',
              {
                rules: [{ required: true, message: '请输入用户名' }]
              }
            ]"
            size="large"
            placeholder="请输入用户名"
          >
            <a-icon slot="prefix" type="user" />
          </a-input>
          
          <a-input-group v-else compact>
            <a-select
              v-decorator="[
                'areaCode',
                { initialValue: '+86' }
              ]"
              style="width: 100px"
              size="large"
            >
              <a-select-option
                v-for="item in mobileAreaList"
                :key="item.key"
                :value="item.key"
              >
                {{ item.key }}
              </a-select-option>
            </a-select>
            <a-input
              v-decorator="[
                'mobile',
                {
                  rules: [
                    { required: true, message: '请输入手机号' },
                    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
                  ]
                }
              ]"
              style="width: calc(100% - 100px)"
              size="large"
              placeholder="请输入手机号"
            >
              <a-icon slot="prefix" type="mobile" />
            </a-input>
          </a-input-group>
        </a-form-item>

        <!-- 密码输入 -->
        <a-form-item>
          <a-input-password
            v-decorator="[
              'password',
              {
                rules: [{ required: true, message: '请输入密码' }]
              }
            ]"
            size="large"
            placeholder="请输入密码"
            :visibilityToggle="true"
          >
            <a-icon slot="prefix" type="lock" />
          </a-input-password>
        </a-form-item>

        <!-- 验证码输入 -->
        <a-form-item>
          <a-row :gutter="8">
            <a-col :span="15">
              <a-input
                v-decorator="[
                  'captcha',
                  {
                    rules: [{ required: true, message: '请输入验证码' }]
                  }
                ]"
                size="large"
                placeholder="请输入验证码"
              >
                <a-icon slot="prefix" type="safety-certificate" />
              </a-input>
            </a-col>
            <a-col :span="9">
              <div class="captcha-container">
                <img
                  v-if="captchaUrl"
                  :src="captchaUrl"
                  alt="验证码"
                  class="captcha-image"
                  @click="fetchCaptcha"
                />
                <a-spin v-else />
              </div>
            </a-col>
          </a-row>
        </a-form-item>

        <!-- 操作链接 -->
        <div class="form-links">
          <a
            v-if="allowUserRegister"
            @click="goToRegister"
            class="form-link"
          >
            新用户注册
          </a>
          <a
            v-if="enableMobileRegister"
            @click="goToForgetPassword"
            class="form-link"
          >
            忘记密码？
          </a>
        </div>

        <!-- 登录按钮 -->
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loginLoading"
            class="login-button"
          >
            <a-icon type="login" />
            立即登录
          </a-button>
        </a-form-item>

        <!-- 用户协议 -->
        <div class="form-agreement">
          <span class="agreement-text">
            登录即同意
            <a href="#" class="agreement-link">《用户协议》</a>
            和
            <a href="#" class="agreement-link">《隐私政策》</a>
          </span>
        </div>
      </a-form>
    </div>
  </auth-layout>
</template>

<script>
import Api from '@/apis/api'
import AuthLayout from '@/layouts/AuthLayout.vue'
import { getUUID, goToPage, validateMobile } from '@/utils'
import { mapState } from 'vuex'

export default {
  name: 'Login',
  components: {
    AuthLayout
  },
  computed: {
    ...mapState({
      allowUserRegister: state => state.pubConfig.allowUserRegister,
      enableMobileRegister: state => state.pubConfig.enableMobileRegister,
      mobileAreaList: state => state.pubConfig.mobileAreaList
    })
  },
  data() {
    return {
      form: this.$form.createForm(this),
      loginType: 'username',
      captchaUuid: '',
      captchaUrl: '',
      loginLoading: false
    }
  },
  mounted() {
    this.checkAuth()
    this.fetchCaptcha()
    this.$store.dispatch('fetchPubConfig').then(() => {
      // 根据配置决定默认登录方式
      if (this.enableMobileRegister) {
        this.loginType = 'mobile'
      }
    })
  },
  watch: {
    loginType() {
      // 切换登录方式时重置表单
      this.form.resetFields()
      this.fetchCaptcha()
    }
  },
  methods: {
    checkAuth() {
      if (this.$store.getters.getToken) {
        if (this.$route.path !== '/home') {
          this.$router.push('/home')
        }
      }
    },
    fetchCaptcha() {
      this.captchaUuid = getUUID()
      Api.user.getCaptcha(this.captchaUuid, (res) => {
        if (res.status === 200) {
          const blob = new Blob([res.data], { type: res.data.type })
          this.captchaUrl = URL.createObjectURL(blob)
        } else {
          this.$message.error('验证码加载失败，点击刷新')
        }
      })
    },
    handleLogin() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.performLogin(values)
        }
      })
    },
    performLogin(values) {
      this.loginLoading = true
      
      const loginData = {
        password: values.password,
        captcha: values.captcha,
        captchaId: this.captchaUuid
      }

      if (this.loginType === 'mobile') {
        // 手机号登录
        if (!validateMobile(values.mobile, values.areaCode)) {
          this.$message.error('请输入正确的手机号码')
          this.loginLoading = false
          return
        }
        loginData.username = values.areaCode + values.mobile
      } else {
        // 用户名登录
        loginData.username = values.username
      }

      Api.user.login(
        loginData,
        ({ data }) => {
          this.$message.success('登录成功！')
          this.$store.commit('setToken', JSON.stringify(data.data))
          goToPage('/home')
        },
        (err) => {
          this.$message.error(err.data.msg || '登录失败')
          if (err.data && err.data.msg && err.data.msg.indexOf('图形验证码') > -1) {
            this.fetchCaptcha()
          }
          this.loginLoading = false
        }
      )

      // 重新获取验证码
      setTimeout(() => {
        this.fetchCaptcha()
      }, 1000)
    },
    goToRegister() {
      goToPage('/register')
    },
    goToForgetPassword() {
      goToPage('/retrieve-password')
    }
  }
}
</script>
<style lang="scss" scoped>
.login-form {
  width: 100%;
  
  .form-header {
    text-align: center;
    margin-bottom: 32px;
    
    .form-title {
      font-size: 24px;
      font-weight: 600;
      color: rgba(0, 0, 0, 0.85);
      margin-bottom: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      
      .title-icon {
        color: #5778FF;
        font-size: 28px;
      }
    }
    
    .form-subtitle {
      color: rgba(0, 0, 0, 0.45);
      font-size: 14px;
      margin: 0;
    }
  }
  
  .login-form-content {
    .login-type-switch {
      text-align: center;
      margin-bottom: 24px;
      
      :deep(.ant-radio-button-wrapper) {
        border-radius: 6px;
        
        &:first-child {
          border-radius: 6px 0 0 6px;
        }
        
        &:last-child {
          border-radius: 0 6px 6px 0;
        }
        
        &.ant-radio-button-wrapper-checked {
          background: #5778FF;
          border-color: #5778FF;
          color: #fff;
          
          &::before {
            background-color: #5778FF;
          }
        }
      }
    }
    
    :deep(.ant-form-item) {
      margin-bottom: 20px;
    }
    
    :deep(.ant-input-affix-wrapper),
    :deep(.ant-input) {
      border-radius: 8px;
      border: 1px solid #d9d9d9;
      transition: all 0.3s;
      
      &:hover {
        border-color: #5778FF;
      }
      
      &:focus,
      &.ant-input-affix-wrapper-focused {
        border-color: #5778FF;
        box-shadow: 0 0 0 2px rgba(87, 120, 255, 0.2);
      }
    }
    
    .captcha-container {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 40px;
      background: #f5f5f5;
      border-radius: 8px;
      border: 1px solid #d9d9d9;
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        border-color: #5778FF;
        background: rgba(87, 120, 255, 0.05);
      }
      
      .captcha-image {
        width: 100%;
        height: 38px;
        border-radius: 6px;
        object-fit: cover;
      }
    }
    
    .form-links {
      display: flex;
      justify-content: space-between;
      margin-bottom: 24px;
      
      .form-link {
        color: #5778FF;
        font-size: 14px;
        cursor: pointer;
        transition: color 0.3s;
        
        &:hover {
          color: #4a6ae8;
          text-decoration: underline;
        }
      }
    }
    
    .login-button {
      border-radius: 8px;
      font-size: 16px;
      font-weight: 500;
      height: 48px;
      background: #5778FF;
      border-color: #5778FF;
      
      &:hover,
      &:focus {
        background: #4a6ae8;
        border-color: #4a6ae8;
      }
      
      &:active {
        background: #3d5cd6;
        border-color: #3d5cd6;
      }
    }
    
    .form-agreement {
      text-align: center;
      margin-top: 16px;
      
      .agreement-text {
        font-size: 12px;
        color: rgba(0, 0, 0, 0.45);
        
        .agreement-link {
          color: #5778FF;
          text-decoration: none;
          
          &:hover {
            text-decoration: underline;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .login-form {
    .form-header {
      margin-bottom: 24px;
      
      .form-title {
        font-size: 20px;
        
        .title-icon {
          font-size: 24px;
        }
      }
      
      .form-subtitle {
        font-size: 13px;
      }
    }
    
    .login-form-content {
      .login-type-switch {
        margin-bottom: 20px;
        
        :deep(.ant-radio-button-wrapper) {
          font-size: 13px;
          height: 32px;
          line-height: 30px;
        }
      }
      
      :deep(.ant-form-item) {
        margin-bottom: 16px;
      }
      
      :deep(.ant-input-lg) {
        height: 44px;
        font-size: 14px;
      }
      
      .captcha-container {
        height: 44px;
        
        .captcha-image {
          height: 42px;
        }
      }
      
      .login-button {
        height: 44px;
        font-size: 15px;
      }
      
      .form-agreement {
        .agreement-text {
          font-size: 11px;
        }
      }
    }
  }
}
</style>
