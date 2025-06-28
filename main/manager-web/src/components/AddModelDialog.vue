<template>
  <el-dialog :visible="dialogVisible" @update:visible="handleVisibleChange" :width="isMobile ? '95%' : '57%'" center
    custom-class="custom-dialog" :show-close="false" class="center-dialog">
    <div :class="['dialog-content', { 'mobile-content': isMobile }]">
      <div :class="['dialog-title', { 'mobile-title': isMobile }]">
        添加模型
      </div>

      <button class="custom-close-btn" @click="handleClose">
        ×
      </button>

      <!-- 模型信息部分 -->
      <div :class="['section-header', { 'mobile-section-header': isMobile }]">
        <div :class="['section-title', { 'mobile-section-title': isMobile }]">模型信息</div>
        <div :class="['switch-group', { 'mobile-switch-group': isMobile }]">
          <div class="switch-item">
            <span :class="['switch-label', { 'mobile-switch-label': isMobile }]">是否启用</span>
            <el-switch v-model="formData.isEnabled" class="custom-switch"></el-switch>
          </div>
          <div style="display: none;" class="switch-item">
            <span :class="['switch-label', { 'mobile-switch-label': isMobile }]">设为默认</span>
            <el-switch v-model="formData.isDefault" class="custom-switch"></el-switch>
          </div>
        </div>
      </div>

      <div class="section-divider"></div>
      <el-form :model="formData" :label-width="isMobile ? '80px' : '100px'" label-position="left" :class="['custom-form', { 'mobile-form': isMobile }]">
        <div :class="['form-row', { 'mobile-form-row': isMobile }]">
          <el-form-item label="模型名称" prop="modelName" :class="['form-item', { 'mobile-form-item': isMobile }]">
            <el-input v-model="formData.modelName" placeholder="请输入模型名称" :class="['custom-input-bg', { 'mobile-input': isMobile }]"></el-input>
          </el-form-item>
          <el-form-item label="模型编码" prop="modelCode" :class="['form-item', { 'mobile-form-item': isMobile }]">
            <el-input v-model="formData.modelCode" placeholder="请输入模型编码" :class="['custom-input-bg', { 'mobile-input': isMobile }]"></el-input>
          </el-form-item>
        </div>

        <div :class="['form-row', { 'mobile-form-row': isMobile }]">
          <el-form-item label="供应器" prop="supplier" :class="['form-item', { 'mobile-form-item': isMobile }]">
            <el-select v-model="formData.supplier" placeholder="请选择" class="custom-select custom-input-bg"
              style="width: 100%;" @focus="loadProviders" filterable>
              <el-option v-for="item in providers" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="排序号" prop="sortOrder" :class="['form-item', { 'mobile-form-item': isMobile }]">
            <el-input v-model="formData.sort" type="number" placeholder="请输入排序号" :class="['custom-input-bg', { 'mobile-input': isMobile }]"></el-input>
          </el-form-item>
        </div>

        <el-form-item label="文档地址" prop="docLink" :class="['form-item-full', { 'mobile-form-item': isMobile }]">
          <el-input v-model="formData.docLink" placeholder="请输入文档地址" :class="['custom-input-bg', { 'mobile-input': isMobile }]"></el-input>
        </el-form-item>

        <el-form-item label="备注" prop="remark" :class="['form-item-full prop-remark', { 'mobile-form-item': isMobile }]">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入模型备注" :autosize="{ minRows: 3, maxRows: 5 }"
            :class="['custom-input-bg', { 'mobile-textarea': isMobile }]"></el-input>
        </el-form-item>
      </el-form>

      <div :class="['section-title', { 'mobile-section-title': isMobile }]" style="margin-bottom: 15px;">调用信息</div>
      <div class="section-divider"></div>

      <el-form :model="formData.configJson" :label-width="isMobile ? '80px' : 'auto'" label-position="left" :class="['custom-form', { 'mobile-form': isMobile }]">
        <template v-for="(row, rowIndex) in chunkedCallInfoFields">
          <div :key="rowIndex" :class="['form-row', { 'mobile-form-row': isMobile }]">
            <el-form-item v-for="field in row" :key="field.prop" :label="field.label" :prop="field.prop"
              :class="['form-item', { 'mobile-form-item': isMobile }]">
              <el-input v-model="formData.configJson[field.prop]" :placeholder="field.placeholder"
                :type="field.type || 'text'" :class="['custom-input-bg', { 'mobile-input': isMobile }]" :show-password="field.type === 'password'">
              </el-input>
            </el-form-item>
          </div>
        </template>
      </el-form>
    </div>

    <div :class="['dialog-footer', { 'mobile-footer': isMobile }]">
      <el-button
        type="primary"
        @click="confirm"
        :class="['save-btn', { 'mobile-save-btn': isMobile }]"
        :loading="saving"
        :disabled="saving">
        保存
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/apis/api';
import { isMobileDevice } from '@/utils/index';

export default {
  name: 'AddModelDialog',
  props: {
    visible: { type: Boolean, required: true },
    modelType: { type: String, required: true }
  },
  data() {
    return {
      saving: false,
      providers: [],
      dialogVisible: false,
      providersLoaded: false,
      providerFields: [],
      currentProvider: null,
      formData: {
        modelName: '',
        modelCode: '',
        supplier: '',
        sort: 1,
        docLink: '',
        remark: '',
        isEnabled: true,
        isDefault: true,
        configJson: {}
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
      if (val) {
        this.initConfigJson();
      } else {
        this.resetForm();
      }
    },
    'formData.supplier'(newVal) {
      this.currentProvider = this.providers.find(p => p.value === newVal);
      this.providerFields = this.currentProvider?.fields || [];
      this.initDynamicConfig();
    }
  },
  computed: {
    isMobile() {
      return isMobileDevice();
    },
    dynamicCallInfoFields() {
      return this.providerFields;
    },
    chunkedCallInfoFields() {
      // 移动端使用单列布局
      if (this.isMobile) {
        return this.dynamicCallInfoFields.map(field => [field]);
      }

      // PC端使用双列布局
      const chunkSize = 2;
      const result = [];
      for (let i = 0; i < this.dynamicCallInfoFields.length; i += chunkSize) {
        result.push(this.dynamicCallInfoFields.slice(i, i + chunkSize));
      }
      return result;
    }
  },
  methods: {
    loadProviders() {
      if (this.providersLoaded)
        return

      Api.model.getModelProviders(this.modelType, (data) => {
        this.providers = data.map(item => ({
          label: item.name,
          value: item.providerCode,
          fields: JSON.parse(item.fields || '[]').map(f => ({
            label: f.label,
            prop: f.key,
            type: f.type === 'password' ? 'password' : 'text',
            placeholder: `请输入${f.label}`
          }))
        }))
        this.providersLoaded = true
      })
    },
    initConfigJson() {
      const defaultConfig = {};
      this.providerFields.forEach(field => {
        defaultConfig[field.prop] = '';
      });
      this.formData.configJson = { ...defaultConfig };
    },
    handleVisibleChange(val) {
      this.dialogVisible = val;
      this.$emit('update:visible', val);
      if (!val) {
        this.resetForm();
      }
    },

    handleClose() {
      this.saving = false;
      this.$emit('update:visible', false);
    },
    initDynamicConfig() {
      const newConfig = {};
      this.providerFields.forEach(field => {
        newConfig[field.prop] = this.formData.configJson[field.prop] || '';
      });
      this.formData.configJson = newConfig;
    },
    confirm() {
      this.saving = true;

      if (!this.formData.supplier) {
        this.$message.error('请选择供应器');
        this.saving = false;
        return;
      }

      const submitData = {
        modelName: this.formData.modelName || '',
        modelCode: this.formData.modelCode || '',
        supplier: this.formData.supplier,
        sort: this.formData.sort || 1,
        docLink: this.formData.docLink || '',
        remark: this.formData.remark || '',
        isEnabled: this.formData.isEnabled ? 1 : 0,
        isDefault: this.formData.isDefault ? 1 : 0,
        provideCode: this.formData.supplier,
        configJson: {
          ...this.formData.configJson,
          type: this.formData.supplier
        }
      };

      try {
        this.$emit('confirm', submitData);
        this.$emit('update:visible', false);
        this.resetForm();
      } catch (e) {
        console.error(e);
      } finally {
        this.saving = false;
      }
    },
    resetForm() {
      this.saving = false;
      this.formData = {
        modelName: '',
        modelCode: '',
        supplier: '',
        sort: 1,
        docLink: '',
        remark: '',
        isEnabled: true,
        isDefault: true,
        configJson: {}
      };
      // 重置加载状态
      this.providers = [];
      this.providersLoaded = false;
      // 重置字段配置
      this.providerFields = [];
      this.currentProvider = null;
    },
  }
}
</script>

<style lang="scss" scoped>
.custom-dialog {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  background: white;
  padding-bottom: 17px;
}

.custom-dialog .el-dialog__header {
  padding: 0;
  border-bottom: none;
}

.center-dialog {
  display: flex;
  align-items: center;
  justify-content: center;
}

.center-dialog .el-dialog {
  margin: 0 0 auto !important;
  display: flex;
  flex-direction: column;
}

// 弹窗内容容器
.dialog-content {
  margin: 0 18px;
  text-align: left;
  padding: 10px;
  border-radius: 10px;

  &.mobile-content {
    margin: 0 12px;
    padding: 8px;
  }
}

// 弹窗标题
.dialog-title {
  font-size: 30px;
  color: #3d4566;
  margin-top: -10px;
  margin-bottom: 10px;
  text-align: center;

  &.mobile-title {
    font-size: 24px;
    margin-bottom: 15px;
  }
}

// 区块头部
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;

  &.mobile-section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #3d4566;

  &.mobile-section-title {
    font-size: 18px;
  }
}

.switch-group {
  display: flex;
  align-items: center;
  gap: 20px;

  &.mobile-switch-group {
    gap: 15px;
  }
}

.switch-item {
  display: flex;
  align-items: center;
}

.switch-label {
  margin-right: 8px;

  &.mobile-switch-label {
    font-size: 14px;
  }
}

// 分割线
.section-divider {
  height: 2px;
  background: #e9e9e9;
  margin-bottom: 22px;
}

// 表单行
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 0;

  &.mobile-form-row {
    flex-direction: column;
    gap: 0;
  }
}

.form-item {
  flex: 1;

  &.mobile-form-item {
    flex: none;
    width: 100%;
  }
}

.form-item-full {
  margin-bottom: 27px;

  &.mobile-form-item {
    margin-bottom: 20px;
  }
}

// 底部按钮区域
.dialog-footer {
  display: flex;
  justify-content: center;

  &.mobile-footer {
    padding: 10px 0;
  }
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

.custom-select .el-input__suffix {
  background: #e6e8ea;
  right: 6px;
  width: 20px;
  height: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  top: 9px;
}

.custom-select .el-input__suffix-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.custom-select .el-icon-arrow-up:before {
  content: "";
  display: inline-block;
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 7px solid #c0c4cc;
  position: relative;
  top: -2px;
  transform: rotate(180deg);
}

.custom-form .el-form-item {
  margin-bottom: 20px;

  &.mobile-form-item {
    margin-bottom: 16px;
  }
}

.custom-form .el-form-item__label {
  color: #3d4566;
  font-weight: normal;
  text-align: right;
  padding-right: 20px;
}

.custom-form.mobile-form .el-form-item__label {
  text-align: left;
  padding-right: 10px;
  font-size: 14px;
}

.custom-form .el-form-item.prop-remark .el-form-item__label {
  margin-top: -4px;
}

.custom-input-bg .el-input__inner::-webkit-input-placeholder,
.custom-input-bg .el-textarea__inner::-webkit-input-placeholder {
  color: #9c9f9e;
}

.custom-input-bg .el-input__inner,
.custom-input-bg .el-textarea__inner {
  background-color: #f6f8fc;
}

.custom-input-bg .el-input__inner {
  height: 32px;
}

// 移动端输入框样式
.mobile-input .el-input__inner {
  height: 40px;
  font-size: 14px;
}

.mobile-textarea .el-textarea__inner {
  font-size: 14px;
}

.save-btn {
  background: #e6f0fd;
  color: #237ff4;
  border: 1px solid #b3d1ff;
  width: 150px;
  height: 40px;
  font-size: 16px;
  transition: all 0.3s ease;

  &.mobile-save-btn {
    width: 120px;
    height: 36px;
    font-size: 14px;
  }
}

.save-btn:hover {
  background: linear-gradient(to right, #237ff4, #9c40d5);
  color: white;
  border: none;
}

.custom-switch .el-switch__core {
  border-radius: 20px;
  height: 23px;
  background-color: #c0ccda;
  width: 35px;
  padding: 0 20px;
}

.custom-switch .el-switch__core:after {
  width: 15px;
  height: 15px;
  background-color: white;
  top: 3px;
  left: 4px;
  transition: all .3s;
}

.custom-switch.is-checked .el-switch__core {
  border-color: #b5bcf0;
  background-color: #cfd7fa;
  padding: 0 20px;
}

.custom-switch.is-checked .el-switch__core:after {
  left: 100%;
  margin-left: -18px;
  background-color: #1b47ee;
}

[style*="display: flex"] {
  gap: 20px;
}

// 移动端媒体查询
@media screen and (max-width: 768px) {
  .custom-dialog {
    border-radius: 15px;
  }

  .dialog-content {
    max-height: 85vh;
    overflow-y: auto;
  }
}
</style>