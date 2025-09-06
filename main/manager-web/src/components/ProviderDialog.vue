<template>
  <el-dialog :visible="visible" :close-on-click-modal="false" @update:visible="handleVisibleChange" :width="isMobile ? '95%' : '57%'" center custom-class="custom-dialog"
    :show-close="false" class="center-dialog" :modal="false">

    <div style="margin: 0 18px; text-align: left; padding: 10px; border-radius: 10px;">
      <div style="font-size: 30px; color: #3d4566; margin-top: -15px; margin-bottom: 20px; text-align: center;">
        {{ title }}
      </div>

      <button class="custom-close-btn" @click="handleClose">×</button>

      <el-form :model="form" :label-width="isMobile ? '80px' : '100px'" :rules="rules" ref="form" :class="['custom-form', { 'mobile-form': isMobile }]">
        <div :class="['form-row', { 'mobile-form-row': isMobile }]">
          <el-form-item label="类别" prop="modelType" :class="['form-item', { 'mobile-form-item': isMobile }]">
            <el-select v-model="form.modelType" placeholder="请选择类别" :class="['custom-input-bg', { 'mobile-select': isMobile }]" style="width: 100%;">
              <el-option v-for="item in modelTypes" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="编码" prop="providerCode" :class="['form-item', { 'mobile-form-item': isMobile }]">
            <el-input v-model="form.providerCode" placeholder="请输入供应器编码" :class="['custom-input-bg', { 'mobile-input': isMobile }]"></el-input>
          </el-form-item>
        </div>

        <div :class="['form-row', { 'mobile-form-row': isMobile }]">
          <el-form-item label="名称" prop="name" :class="['form-item', { 'mobile-form-item': isMobile }]">
            <el-input v-model="form.name" placeholder="请输入供应器名称" :class="['custom-input-bg', { 'mobile-input': isMobile }]"></el-input>
          </el-form-item>
          <el-form-item label="排序" prop="sort" :class="['form-item', { 'mobile-form-item': isMobile }]">
            <el-input-number v-model="form.sort" :min="0" controls-position="right" :class="['custom-input-bg', { 'mobile-input-number': isMobile }]"
              style="width: 100%;"></el-input-number>
          </el-form-item>
        </div>

        <div :class="['fields-header', { 'mobile-fields-header': isMobile }]">
          字段配置
          <div style="display: inline-block; float: right;">
            <el-button type="primary" @click="addField" :size="isMobile ? 'mini' : 'small'" style="background: #5bc98c; border: none;"
              :disabled="hasIncompleteFields">
              添加
            </el-button>
            <el-button type="primary" @click="toggleSelectAllFields" :size="isMobile ? 'mini' : 'small'"
              style="background: #5f70f3; border: none; margin-left: 10px;">
              {{ isAllFieldsSelected ? '取消全选' : '全选' }}
            </el-button>
            <el-button type="danger" @click="batchRemoveFields" :size="isMobile ? 'mini' : 'small'"
              style="background: red; border: none; margin-left: 10px;">
              批量删除
            </el-button>
          </div>
        </div>
        <div style="height: 2px; background: #e9e9e9; margin-bottom: 22px;"></div>

        <div :class="['fields-container', { 'mobile-fields-container': isMobile }]">
          <el-table :data="form.fields" style="width: 100%;" border :size="isMobile ? 'mini' : 'medium'" :key="tableKey">
            <el-table-column label="选择" align="center" :width="isMobile ? 40 : 50">
              <template slot-scope="scope">
                <el-checkbox v-model="scope.row.selected" @change="handleFieldSelectChange" :class="{ 'mobile-checkbox': isMobile }"></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column label="字段key" :width="isMobile ? 80 : undefined">
              <template slot-scope="scope">
                <template v-if="scope.row.editing">
                  <el-input v-model="scope.row.key" placeholder="字段key" :size="isMobile ? 'mini' : 'small'"></el-input>
                </template>
                <template v-else>
                  {{ scope.row.key }}
                </template>
              </template>
            </el-table-column>
            <el-table-column label="字段标签" :width="isMobile ? 80 : undefined">
              <template slot-scope="scope">
                <template v-if="scope.row.editing">
                  <el-input v-model="scope.row.label" placeholder="字段标签" :size="isMobile ? 'mini' : 'small'"></el-input>
                </template>
                <template v-else>
                  {{ scope.row.label }}
                </template>
              </template>
            </el-table-column>
            <el-table-column label="字段类型" :width="isMobile ? 80 : undefined">
              <template slot-scope="scope">
                <template v-if="scope.row.editing">
                  <el-select v-model="scope.row.type" placeholder="类型" :size="isMobile ? 'mini' : 'small'">
                    <el-option label="字符串" value="string"></el-option>
                    <el-option label="数字" value="number"></el-option>
                    <el-option label="布尔值" value="boolean"></el-option>
                    <el-option label="字典" value="dict"></el-option>
                    <el-option label="分号分割的列表" value="array"></el-option>
                  </el-select>
                </template>
                <template v-else>
                  {{ getTypeLabel(scope.row.type) }}
                </template>
              </template>
            </el-table-column>
            <el-table-column label="默认值" :width="isMobile ? 80 : undefined">
              <template slot-scope="scope">
                <template v-if="scope.row.editing">
                  <el-input v-model="scope.row.default" placeholder="请输入默认值" :size="isMobile ? 'mini' : 'small'"></el-input>
                </template>
                <template v-else>
                  {{ scope.row.default }}
                </template>
              </template>
            </el-table-column>
            <el-table-column label="操作" :width="isMobile ? 100 : 150" align="center">
              <template slot-scope="scope">
                <el-button v-if="!scope.row.editing" type="primary" size="mini" @click="startEditing(scope.row)">
                  {{ isMobile ? '编辑' : '编辑' }}
                </el-button>
                <el-button v-else type="success" size="mini" @click="stopEditing(scope.row)">
                  {{ isMobile ? '完成' : '完成' }}
                </el-button>
                <el-button type="danger" size="mini" @click="removeField(scope.$index)">
                  {{ isMobile ? '删除' : '删除' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
    </div>

    <div :class="['save-container', { 'mobile-save-container': isMobile }]">
      <el-button type="primary" @click="submit" :class="['save-btn', { 'mobile-save-btn': isMobile }]" :loading="saving" :size="isMobile ? 'small' : 'medium'">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { isMobileDevice } from "@/utils/index";

export default {
  props: {
    title: String,
    visible: Boolean,
    form: Object,
    modelTypes: Array
  },
  data() {
    return {
      saving: false,
      rules: {
        modelType: [{ required: true, message: '请选择类别', trigger: 'change' }],
        providerCode: [{ required: true, message: '请输入供应器编码', trigger: 'blur' }],
        name: [{ required: true, message: '请输入供应器名称', trigger: 'blur' }]
      },
      isAllFieldsSelected: false,
      tableKey: 0 // 用于强制表格重新渲染
    };
  },
  computed: {
    isMobile() {
      return isMobileDevice();
    },
    hasIncompleteFields() {
      return this.form.fields && this.form.fields.some(field =>
        !field.key || !field.label || !field.type
      );
    }
  },
  methods: {
    getTypeLabel(type) {
      const typeMap = {
        'string': '字符串',
        'number': '数字',
        'boolean': '布尔值',
        'dict': '字典',
        'array': '分号分割的列表'
      };
      return typeMap[type];
    },

    startEditing(row) {
      this.$set(row, 'editing', true);
    },

    stopEditing(row) {
      this.$set(row, 'editing', false);

      const index = this.form.fields.indexOf(row);
      if (index > -1) {
        this.form.fields.splice(index, 1);
        this.form.fields.push(row);
        this.forceTableRerender();
      }
    },

    handleFieldSelectChange() {
      this.isAllFieldsSelected = this.form.fields.length > 0 &&
        this.form.fields.every(field => field.selected);
    },

    toggleSelectAllFields() {
      this.isAllFieldsSelected = !this.isAllFieldsSelected;
      this.form.fields = this.form.fields.map(field => ({
        ...field,
        selected: this.isAllFieldsSelected
      }));
    },

    handleVisibleChange(val) {
      this.$emit('update:visible', val);
      if (!val) {
        this.resetForm();
      }
    },

    handleClose() {
      this.resetForm();
      this.$emit('update:visible', false);
      this.$emit('cancel');
    },

    addField() {
      if (this.hasIncompleteFields) {
        this.$message.warning({
          message: '请先完成当前字段的编辑',
          showClose: true
        });
        return;
      }

      this.form.fields.unshift({
        key: '',
        label: '',
        type: 'string',
        default: '',
        selected: false,
        editing: true
      });
      this.forceTableRerender();
    },

    removeField(index) {
      this.$confirm('确定要删除该字段吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.form.fields = this.form.fields.filter((_, i) => i !== index);
        this.updateSelectAllStatus();
        this.forceTableRerender();
        this.$message.success({
          message: '删除成功',
          showClose: true
        });
      }).catch(() => {
        this.$message.info({
          message: '已取消删除',
          showClose: true
        });
      });
    },

    batchRemoveFields() {
      const selectedFields = this.form.fields.filter(field => field.selected);
      if (selectedFields.length === 0) {
        this.$message.warning({
          message: '请先选择要删除的字段',
          showClose: true
        });
        return;
      }
      this.$confirm(`确定要删除选中的 ${selectedFields.length} 个字段吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.form.fields = this.form.fields.filter(field => !field.selected);
        this.isAllFieldsSelected = false;
        this.forceTableRerender();
        this.$message.success({
          message: `成功删除 ${selectedFields.length} 个字段`,
          showClose: true
        });
      }).catch(() => {
        this.$message.info({
          message: '已取消删除',
          showClose: true
        });
      });
    },

    updateSelectAllStatus() {
      this.isAllFieldsSelected = this.form.fields.length > 0 &&
        this.form.fields.every(field => field.selected);
    },

    forceTableRerender() {
      this.tableKey += 1; // 改变key值强制表格重新渲染
    },

    submit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const editingField = this.form.fields.find(field => field.editing);
          if (editingField) {
            this.$message.warning({
              message: '请先完成当前字段的编辑',
              showClose: true
            });
            return;
          }

          this.form.fields = this.form.fields.map(field => ({
            ...field,
            selected: false
          }));
          this.isAllFieldsSelected = false;

          this.saving = true;
          this.$emit('submit', {
            form: this.form,
            done: () => {
              this.saving = false;
              this.resetForm();
            }
          });
        }
      });
    },

    resetForm() {
      this.$refs.form.resetFields();
      if (this.form.fields) {
        this.form.fields.forEach(field => {
          field.selected = false;
          field.editing = false;
        });
      }
      this.isAllFieldsSelected = false;
      this.forceTableRerender();
    },

  },
  watch: {
    visible(val) {
      if (!val) {
        this.resetForm();
      }
    }
  }
};
</script>

<style lang="scss" scoped>
::v-deep .custom-dialog.el-dialog {
  margin-top: 0 !important;
  border-radius: 20px !important;
}

::v-deep .custom-dialog .el-dialog__header {
  padding: 0;
  border-bottom: none;
}

.custom-close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  border: 2px solid #cfcfcf;
  background: none;
  font-size: 30px;
  font-weight: lighter;
  color: #cfcfcf;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  padding: 0;
  outline: none;
}

.custom-close-btn:hover {
  color: #409EFF;
  border-color: #409EFF;
}

.custom-form .el-form-item {
  margin-bottom: 20px;
}

// 移动端表单样式
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;

  &.mobile-form-row {
    flex-direction: column;
    gap: 10px;
    margin-bottom: 15px;
  }
}

.form-item {
  flex: 1;

  &.mobile-form-item {
    flex: none;
  }
}

// 移动端输入框样式
.mobile-input {
  :deep(.el-input__inner) {
    height: 36px !important;
    font-size: 14px !important;
  }
}

.mobile-select {
  :deep(.el-input__inner) {
    height: 36px !important;
    font-size: 14px !important;
  }
}

.mobile-input-number {
  :deep(.el-input__inner) {
    height: 36px !important;
    font-size: 14px !important;
  }
}

// 移动端字段配置样式
.fields-header {
  font-size: 20px;
  font-weight: bold;
  color: #3d4566;
  margin-bottom: 15px;

  &.mobile-fields-header {
    font-size: 16px;
    margin-bottom: 10px;
  }
}

.fields-container {
  &.mobile-fields-container {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;

    .el-table {
      min-width: 500px;
      font-size: 12px;

      .el-table__header th {
        padding: 6px 0;
        font-size: 12px;
      }

      .el-table__body td {
        padding: 6px 0;
      }

      .cell {
        padding: 0 4px;
        font-size: 12px;
      }
    }
  }
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

// 保存按钮容器
.save-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;

  &.mobile-save-container {
    padding: 15px 0;
  }
}

.save-btn {
  min-width: 120px;

  &.mobile-save-btn {
    min-width: 100px;
    height: 36px;
  }
}

.custom-form .el-form-item__label {
  color: #3d4566;
  font-weight: normal;
  text-align: right;
  padding-right: 20px;
}

.custom-input-bg .el-input__inner {
  background-color: #f6f8fc;
  height: 32px;
}

.custom-input-bg .el-input__inner::-webkit-input-placeholder {
  color: #9c9f9e;
}

.fields-container {
  margin-bottom: 20px;
}

.save-btn {
  background: #e6f0fd;
  color: #237ff4;
  border: 1px solid #b3d1ff;
  width: 150px;
  height: 40px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.save-btn:hover {
  background: linear-gradient(to right, #237ff4, #9c40d5);
  color: white;
  border: none;
}

.el-table {
  border-radius: 4px;
}

.el-table::before {
  display: none;
}

.el-table th,
.el-table td {
  padding: 8px 0;
}

.el-button.is-circle {
  border-radius: 2px;
}
</style>