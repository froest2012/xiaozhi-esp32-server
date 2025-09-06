<template>
  <div class="admin-sider">
    <!-- Logo区域 -->
    <div class="sider-logo">
      <img v-if="!collapsed" src="@/assets/xiaozhi-logo.png" alt="小智" />
      <img v-else src="@/assets/xiaozhi-ai.png" alt="小智" class="logo-collapsed" />
      <span v-if="!collapsed" class="logo-title">小智控制台</span>
    </div>

    <!-- 菜单区域 -->
    <a-menu
      mode="inline"
      theme="dark"
      :selected-keys="selectedKeys"
      :open-keys="openKeys"
      :inline-collapsed="collapsed"
      @click="handleMenuClick"
      @openChange="handleOpenChange"
    >
      <template v-for="item in menuData">
        <a-menu-item v-if="!item.children" :key="item.key">
          <a-icon :type="item.icon" />
          <span>{{ item.title }}</span>
        </a-menu-item>
        
        <a-sub-menu v-else :key="item.key">
          <span slot="title">
            <a-icon :type="item.icon" />
            <span>{{ item.title }}</span>
          </span>
          <a-menu-item
            v-for="child in item.children"
            :key="child.key"
          >
            <a-icon v-if="child.icon" :type="child.icon" />
            <span>{{ child.title }}</span>
          </a-menu-item>
        </a-sub-menu>
      </template>
    </a-menu>
  </div>
</template>

<script>
export default {
  name: 'AdminSider',
  props: {
    collapsed: {
      type: Boolean,
      default: false
    },
    menuData: {
      type: Array,
      default: () => []
    },
    selectedKeys: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      openKeys: []
    }
  },
  watch: {
    selectedKeys: {
      handler(newKeys) {
        // 根据选中的菜单项自动展开对应的子菜单
        if (newKeys.length > 0) {
          const selectedKey = newKeys[0]
          const parentKey = this.findParentKey(selectedKey)
          if (parentKey && !this.openKeys.includes(parentKey)) {
            this.openKeys = [parentKey]
          }
        }
      },
      immediate: true
    },
    collapsed(val) {
      if (val) {
        this.openKeys = []
      }
    }
  },
  methods: {
    handleMenuClick({ key }) {
      const menuItem = this.findMenuItem(key)
      if (menuItem && menuItem.path) {
        this.$emit('menu-click', {
          key,
          path: menuItem.path
        })
      }
    },
    handleOpenChange(openKeys) {
      this.openKeys = openKeys
    },
    findMenuItem(key) {
      const findInMenu = (menus) => {
        for (const menu of menus) {
          if (menu.key === key) {
            return menu
          }
          if (menu.children) {
            const found = findInMenu(menu.children)
            if (found) return found
          }
        }
        return null
      }
      return findInMenu(this.menuData)
    },
    findParentKey(childKey) {
      for (const menu of this.menuData) {
        if (menu.children) {
          const found = menu.children.find(child => child.key === childKey)
          if (found) return menu.key
        }
      }
      return null
    }
  }
}
</script>

<style lang="scss" scoped>
.admin-sider {
  height: 100vh;
  overflow: auto;
  
  .sider-logo {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 16px;
    background: rgba(255, 255, 255, 0.1);
    margin: 16px 16px 24px;
    border-radius: 6px;
    transition: all 0.3s;
    
    img {
      width: 32px;
      height: 32px;
      filter: brightness(0) invert(1);
      transition: all 0.3s;
      
      &.logo-collapsed {
        width: 28px;
        height: 28px;
      }
    }
    
    .logo-title {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      margin-left: 12px;
      white-space: nowrap;
      transition: all 0.3s;
    }
  }
  
  :deep(.ant-menu) {
    border-right: none;
    
    .ant-menu-item {
      margin: 4px 12px;
      border-radius: 6px;
      height: 40px;
      line-height: 40px;
      
      &.ant-menu-item-selected {
        background-color: #5778FF !important;
        
        &::after {
          display: none;
        }
      }
    }
    
    .ant-menu-submenu-title {
      margin: 4px 12px;
      border-radius: 6px;
      height: 40px;
      line-height: 40px;
    }
    
    .ant-menu-submenu {
      .ant-menu-item {
        margin: 2px 12px 2px 24px;
        height: 36px;
        line-height: 36px;
      }
    }
    
    .ant-menu-item:hover,
    .ant-menu-submenu-title:hover {
      background-color: rgba(87, 120, 255, 0.8) !important;
    }
  }
}

// 滚动条样式
.admin-sider::-webkit-scrollbar {
  width: 6px;
}

.admin-sider::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

.admin-sider::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  
  &:hover {
    background: rgba(255, 255, 255, 0.5);
  }
}
</style>
