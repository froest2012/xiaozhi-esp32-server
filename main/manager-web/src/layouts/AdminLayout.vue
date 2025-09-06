<template>
  <a-layout class="admin-layout">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model="collapsed"
      :trigger="null"
      collapsible
      class="layout-sider"
      :theme="siderTheme"
      :width="siderWidth"
    >
      <admin-sider 
        :collapsed="collapsed"
        :menu-data="menuData"
        :selected-keys="selectedKeys"
        @menu-click="handleMenuClick"
      />
    </a-layout-sider>

    <!-- 主体布局 -->
    <a-layout class="layout-main">
      <!-- 顶部导航 -->
      <a-layout-header class="layout-header">
        <admin-header
          :collapsed="collapsed"
          :user-info="userInfo"
          @toggle="handleToggle"
          @logout="handleLogout"
          @change-password="handleChangePassword"
        />
      </a-layout-header>

      <!-- 内容区域 -->
      <a-layout-content class="layout-content">
        <div class="page-container">
          <router-view />
        </div>
      </a-layout-content>

      <!-- 底部信息 -->
      <a-layout-footer class="layout-footer">
        <admin-footer />
      </a-layout-footer>
    </a-layout>

    <!-- 修改密码对话框 -->
    <change-password-dialog
      :visible="changePasswordVisible"
      @close="changePasswordVisible = false"
      @success="handlePasswordChangeSuccess"
    />
  </a-layout>
</template>

<script>
import { mapGetters } from 'vuex'
import AdminSider from './components/AdminSider.vue'
import AdminHeader from './components/AdminHeader.vue'
import AdminFooter from './components/AdminFooter.vue'
import ChangePasswordDialog from '@/components/ChangePasswordDialog.vue'

export default {
  name: 'AdminLayout',
  components: {
    AdminSider,
    AdminHeader,
    AdminFooter,
    ChangePasswordDialog
  },
  data() {
    return {
      collapsed: false,
      siderTheme: 'dark',
      siderWidth: 200,
      changePasswordVisible: false,
      menuData: [
        {
          key: 'home',
          title: '首页',
          icon: 'home',
          path: '/home'
        },
        {
          key: 'agent',
          title: '智能体管理',
          icon: 'robot',
          children: [
            { key: 'roleConfig', title: '智能体配置', path: '/roleConfig' },
            { key: 'voiceprint', title: '声纹管理', path: '/voiceprint' }
          ]
        },
        {
          key: 'device',
          title: '设备管理',
          icon: 'laptop',
          path: '/device'
        },
        {
          key: 'model',
          title: '模型配置',
          icon: 'experiment',
          children: [
            { key: 'modelConfig', title: '模型管理', path: '/model' },
            { key: 'providerManagement', title: '供应商管理', path: '/provider' }
          ]
        },
        {
          key: 'system',
          title: '系统管理',
          icon: 'setting',
          children: [
            { key: 'userManagement', title: '用户管理', path: '/user' },
            { key: 'paramsManagement', title: '参数管理', path: '/params' },
            { key: 'dictManagement', title: '字典管理', path: '/dict' },
            { key: 'otaManagement', title: 'OTA管理', path: '/ota' },
            { key: 'serverSideManager', title: '服务端管理', path: '/server' }
          ]
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['getUserInfo']),
    userInfo() {
      return this.getUserInfo || {}
    },
    selectedKeys() {
      // 根据当前路由匹配选中的菜单项
      const path = this.$route.path
      return [this.getMenuKeyByPath(path)]
    }
  },
  methods: {
    handleToggle() {
      this.collapsed = !this.collapsed
    },
    handleMenuClick({ key, path }) {
      if (path && this.$route.path !== path) {
        this.$router.push(path)
      }
    },
    handleLogout() {
      this.$confirm({
        title: '确认退出',
        content: '确定要退出系统吗？',
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          this.$store.dispatch('logout').then(() => {
            this.$router.push('/login')
            this.$message.success('已安全退出')
          })
        }
      })
    },
    handleChangePassword() {
      this.changePasswordVisible = true
    },
    handlePasswordChangeSuccess() {
      this.changePasswordVisible = false
      this.$message.success('密码修改成功')
    },
    getMenuKeyByPath(path) {
      // 递归查找路径对应的菜单key
      const findKey = (menus, targetPath) => {
        for (const menu of menus) {
          if (menu.path === targetPath) {
            return menu.key
          }
          if (menu.children) {
            const childKey = findKey(menu.children, targetPath)
            if (childKey) return childKey
          }
        }
        return null
      }
      return findKey(this.menuData, path) || 'home'
    }
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  min-height: 100vh;

  .layout-sider {
    position: fixed;
    height: 100vh;
    left: 0;
    top: 0;
    z-index: 10;
  }

  .layout-main {
    margin-left: 200px;
    transition: margin-left 0.2s;

    &.collapsed {
      margin-left: 80px;
    }
  }

  .layout-header {
    position: fixed;
    top: 0;
    right: 0;
    left: 200px;
    z-index: 9;
    padding: 0;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    transition: left 0.2s;

    &.collapsed {
      left: 80px;
    }
  }

  .layout-content {
    margin-top: 64px;
    padding: 0;
    background: #f0f2f5;
    min-height: calc(100vh - 64px - 69px);
  }

  .layout-footer {
    text-align: center;
    background: #f0f2f5;
    padding: 12px 24px;
    border-top: 1px solid #e8e8e8;
  }

  .page-container {
    padding: 24px;
    margin: 0;
    background: transparent;
  }
}

// 响应式适配
@media (max-width: 768px) {
  .admin-layout {
    .layout-sider {
      transform: translateX(-100%);
      transition: transform 0.3s;
      
      &.ant-layout-sider-collapsed {
        transform: translateX(0);
      }
    }

    .layout-main {
      margin-left: 0;
    }

    .layout-header {
      left: 0;
    }

    .page-container {
      padding: 16px;
    }
  }
}
</style>
