<template>
  <div class="welcome">
    <HeaderBar />

    <div :class="['operation-bar', { 'mobile-operation-bar': isMobile }]">
      <h2 class="page-title">字段管理</h2>
      <div class="right-operations" v-if="!isMobile">
        <el-dropdown trigger="click" @command="handleSelectModelType" @visible-change="handleDropdownVisibleChange">
          <el-button class="category-btn">
            类别筛选 {{ selectedModelTypeLabel }}<i class="el-icon-arrow-down el-icon--right"
              :class="{ 'rotate-down': DropdownVisible }"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="">全部</el-dropdown-item>
            <el-dropdown-item v-for="item in modelTypes" :key="item.value" :command="item.value">
              {{ item.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-input placeholder="请输入供应器名称查询" v-model="searchName" class="search-input" @keyup.enter.native="handleSearch"
          clearable />
        <el-button class="btn-search" @click="handleSearch">搜索</el-button>
      </div>
    </div>

    <!-- 移动端搜索和筛选区域 -->
    <div v-if="isMobile" class="mobile-filter-container">
      <div class="mobile-search-row">
        <el-input placeholder="请输入供应器名称查询" v-model="searchName" class="mobile-search-input" clearable
          @keyup.enter.native="handleSearch" size="small">
          <template slot="suffix">
            <el-button class="search-btn" @click="handleSearch" type="text" size="mini">
              <i class="el-icon-search"></i>
            </el-button>
          </template>
        </el-input>
      </div>
      <div class="mobile-filter-row">
        <el-dropdown trigger="click" @command="handleSelectModelType" @visible-change="handleDropdownVisibleChange" class="mobile-dropdown">
          <el-button class="mobile-category-btn" size="small">
            类别筛选 {{ selectedModelTypeLabel }}<i class="el-icon-arrow-down el-icon--right"
              :class="{ 'rotate-down': DropdownVisible }"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="">全部</el-dropdown-item>
            <el-dropdown-item v-for="item in modelTypes" :key="item.value" :command="item.value">
              {{ item.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <div class="main-wrapper">
      <div class="content-panel">
        <div class="content-area">
          <el-card :class="['provider-card', { 'mobile-provider-card': isMobile }]" shadow="never">
            <div class="table-container" :class="{ 'mobile-table': isMobile }">
              <el-table ref="providersTable" :data="filteredProvidersList" :class="['transparent-table', { 'mobile-data-table': isMobile }]" v-loading="loading"
                element-loading-text="拼命加载中" element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(255, 255, 255, 0.7)" :header-cell-class-name="headerCellClassName">
                <el-table-column label="选择" align="center" :width="isMobile ? 50 : 120">
                  <template slot-scope="scope">
                    <el-checkbox v-model="scope.row.selected" :class="{ 'mobile-checkbox': isMobile }"></el-checkbox>
                  </template>
                </el-table-column>

                <el-table-column label="类别" prop="modelType" align="center" :width="isMobile ? 80 : 200" :show-overflow-tooltip="isMobile">
                                     <template slot="header" v-if="!isMobile">
                    <el-dropdown trigger="click" @command="handleSelectModelType"
                      @visible-change="isDropdownOpen = $event">
                      <span class="dropdown-trigger" :class="{ 'active': isDropdownOpen }">
                        类别{{ selectedModelTypeLabel }} <i class="dropdown-arrow"
                          :class="{ 'is-active': isDropdownOpen }"></i>
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="">全部</el-dropdown-item>
                        <el-dropdown-item v-for="item in modelTypes" :key="item.value" :command="item.value">
                          {{ item.label }}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </template>
                  <template slot-scope="scope">
                    <el-tag :type="getModelTypeTag(scope.row.modelType)" :size="isMobile ? 'mini' : 'small'">
                      {{ getModelTypeLabel(scope.row.modelType) }}
                    </el-tag>
                  </template>
                </el-table-column>

                <el-table-column label="供应器编码" prop="providerCode" align="center" :width="isMobile ? 100 : 150" :show-overflow-tooltip="isMobile"></el-table-column>
                <el-table-column label="名称" prop="name" align="center" :width="isMobile ? 100 : undefined" :show-overflow-tooltip="isMobile"></el-table-column>
                <el-table-column v-if="!isMobile" label="字段配置" align="center">
                  <template slot-scope="scope">
                    <el-popover placement="top-start" width="400" trigger="hover">
                      <div v-for="field in scope.row.fields" :key="field.key" class="field-item">
                        <span class="field-label">{{ field.label }}:</span>
                        <span class="field-type">{{ field.type }}</span>
                        <span v-if="isSensitiveField(field.key)" class="sensitive-tag">敏感</span>
                      </div>
                      <el-button slot="reference" size="mini" type="text">查看字段</el-button>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column label="排序" prop="sort" align="center" :width="isMobile ? 60 : 80"></el-table-column>
                <el-table-column label="操作" align="center" :width="isMobile ? 100 : 180">
                  <template slot-scope="scope">
                    <el-button :size="isMobile ? 'mini' : 'mini'" type="text" @click="editProvider(scope.row)" :class="{ 'mobile-btn': isMobile }">编辑</el-button>
                    <el-button :size="isMobile ? 'mini' : 'mini'" type="text" @click="deleteProvider(scope.row)" :class="{ 'mobile-btn': isMobile }">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <div :class="['table_bottom', { 'mobile-footer': isMobile }]">
              <div :class="['ctrl_btn', { 'mobile-actions': isMobile }]">
                <el-button :size="isMobile ? 'mini' : 'mini'" type="primary" class="select-all-btn" @click="handleSelectAll">
                  {{ isAllSelected ? '取消全选' : '全选' }}
                </el-button>
                <el-button :size="isMobile ? 'mini' : 'mini'" type="success" @click="showAddDialog">新增</el-button>
                <el-button :size="isMobile ? 'mini' : 'mini'" type="danger" icon="el-icon-delete" @click="deleteSelectedProviders">删除
                </el-button>
              </div>
              <div :class="['custom-pagination', { 'mobile-pagination': isMobile }]">
                <el-select v-model="pageSize" @change="handlePageSizeChange" class="page-size-select">
                  <el-option v-for="item in pageSizeOptions" :key="item" :label="`${item}条/页`" :value="item">
                  </el-option>
                </el-select>
                <button class="pagination-btn" :disabled="currentPage === 1" @click="goFirst">
                  首页
                </button>
                <button class="pagination-btn" :disabled="currentPage === 1" @click="goPrev">
                  上一页
                </button>
                <button v-for="page in visiblePages" :key="page" class="pagination-btn"
                  :class="{ active: page === currentPage }" @click="goToPage(page)">
                  {{ page }}
                </button>
                <button class="pagination-btn" :disabled="currentPage === pageCount" @click="goNext">
                  下一页
                </button>
                <span :class="['total-text', { 'mobile-total': isMobile }]">共{{ total }}条记录</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 新增/编辑供应器对话框 -->
    <provider-dialog :title="dialogTitle" :visible.sync="dialogVisible" :form="providerForm" :model-types="modelTypes"
      @submit="handleSubmit" @cancel="dialogVisible = false" />

    <el-footer>
      <version-footer />
    </el-footer>
  </div>
</template>

<script>
import Api from "@/apis/api";
import HeaderBar from "@/components/HeaderBar.vue";
import ProviderDialog from "@/components/ProviderDialog.vue";
import VersionFooter from "@/components/VersionFooter.vue";
import { isMobileDevice } from "@/utils/index";

export default {
  components: { HeaderBar, ProviderDialog, VersionFooter },
  data() {
    return {
      searchName: "",
      searchModelType: "",
      providersList: [],
      modelTypes: [
        { value: "ASR", label: "语音识别" },
        { value: "TTS", label: "语音合成" },
        { value: "LLM", label: "大语言模型" },
        { value: "VLLM", label: "视觉大语言模型" },
        { value: "Intent", label: "意图识别" },
        { value: "Memory", label: "记忆模块" },
        { value: "VAD", label: "语音活动检测" },
        { value: "Plugin", label: "插件工具" }
      ],
      currentPage: 1,
      loading: false,
      pageSize: 10,
      pageSizeOptions: [10, 20, 50, 100],
      total: 0,
      dialogVisible: false,
      dialogTitle: "新增供应器",
      isAllSelected: false,
      isDropdownOpen: false,
      sensitive_keys: ["api_key", "personal_access_token", "access_token", "token", "secret", "access_key_secret", "secret_key"],
      providerForm: {
        id: null,
        modelType: "",
        providerCode: "",
        name: "",
        fields: [],
        sort: 0
      },
      DropdownVisible: false,
    };
  },
  computed: {
    isMobile() {
      return isMobileDevice();
    },
    selectedModelTypeLabel() {
      if (!this.searchModelType) return "（全部）";
      const selectedType = this.modelTypes.find(item => item.value === this.searchModelType);
      return selectedType ? `（${selectedType.label}）` : "";
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
    filteredProvidersList() {
      return this.providersList;

      // let list = this.providersList.filter(item => {
      //   const nameMatch = item.name.toLowerCase().includes(this.searchName.toLowerCase());
      //   const typeMatch = !this.searchModelType || item.model_type === this.searchModelType;
      //   return nameMatch && typeMatch;
      // });

      // list.sort((a, b) => a.sort - b.sort);

      // // 分页处理
      // const start = (this.currentPage - 1) * this.pageSize;
      // return list.slice(start, start + this.pageSize);
    }
  },
  created() {
    this.fetchProviders();
  },
  methods: {
    fetchProviders() {
      this.loading = true;

      Api.model.getModelProvidersPage(
        {
          page: this.currentPage,
          limit: this.pageSize,
          name: this.searchName,
          modelType: this.searchModelType
        },
        ({ data }) => {
          this.loading = false;
          if (data.code === 0) {
            this.providersList = data.data.list.map(item => {
              return {
                ...item,
                selected: false,
                fields: JSON.parse(item.fields)
              };
            });
            this.total = data.data.total;
          } else {
            this.$message.error({
              message: data.msg || '获取参数列表失败'
            });
          }
        }
      );
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchProviders();
    },
    handleSelectModelType(value) {
      this.isDropdownOpen = false;
      this.searchModelType = value;
      this.handleSearch();
    },
    handleSelectAll() {
      this.isAllSelected = !this.isAllSelected;
      this.providersList.forEach(row => {
        row.selected = this.isAllSelected;
      });
    },
    showAddDialog() {
      this.dialogTitle = "新增供应器";
      this.providerForm = {
        id: null,
        modelType: "",
        providerCode: "",
        name: "",
        fields: [],
        sort: 0
      };
      this.dialogVisible = true;
    },
    editProvider(row) {
      this.dialogTitle = "编辑供应器";
      this.providerForm = {
        ...row,
        fields: JSON.parse(JSON.stringify(row.fields))
      };
      this.dialogVisible = true;
    },
    handleSubmit({ form, done }) {
      this.loading = true;
      if (form.id) {
        // 编辑
        Api.model.updateModelProvider(form, ({ data }) => {

          if (data.code === 0) {
            this.fetchProviders(); // 刷新表格
            this.$message.success({
              message: "修改成功",
              showClose: true
            });
          }
        });
      } else {
        // 新增
        Api.model.addModelProvider(form, ({ data }) => {
          if (data.code === 0) {
            this.fetchProviders(); // 刷新表格
            this.$message.success({
              message: "新增成功",
              showClose: true
            });
            this.total += 1;
          }
        });
      }
      this.loading = false;
      this.dialogVisible = false;
      done && done();
    },
    deleteSelectedProviders() {
      const selectedRows = this.providersList.filter(row => row.selected);
      if (selectedRows.length === 0) {
        this.$message.warning({
          message: "请先选择需要删除的供应器",
          showClose: true
        });
        return;
      }
      this.deleteProvider(selectedRows);
    },
    deleteProvider(row) {
      const providers = Array.isArray(row) ? row : [row];
      const providerCount = providers.length;

      this.$confirm(`确定要删除选中的${providerCount}个供应器吗？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        const ids = providers.map(provider => provider.id);
        Api.model.deleteModelProviderByIds(ids, ({ data }) => {
          if (data.code === 0) {

            this.isAllSelected = false;
            this.fetchProviders(); // 刷新表格

            this.$message.success({
              message: `成功删除${providerCount}个参数`,
              showClose: true
            });
          } else {
            this.$message.error({
              message: data.msg || '删除失败，请重试',
              showClose: true
            });
          }
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除',
          showClose: true,
          duration: 1000
        });
      });
    },
    getModelTypeTag(type) {
      const typeMap = {
        'ASR': 'success',
        'TTS': 'warning',
        'LLM': 'danger',
        'Intent': 'info',
        'Memory': '',
        'VAD': 'primary'
      };
      return typeMap[type] || '';
    },
    getModelTypeLabel(type) {
      const typeItem = this.modelTypes.find(item => item.value === type);
      return typeItem ? typeItem.label : type;
    },
    isSensitiveField(fieldKey) {
      if (typeof fieldKey !== 'string') return false;
      return this.sensitive_keys.some(key =>
        fieldKey.toLowerCase().includes(key.toLowerCase())
      );
    },
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.fetchProviders();
    },
    headerCellClassName({ columnIndex }) {
      if (columnIndex === 0) {
        return "custom-selection-header";
      }
      return "";
    },
    goFirst() {
      this.currentPage = 1;
      this.fetchProviders();
    },
    goPrev() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.fetchProviders();
      }
    },
    goNext() {
      if (this.currentPage < this.pageCount) {
        console.log("this.currentPage", this.currentPage);
        this.currentPage++;
        this.fetchProviders();
      }
    },
    goToPage(page) {
      this.currentPage = page;
      this.fetchProviders();
    },
    handleDropdownVisibleChange(visible) {
      this.DropdownVisible = visible;
    },
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
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

// 移动端筛选容器
.mobile-filter-container {
  padding: 0 16px 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;

  .mobile-search-row {
    width: 100%;

    .mobile-search-input {
      width: 100%;

      :deep(.el-input__inner) {
        height: 32px;
        border-radius: 16px;
        background-color: rgba(255, 255, 255, 0.9);
        border: 1px solid #e4e7ed;
        font-size: 14px;
        padding-right: 35px;
      }

      :deep(.el-input__suffix) {
        right: 8px;
        top: 50%;
        transform: translateY(-50%);
      }

      .search-btn {
        background: transparent !important;
        border: none !important;
        color: #909399 !important;
        padding: 0 !important;
        width: 24px !important;
        height: 24px !important;
        display: flex !important;
        align-items: center !important;
        justify-content: center !important;
        margin: 0 !important;
        box-shadow: none !important;

        &:hover {
          color: #5f70f3 !important;
          background: transparent !important;
          transform: none !important;
          box-shadow: none !important;
        }

        &:focus {
          background: transparent !important;
          box-shadow: none !important;
        }

        i {
          font-size: 14px;
        }
      }
    }
  }

  .mobile-filter-row {
    display: flex;
    justify-content: flex-start;

    .mobile-dropdown {
      width: auto;
    }

    .mobile-category-btn {
      background-color: rgba(255, 255, 255, 0.9);
      border: 1px solid #e4e7ed;
      border-radius: 16px;
      height: 32px;
      padding: 0 12px;
      font-size: 14px;
      color: #606266;

      &:hover {
        background-color: rgba(255, 255, 255, 1);
        border-color: #5f70f3;
        color: #5f70f3;
      }

      .el-icon-arrow-down {
        margin-left: 5px;
      }
    }
  }
}

.page-title {
  font-size: 24px;
  margin: 0;
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
}

.el-card {
  border: none;
}

.provider-card {
  background: white;
  flex: 1;
  display: flex;
  flex-direction: column;
  box-shadow: none;
  overflow: hidden;

  ::v-deep .el-card__body {
    padding: 15px;
    display: flex;
    flex-direction: column;
    flex: 1;
    overflow: hidden;
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

.custom-pagination {
  display: flex;
  align-items: center;
  gap: 10px;

  .el-select {
    margin-right: 8px;
  }

  .pagination-btn:first-child,
  .pagination-btn:nth-child(2),
  .pagination-btn:nth-last-child(2),
  .pagination-btn:nth-child(3) {
    min-width: 60px;
    height: 32px;
    padding: 0 12px;
    border-radius: 4px;
    border: 1px solid #e4e7ed;
    background: #dee7ff;
    color: #606266;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: #d7dce6;
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

  .pagination-btn:not(:first-child):not(:nth-child(3)):not(:nth-child(2)):not(:nth-last-child(2)) {
    min-width: 28px;
    height: 32px;
    padding: 0;
    border-radius: 4px;
    border: 1px solid transparent;
    background: transparent;
    color: #606266;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(245, 247, 250, 0.3);
    }
  }

  .pagination-btn.active {
    background: #5f70f3 !important;
    color: #ffffff !important;
    border-color: #5f70f3 !important;

    &:hover {
      background: #6d7cf5 !important;
    }
  }

  .total-text {
    color: #909399;
    font-size: 14px;
    margin-left: 10px;
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

.field-item {
  padding: 5px 0;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;

  .field-label {
    flex: 1;
    font-weight: bold;
  }

  .field-type {
    width: 80px;
    color: #666;
  }

  .sensitive-tag {
    margin-left: 10px;
    color: #f56c6c;
    font-size: 12px;
  }
}

.dropdown-trigger {
  font-size: 14px;
  color: #303133;
  cursor: pointer;
  display: flex;
  align-items: center;

  &:hover {
    color: #409EFF;
  }
}

.dropdown-trigger.active {
  color: #409EFF;
}

.dropdown-arrow {
  display: inline-block;
  margin-left: 5px;
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 7px solid black;
  position: relative;
  transition: transform 0.3s ease;
  transform: rotate(0deg);

  &.is-active {
    transform: rotate(180deg);
    border-top-color: #409EFF;
  }
}

.rotate-down {
  transform: rotate(180deg);
  transition: transform 0.3s ease;
}

.el-icon-arrow-down {
  transition: transform 0.3s ease;
}

.dropdown-trigger {
  font-size: 14px;
  color: #303133;
  cursor: pointer;
  display: flex;
  align-items: center;

  &:hover {
    color: #409EFF;
  }
}

.dropdown-trigger.active {
  color: #409EFF;
}
</style>