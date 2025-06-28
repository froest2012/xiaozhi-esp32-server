<template>
  <div class="welcome">
    <HeaderBar />

    <div :class="['operation-bar', { 'mobile-operation-bar': isMobile }]">
      <h2 class="page-title">服务端管理</h2>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div :class="['content-area', { 'mobile-content-area': isMobile }]">
          <el-card :class="['params-card', { 'mobile-params-card': isMobile }]" shadow="never">
            <div :class="['table-container', { 'mobile-table-container': isMobile }]">
              <el-table ref="paramsTable" :data="paramsList" :class="['transparent-table', { 'mobile-data-table': isMobile }]" v-loading="loading"
                element-loading-text="拼命加载中" element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(255, 255, 255, 0.7)" :header-cell-class-name="headerCellClassName">
                <el-table-column label="选择" align="center" :width="isMobile ? 50 : 120">
                  <template slot-scope="scope">
                    <el-checkbox v-model="scope.row.selected" :class="{ 'mobile-checkbox': isMobile }"></el-checkbox>
                  </template>
                </el-table-column>
                <el-table-column label="ws地址" prop="address" align="center" :show-overflow-tooltip="isMobile"></el-table-column>
                <el-table-column label="操作" prop="operator" align="center" :width="isMobile ? 120 : undefined" show-overflow-tooltip>
                  <template slot-scope="scope">
                    <el-button :size="isMobile ? 'mini' : 'medium'" type="text" @click="emitAction(scope.row, actionMap.restart)" :class="{ 'mobile-btn': isMobile }">重启</el-button>
                    <el-button :size="isMobile ? 'mini' : 'medium'" type="text"
                      @click="emitAction(scope.row, actionMap.update_config)" :class="{ 'mobile-btn': isMobile }">{{ isMobile ? '更新' : '更新配置' }}</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <el-footer>
      <version-footer />
    </el-footer>
  </div>
</template>

<script>
import Api from "@/apis/api";
import HeaderBar from "@/components/HeaderBar.vue";
import ParamDialog from "@/components/ParamDialog.vue";
import VersionFooter from "@/components/VersionFooter.vue";
import { isMobileDevice } from "@/utils/index";

export default {
  components: { HeaderBar, ParamDialog, VersionFooter },
  data() {
    return {
      paramsList: [],
      actionMap: {
        restart: {
          value: 'restart',
          title: "重启服务端",
          message: "确定要重启服务端吗？",
          confirmText: "重启",
        },
        update_config: {
          value: 'update_config',
          title: "更新配置",
          message: "确定要更新配置吗？",
          confirmText: "更新",
        }
      },
      currentPage: 1,
      loading: false,
      pageSize: 10,
      pageSizeOptions: [10, 20, 50, 100],
      total: 0,
      dialogVisible: false,
      dialogTitle: "新增参数",
      isAllSelected: false,
      sensitive_keys: ["api_key", "personal_access_token", "access_token", "token", "secret", "access_key_secret", "secret_key"],
      paramForm: {
        id: null,
        paramCode: "",
        paramValue: "",
        remark: ""
      },
    };
  },
  computed: {
    isMobile() {
      return isMobileDevice();
    },
    pageCount() {
      return Math.ceil(this.total / this.pageSize);
    },
    visiblePages() {
      const pages = [];
      const maxVisible = this.isMobile ? 2 : 3;
      let start = Math.max(1, this.currentPage - 1);
      let end = Math.min(this.pageCount, start + maxVisible - 1);

      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1);
      }

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      return pages;
    },
  },
  created() {
    this.fetchParams();
  },
  methods: {
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchParams();
    },
    fetchParams() {
      this.loading = true;
      Api.admin.getWsServerList(
        {},
        ({ data }) => {
          this.loading = false;
          if (data.code === 0) {
            this.paramsList = data.data.map(item => ({ address: item }));
            this.total = data.data.length;
          } else {
            this.$message.error({
              message: data.msg || '获取参数列表失败',
              showClose: true
            });
          }
        }
      );
    },
    emitAction(rowItem, actionItem) {
      if (actionItem === undefined || rowItem.address === undefined) {
        return;
      }
      // 弹开询问框
      this.$confirm(actionItem.message, actionItem.title, {
        confirmButtonText: actionItem.confirmText, // 确认按钮文本
      }).then(() => {
        // 用户点击了确认按钮
        Api.admin.sendWsServerAction({
          targetWs: rowItem.address,
          action: actionItem.value
        }, ({ data }) => {
          if (data.code !== 0) {
            this.$message.error({
              message: data.msg || '操作失败',
              showClose: true
            });
            return;
          }
          this.$message.success({
            message: `${actionItem.title}成功`,
            showClose: true
          })
        })
      })
    },
    headerCellClassName({ columnIndex }) {
      if (columnIndex === 0) {
        return "custom-selection-header";
      }
      return "";
    }
  },
};
</script>

<style lang="scss" scoped>
.welcome {
  min-width: 900px;
  min-height: 506px;
  height: 100vh;
  display: flex;
  position: relative;
  flex-direction: column;
  background-size: cover;
  background: linear-gradient(to bottom right, #dce8ff, #e4eeff, #e6cbfd) center;
  -webkit-background-size: cover;
  -o-background-size: cover;
  overflow: hidden;

  @media (max-width: 768px) {
    min-width: unset;
    overflow-x: hidden;
    overflow-y: auto;
  }
}

.main-wrapper {
  margin: 5px 22px;
  border-radius: 15px;
  min-height: calc(100vh - 24vh);
  height: auto;
  max-height: 80vh;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  background: rgba(237, 242, 255, 0.5);
  display: flex;
  flex-direction: column;

  @media (max-width: 768px) {
    margin: 5px 16px;
    max-height: none;
    min-height: auto;
    height: auto;
    flex: 1;
  }
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;

  &.mobile-operation-bar {
    padding: 12px 16px;
    justify-content: center;
  }
}

.page-title {
  font-size: 24px;
  margin: 0;

  @media (max-width: 768px) {
    font-size: 18px;
  }
}

.right-operations {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.search-input {
  width: 240px;
}

.btn-search {
  background: linear-gradient(135deg, #6b8cff, #a966ff);
  border: none;
  color: white;
}

.content-panel {
  flex: 1;
  display: flex;
  overflow: hidden;
  height: 100%;
  border-radius: 15px;
  background: transparent;
  border: 1px solid #fff;
}

.content-area {
  flex: 1;
  height: 100%;
  min-width: 600px;
  overflow: auto;
  background-color: white;
  display: flex;
  flex-direction: column;

  &.mobile-content-area {
    min-width: 100%;
    overflow-x: visible;
  }
}

.params-card {
  background: white;
  flex: 1;
  display: flex;
  flex-direction: column;
  border: none;
  box-shadow: none;
  overflow: hidden;

  ::v-deep .el-card__body {
    padding: 15px;
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
  }

  &.mobile-params-card {
    ::v-deep .el-card__body {
      padding: 8px;
    }
  }
}

// 移动端表格容器
.table-container {
  &.mobile-table-container {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }
}

.table_bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-bottom: 10px;
}

.ctrl_btn {
  display: flex;
  gap: 8px;
  padding-left: 26px;

  .el-button {
    min-width: 72px;
    height: 32px;
    padding: 7px 12px 7px 10px;
    font-size: 12px;
    border-radius: 4px;
    line-height: 1;
    font-weight: 500;
    border: none;
    transition: all 0.3s ease;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    }
  }

  .el-button--primary {
    background: #5f70f3;
    color: white;
  }

  .el-button--danger {
    background: #fd5b63;
    color: white;
  }
}

:deep(.transparent-table) {
  background: white;
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: column;

  .el-table__body-wrapper {
    flex: 1;
    overflow-y: auto;
    max-height: none !important;
  }

  .el-table__header-wrapper {
    flex-shrink: 0;
  }

  .el-table__header th {
    background: white !important;
    color: black;
  }

  &::before {
    display: none;
  }

  .el-table__body tr {
    background-color: white;

    td {
      border-top: 1px solid rgba(0, 0, 0, 0.04);
      border-bottom: 1px solid rgba(0, 0, 0, 0.04);
    }
  }

  &.mobile-data-table {
    min-width: 100%;
    font-size: 12px;

    .el-table__header th {
      padding: 8px 0;
      font-size: 12px;
    }

    .el-table__body td {
      padding: 8px 0;
    }

    .cell {
      padding: 0 4px;
      font-size: 12px;
    }
  }
}


:deep(.el-checkbox__inner) {
  background-color: #eeeeee !important;
  border-color: #cccccc !important;
}

:deep(.el-checkbox__inner:hover) {
  border-color: #cccccc !important;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #5f70f3 !important;
  border-color: #5f70f3 !important;
}

// 移动端复选框样式
.mobile-checkbox {
  :deep(.el-checkbox__inner) {
    width: 14px !important;
    height: 14px !important;
  }

  :deep(.el-checkbox__inner::after) {
    height: 6px !important;
    width: 3px !important;
    left: 4px !important;
    top: 1px !important;
  }
}

// 移动端按钮样式
.mobile-btn {
  font-size: 12px !important;
  padding: 2px 4px !important;
  margin: 0 2px !important;
}

@media (min-width: 1144px) {
  .table_bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 40px;
  }

  :deep(.transparent-table) {
    .el-table__body tr {
      td {
        padding-top: 16px;
        padding-bottom: 16px;
      }

      &+tr {
        margin-top: 10px;
      }
    }
  }
}

:deep(.el-table .el-button--text) {
  color: #7079aa;
}

:deep(.el-table .el-button--text:hover) {
  color: #5a64b5;
}

.el-button--success {
  background: #5bc98c;
  color: white;
}

:deep(.el-table .cell) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.page-size-select {
  width: 100px;
  margin-right: 10px;

  :deep(.el-input__inner) {
    height: 32px;
    line-height: 32px;
    border-radius: 4px;
    border: 1px solid #e4e7ed;
    background: #dee7ff;
    color: #606266;
    font-size: 14px;
  }

  :deep(.el-input__suffix) {
    right: 6px;
    width: 15px;
    height: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
    top: 6px;
    border-radius: 4px;
  }

  :deep(.el-input__suffix-inner) {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
  }

  :deep(.el-icon-arrow-up:before) {
    content: "";
    display: inline-block;
    border-left: 6px solid transparent;
    border-right: 6px solid transparent;
    border-top: 9px solid #606266;
    position: relative;
    transform: rotate(0deg);
    transition: transform 0.3s;
  }
}

:deep(.el-table) {
  .el-table__body-wrapper {
    transition: height 0.3s ease;
  }
}

.el-table {
  --table-max-height: calc(100vh - 40vh);
  max-height: var(--table-max-height);

  .el-table__body-wrapper {
    max-height: calc(var(--table-max-height) - 40px);
  }
}

:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.6) !important;
  backdrop-filter: blur(2px);
}

:deep(.el-loading-spinner .circular) {
  width: 28px;
  height: 28px;
}

:deep(.el-loading-spinner .path) {
  stroke: #6b8cff;
}

:deep(.el-loading-text) {
  color: #6b8cff !important;
  font-size: 14px;
  margin-top: 8px;
}
</style>
