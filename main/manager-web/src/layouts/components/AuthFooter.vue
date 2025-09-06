<template>
  <div class="auth-footer">
    <div class="footer-links">
      <a href="#" class="footer-link">服务条款</a>
      <a-divider type="vertical" />
      <a href="#" class="footer-link">隐私政策</a>
      <a-divider type="vertical" />
      <a href="#" class="footer-link">帮助中心</a>
    </div>
    <div class="copyright">
      © {{ currentYear }} 小智控制台. All rights reserved.
    </div>
  </div>
</template>

<script>
export default {
  name: 'AuthFooter',
  computed: {
    currentYear() {
      return new Date().getFullYear()
    }
  }
}
</script>

<style lang="scss" scoped>
.auth-footer {
  .footer-links {
    margin-bottom: 8px;

    .footer-link {
      color: rgba(255, 255, 255, 0.8);
      text-decoration: none;
      font-size: 14px;
      transition: color 0.3s;

      &:hover {
        color: #fff;
      }
    }

    .ant-divider-vertical {
      background: rgba(255, 255, 255, 0.3);
      margin: 0 12px;
    }
  }

  .copyright {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
  }
}

@media (max-width: 768px) {
  .auth-footer {
    .footer-links {
      .footer-link {
        font-size: 12px;
      }

      .ant-divider-vertical {
        margin: 0 8px;
      }
    }

    .copyright {
      font-size: 11px;
    }
  }
}
</style>
