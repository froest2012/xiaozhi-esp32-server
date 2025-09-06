<template>
  <div class="auth-layout">
    <!-- 背景装饰 -->
    <div class="auth-background">
      <div class="bg-decoration">
        <div class="decoration-item decoration-1"></div>
        <div class="decoration-item decoration-2"></div>
        <div class="decoration-item decoration-3"></div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="auth-container">
      <!-- 左侧信息区域 -->
      <div class="auth-info">
        <div class="brand-section">
          <div class="logo">
            <img src="@/assets/xiaozhi-logo.png" alt="小智" />
            <h1>小智控制台</h1>
          </div>
          <div class="slogan">
            <h2>智能语音交互，让生活更简单</h2>
            <p>基于AI技术的智能语音助手管理平台</p>
          </div>
        </div>
        
        <div class="feature-list">
          <div class="feature-item">
            <a-icon type="robot" />
            <span>智能体管理</span>
          </div>
          <div class="feature-item">
            <a-icon type="sound" />
            <span>语音交互</span>
          </div>
          <div class="feature-item">
            <a-icon type="setting" />
            <span>设备配置</span>
          </div>
          <div class="feature-item">
            <a-icon type="cloud" />
            <span>云端服务</span>
          </div>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="auth-form">
        <a-card class="form-card" :bordered="false">
          <router-view />
        </a-card>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="auth-footer">
      <auth-footer />
    </div>
  </div>
</template>

<script>
import AuthFooter from './components/AuthFooter.vue'

export default {
  name: 'AuthLayout',
  components: {
    AuthFooter
  }
}
</script>

<style lang="scss" scoped>
.auth-layout {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.auth-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .bg-decoration {
    position: absolute;
    width: 100%;
    height: 100%;
    
    .decoration-item {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      
      &.decoration-1 {
        width: 300px;
        height: 300px;
        top: -150px;
        right: -150px;
        animation: float 6s ease-in-out infinite;
      }
      
      &.decoration-2 {
        width: 200px;
        height: 200px;
        bottom: -100px;
        left: -100px;
        animation: float 8s ease-in-out infinite reverse;
      }
      
      &.decoration-3 {
        width: 150px;
        height: 150px;
        top: 20%;
        left: 10%;
        animation: float 10s ease-in-out infinite;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.auth-container {
  position: relative;
  display: flex;
  min-height: 100vh;
  z-index: 1;
}

.auth-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 80px;
  color: white;

  .brand-section {
    margin-bottom: 60px;
    
    .logo {
      display: flex;
      align-items: center;
      margin-bottom: 32px;
      
      img {
        width: 64px;
        height: 64px;
        margin-right: 16px;
        filter: brightness(0) invert(1);
      }
      
      h1 {
        font-size: 32px;
        font-weight: 600;
        color: white;
        margin: 0;
      }
    }
    
    .slogan {
      h2 {
        font-size: 28px;
        font-weight: 500;
        margin-bottom: 16px;
        color: white;
      }
      
      p {
        font-size: 16px;
        color: rgba(255, 255, 255, 0.8);
        line-height: 1.6;
        margin: 0;
      }
    }
  }

  .feature-list {
    .feature-item {
      display: flex;
      align-items: center;
      margin-bottom: 24px;
      font-size: 16px;
      color: rgba(255, 255, 255, 0.9);
      
      .anticon {
        font-size: 20px;
        margin-right: 12px;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

.auth-form {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  
  .form-card {
    width: 100%;
    max-width: 400px;
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
    
    :deep(.ant-card-body) {
      padding: 40px;
    }
  }
}

.auth-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 2;
  text-align: center;
  padding: 20px;
  color: rgba(255, 255, 255, 0.8);
}

// 响应式设计
@media (max-width: 1024px) {
  .auth-container {
    flex-direction: column;
  }

  .auth-info {
    flex: none;
    padding: 40px 40px 20px;
    text-align: center;

    .brand-section {
      margin-bottom: 30px;
      
      .logo {
        justify-content: center;
        
        h1 {
          font-size: 24px;
        }
      }
      
      .slogan {
        h2 {
          font-size: 20px;
        }
        
        p {
          font-size: 14px;
        }
      }
    }

    .feature-list {
      display: flex;
      justify-content: center;
      flex-wrap: wrap;
      gap: 20px;
      
      .feature-item {
        margin-bottom: 0;
        font-size: 14px;
      }
    }
  }

  .auth-form {
    flex: none;
    padding: 20px 40px 40px;
    
    .form-card {
      max-width: none;
      
      :deep(.ant-card-body) {
        padding: 30px;
      }
    }
  }
}

@media (max-width: 768px) {
  .auth-info {
    padding: 30px 20px 15px;
    
    .brand-section {
      margin-bottom: 20px;
      
      .logo {
        img {
          width: 48px;
          height: 48px;
        }
        
        h1 {
          font-size: 20px;
        }
      }
      
      .slogan {
        h2 {
          font-size: 18px;
        }
      }
    }
  }

  .auth-form {
    padding: 15px 20px 30px;
    
    .form-card {
      :deep(.ant-card-body) {
        padding: 24px;
      }
    }
  }
}
</style>
