<template>
  <div class="admin-header">
    <div class="header-left">
      <!-- 折叠按钮 -->
      <a-button
        type="link"
        class="trigger"
        @click="handleToggle"
      >
        <a-icon :type="collapsed ? 'menu-unfold' : 'menu-fold'" />
      </a-button>

      <!-- 面包屑导航 -->
      <a-breadcrumb class="breadcrumb">
        <a-breadcrumb-item>
          <a-icon type="home" />
          <span>首页</span>
        </a-breadcrumb-item>
        <a-breadcrumb-item v-if="currentPage.parent">
          {{ currentPage.parent }}
        </a-breadcrumb-item>
        <a-breadcrumb-item>
          {{ currentPage.title }}
        </a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <div class="header-right">
      <!-- 快捷操作 -->
      <div class="quick-actions">
        <!-- 全屏切换 -->
        <a-tooltip title="全屏">
          <a-button type="link" class="action-btn" @click="toggleFullscreen">
            <a-icon :type="isFullscreen ? 'fullscreen-exit' : 'fullscreen'" />
          </a-button>
        </a-tooltip>

        <!-- 刷新页面 -->
        <a-tooltip title="刷新">
          <a-button type="link" class="action-btn" @click="refreshPage">
            <a-icon type="reload" />
          </a-button>
        </a-tooltip>
      </div>

      <!-- 用户信息 -->
      <a-dropdown placement="bottomRight">
        <div class="user-info">
          <a-avatar :src="userInfo.avatar" :style="{ backgroundColor: '#5778FF' }">
            {{ userInfo.username ? userInfo.username.charAt(0).toUpperCase() : 'U' }}
          </a-avatar>
          <span class="username">{{ userInfo.username || '未知用户' }}</span>
          <a-icon type="down" class="dropdown-icon" />
        </div>
        
        <a-menu slot="overlay" @click="handleUserMenuClick">
          <a-menu-item key="profile">
            <a-icon type="user" />
            个人资料
          </a-menu-item>
          <a-menu-item key="changePassword">
            <a-icon type="lock" />
            修改密码
          </a-menu-item>
          <a-menu-divider />
          <a-menu-item key="logout">
            <a-icon type="logout" />
            退出登录
          </a-menu-item>
        </a-menu>
      </a-dropdown>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminHeader',
  props: {
    collapsed: {
      type: Boolean,
      default: false
    },
    userInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      isFullscreen: false
    }
  },
  computed: {
    currentPage() {
      const route = this.$route
      const routeMap = {
        '/home': { title: '首页' },
        '/roleConfig': { title: '智能体配置', parent: '智能体管理' },
        '/voiceprint': { title: '声纹管理', parent: '智能体管理' },
        '/device': { title: '设备管理' },
        '/model': { title: '模型管理', parent: '模型配置' },
        '/provider': { title: '供应商管理', parent: '模型配置' },
        '/user': { title: '用户管理', parent: '系统管理' },
        '/params': { title: '参数管理', parent: '系统管理' },
        '/dict': { title: '字典管理', parent: '系统管理' },
        '/ota': { title: 'OTA管理', parent: '系统管理' },
        '/server': { title: '服务端管理', parent: '系统管理' }
      }
      return routeMap[route.path] || { title: '未知页面' }
    }
  },
  mounted() {
    // 监听全屏状态变化
    document.addEventListener('fullscreenchange', this.handleFullscreenChange)
    document.addEventListener('webkitfullscreenchange', this.handleFullscreenChange)
    document.addEventListener('mozfullscreenchange', this.handleFullscreenChange)
    document.addEventListener('MSFullscreenChange', this.handleFullscreenChange)
  },
  beforeDestroy() {
    // 清除事件监听
    document.removeEventListener('fullscreenchange', this.handleFullscreenChange)
    document.removeEventListener('webkitfullscreenchange', this.handleFullscreenChange)
    document.removeEventListener('mozfullscreenchange', this.handleFullscreenChange)
    document.removeEventListener('MSFullscreenChange', this.handleFullscreenChange)
  },
  methods: {
    handleToggle() {
      this.$emit('toggle')
    },
    handleUserMenuClick({ key }) {
      switch (key) {
        case 'profile':
          this.handleProfile()
          break
        case 'changePassword':
          this.$emit('change-password')
          break
        case 'logout':
          this.$emit('logout')
          break
      }
    },
    handleProfile() {
      this.$message.info('个人资料功能待开发')
    },
    toggleFullscreen() {
      if (!this.isFullscreen) {
        this.requestFullscreen()
      } else {
        this.exitFullscreen()
      }
    },
    requestFullscreen() {
      const element = document.documentElement
      if (element.requestFullscreen) {
        element.requestFullscreen()
      } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen()
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen()
      } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen()
      }
    },
    exitFullscreen() {
      if (document.exitFullscreen) {
        document.exitFullscreen()
      } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen()
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen()
      } else if (document.msExitFullscreen) {
        document.msExitFullscreen()
      }
    },
    handleFullscreenChange() {
      this.isFullscreen = !!(
        document.fullscreenElement ||
        document.webkitFullscreenElement ||
        document.mozFullScreenElement ||
        document.msFullscreenElement
      )
    },
    refreshPage() {
      // 刷新当前路由
      this.$router.go(0)
    }
  }
}
</script>

<style lang="scss" scoped>
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .header-left {
    display: flex;
    align-items: center;
    flex: 1;

    .trigger {
      font-size: 18px;
      line-height: 64px;
      padding: 0 12px;
      cursor: pointer;
      transition: color 0.3s;
      color: rgba(0, 0, 0, 0.65);

      &:hover {
        color: #5778FF;
        background: rgba(87, 120, 255, 0.1);
      }
    }

    .breadcrumb {
      margin-left: 16px;
      
      :deep(.ant-breadcrumb-link) {
        color: rgba(0, 0, 0, 0.65);
        
        .anticon {
          margin-right: 4px;
        }
      }
      
      :deep(.ant-breadcrumb-separator) {
        color: rgba(0, 0, 0, 0.45);
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;

    .quick-actions {
      display: flex;
      align-items: center;
      margin-right: 16px;

      .action-btn {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 40px;
        height: 40px;
        font-size: 16px;
        color: rgba(0, 0, 0, 0.65);
        border-radius: 6px;
        transition: all 0.3s;

        &:hover {
          color: #5778FF;
          background: rgba(87, 120, 255, 0.1);
        }
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      padding: 8px 12px;
      cursor: pointer;
      border-radius: 6px;
      transition: all 0.3s;

      &:hover {
        background: rgba(0, 0, 0, 0.04);
      }

      .username {
        margin: 0 8px;
        color: rgba(0, 0, 0, 0.85);
        font-weight: 500;
      }

      .dropdown-icon {
        font-size: 12px;
        color: rgba(0, 0, 0, 0.45);
        transition: transform 0.3s;
      }

      &:hover .dropdown-icon {
        transform: translateY(1px);
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .admin-header {
    padding: 0 16px;

    .header-left {
      .breadcrumb {
        display: none;
      }
    }

    .header-right {
      .quick-actions {
        margin-right: 8px;
      }

      .user-info {
        .username {
          display: none;
        }
      }
    }
  }
}
</style>
