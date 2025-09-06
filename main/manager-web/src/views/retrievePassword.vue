<template>
  <auth-layout>
    <div class="retrieve-password-form">
      <!-- 页面标题 -->
      <div class="form-header">
        <h2 class="form-title">
          <a-icon type="unlock" class="title-icon" />
          找回密码
        </h2>
        <p class="form-subtitle">通过手机号重置您的密码</p>
      </div>

      <!-- 找回密码表单 -->
      <a-form
        :form="form"
        @submit.prevent="handleRetrievePassword"
        class="retrieve-form-content"
      >
        <!-- 手机号输入 -->
        <a-form-item>
          <a-input-group compact>
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
              placeholder="请输入注册时的手机号"
            >
              <a-icon slot="prefix" type="mobile" />
            </a-input>
          </a-input-group>
        </a-form-item>

        <!-- 图形验证码输入 -->
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

        <!-- 短信验证码输入 -->
        <a-form-item>
          <a-row :gutter="8">
            <a-col :span="15">
              <a-input
                v-decorator="[
                  'smsCode',
                  {
                    rules: [{ required: true, message: '请输入短信验证码' }]
                  }
                ]"
                size="large"
                placeholder="请输入短信验证码"
              >
                <a-icon slot="prefix" type="message" />
              </a-input>
            </a-col>
            <a-col :span="9">
              <a-button
                type="primary"
                size="large"
                block
                :disabled="smsCountdown > 0"
                :loading="smsLoading"
                @click="sendSmsCode"
                class="sms-button"
              >
                {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
              </a-button>
            </a-col>
          </a-row>
        </a-form-item>

        <!-- 新密码输入 -->
        <a-form-item>
          <a-input-password
            v-decorator="[
              'password',
              {
                rules: [
                  { required: true, message: '请输入新密码' },
                  { min: 6, max: 20, message: '密码长度为6-20个字符' }
                ]
              }
            ]"
            size="large"
            placeholder="请输入新密码"
            :visibilityToggle="true"
          >
            <a-icon slot="prefix" type="lock" />
          </a-input-password>
        </a-form-item>

        <!-- 确认新密码输入 -->
        <a-form-item>
          <a-input-password
            v-decorator="[
              'confirmPassword',
              {
                rules: [
                  { required: true, message: '请确认新密码' },
                  { validator: validateConfirmPassword }
                ]
              }
            ]"
            size="large"
            placeholder="请确认新密码"
            :visibilityToggle="true"
          >
            <a-icon slot="prefix" type="lock" />
          </a-input-password>
        </a-form-item>

        <!-- 重置密码按钮 -->
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="retrieveLoading"
            class="retrieve-button"
          >
            <a-icon type="unlock" />
            重置密码
          </a-button>
        </a-form-item>

        <!-- 返回登录 -->
        <div class="form-links">
          <span class="link-text">想起密码了？</span>
          <a @click="goToLogin" class="form-link">
            返回登录
          </a>
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
  name: 'RetrievePassword',
  components: {
    AuthLayout
  },
  computed: {
    ...mapState({
      enableMobileRegister: state => state.pubConfig.enableMobileRegister,
      mobileAreaList: state => state.pubConfig.mobileAreaList
    })
  },
  data() {
    return {
      form: this.$form.createForm(this),
      captchaUuid: '',
      captchaUrl: '',
      retrieveLoading: false,
      smsLoading: false,
      smsCountdown: 0
    }
  },
  mounted() {
    this.checkAuth()
    this.fetchCaptcha()
    this.$store.dispatch('fetchPubConfig')
  },
  methods: {
    checkAuth() {
      if (this.$store.getters.getToken) {
        this.$router.push('/home')
      }
      
      // 检查是否支持手机号找回密码
      if (!this.enableMobileRegister) {
        this.$message.warning('当前系统不支持手机号找回密码')
        this.$router.push('/login')
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
    sendSmsCode() {
      this.form.validateFields(['mobile', 'areaCode', 'captcha'], (err, values) => {
        if (!err) {
          if (!validateMobile(values.mobile, values.areaCode)) {
            this.$message.error('请输入正确的手机号码')
            return
          }

          this.smsLoading = true
          const smsData = {
            phone: values.areaCode + values.mobile,
            captcha: values.captcha,
            captchaId: this.captchaUuid
          }

          Api.user.sendSmsVerification(
            smsData,
            () => {
              this.$message.success('验证码发送成功')
              this.startCountdown()
              this.fetchCaptcha() // 刷新图形验证码
            },
            (err) => {
              this.$message.error(err.data.msg || '验证码发送失败')
              this.fetchCaptcha()
            }
          )
          this.smsLoading = false
        }
      })
    },
    startCountdown() {
      this.smsCountdown = 60
      const timer = setInterval(() => {
        this.smsCountdown--
        if (this.smsCountdown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    },
    validateConfirmPassword(rule, value, callback) {
      const password = this.form.getFieldValue('password')
      if (value && value !== password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    },
    handleRetrievePassword() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.performRetrievePassword(values)
        }
      })
    },
    performRetrievePassword(values) {
      this.retrieveLoading = true

      if (!validateMobile(values.mobile, values.areaCode)) {
        this.$message.error('请输入正确的手机号码')
        this.retrieveLoading = false
        return
      }

      const retrieveData = {
        phone: values.areaCode + values.mobile,
        code: values.smsCode,
        password: values.password
      }

      Api.user.retrievePassword(
        retrieveData,
        () => {
          this.$message.success('密码重置成功！请使用新密码登录')
          this.$router.push('/login')
        },
        (err) => {
          this.$message.error(err.data.msg || '密码重置失败')
          this.fetchCaptcha()
          this.retrieveLoading = false
        }
      )
    },
    goToLogin() {
      goToPage('/login')
    }
  }
}
</script>

<style lang="scss" scoped>
.retrieve-password-form {
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
  
  .retrieve-form-content {
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
    
    .sms-button {
      border-radius: 8px;
      font-size: 14px;
      background: #5778FF;
      border-color: #5778FF;
      
      &:hover:not(:disabled) {
        background: #4a6ae8;
        border-color: #4a6ae8;
      }
      
      &:disabled {
        background: #f5f5f5;
        border-color: #d9d9d9;
        color: rgba(0, 0, 0, 0.25);
      }
    }
    
    .retrieve-button {
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
    
    .form-links {
      text-align: center;
      margin-top: 16px;
      
      .link-text {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.45);
        margin-right: 8px;
      }
      
      .form-link {
        color: #5778FF;
        font-size: 14px;
        cursor: pointer;
        text-decoration: none;
        transition: color 0.3s;
        
        &:hover {
          color: #4a6ae8;
          text-decoration: underline;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .retrieve-password-form {
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
    
    .retrieve-form-content {
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
      
      .sms-button {
        height: 44px;
        font-size: 13px;
      }
      
      .retrieve-button {
        height: 44px;
        font-size: 15px;
      }
      
      .form-links {
        .link-text,
        .form-link {
          font-size: 13px;
        }
      }
    }
  }
}
</style>