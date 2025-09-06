<template>
  <div class="admin-footer">
    <div class="footer-content">
      <span class="copyright">
        © {{ currentYear }} 小智控制台 - 智能语音交互管理平台
      </span>
      <a-divider type="vertical" />
      <span class="version">
        版本 {{ version }}
      </span>
      <a-divider type="vertical" />
      <a href="#" class="link">帮助文档</a>
      <a-divider type="vertical" />
      <a href="#" class="link">技术支持</a>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminFooter',
  computed: {
    currentYear() {
      return new Date().getFullYear()
    },
    version() {
      return process.env.VUE_APP_VERSION || '1.0.0'
    }
  }
}
</script>

<style lang="scss" scoped>
.admin-footer {
  .footer-content {
    display: flex;
    align-items: center;
    justify-content: center;
    color: rgba(0, 0, 0, 0.45);
    font-size: 12px;

    .copyright {
      font-weight: 500;
    }

    .version {
      color: rgba(0, 0, 0, 0.65);
    }

    .link {
      color: rgba(0, 0, 0, 0.45);
      text-decoration: none;
      transition: color 0.3s;

      &:hover {
        color: #5778FF;
      }
    }

    .ant-divider-vertical {
      margin: 0 8px;
      background: rgba(0, 0, 0, 0.15);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .admin-footer {
    .footer-content {
      flex-direction: column;
      gap: 8px;

      .ant-divider-vertical {
        display: none;
      }
    }
  }
}
</style>
