<template>
    <el-dialog :title="title" :visible.sync="dialogVisible" :width="isMobile ? '95%' : '30%'" @close="handleClose">
        <el-form :model="form" :rules="rules" ref="form" :label-width="isMobile ? '80px' : '100px'">
            <el-form-item label="字典标签" prop="dictLabel">
                <el-input v-model="form.dictLabel" placeholder="请输入字典标签" :style="{ height: isMobile ? '40px' : 'auto' }"></el-input>
            </el-form-item>
            <el-form-item label="字典值" prop="dictValue">
                <el-input v-model="form.dictValue" placeholder="请输入字典值" :style="{ height: isMobile ? '40px' : 'auto' }"></el-input>
            </el-form-item>
            <el-form-item label="排序" prop="sort">
                <el-input-number v-model="form.sort" :min="0" :max="999" style="width: 100%;" :size="isMobile ? 'small' : 'medium'"></el-input-number>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="handleClose" :size="isMobile ? 'small' : 'medium'">取 消</el-button>
            <el-button type="primary" @click="handleSave" :size="isMobile ? 'small' : 'medium'">确 定</el-button>
        </div>
    </el-dialog>
</template>

<script>
import { isMobileDevice } from "@/utils/index";

export default {
    name: 'DictDataDialog',
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        title: {
            type: String,
            default: '新增字典数据'
        },
        dictData: {
            type: Object,
            default: () => ({})
        },
        dictTypeId: {
            type: [Number, String],
            default: null
        }
    },
    data() {
        return {
            dialogVisible: this.visible,
            form: {
                id: null,
                dictTypeId: null,
                dictLabel: '',
                dictValue: '',
                sort: 0
            },
            rules: {
                dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
                dictValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }]
            }
        }
    },
    computed: {
        isMobile() {
            return isMobileDevice();
        }
    },
    watch: {
        dictData: {
            handler(val) {
                if (val) {
                    this.form = { ...val }
                }
            },
            immediate: true
        },
        dictTypeId: {
            handler(val) {
                if (val) {
                    this.form.dictTypeId = val
                }
            },
            immediate: true
        },
        visible(val) {
          this.dialogVisible = val;
        },
        dialogVisible(val) {
          this.$emit('update:visible', val);
        }
    },
    methods: {
        handleClose() {
          this.dialogVisible = false;
          this.resetForm();
        },
        resetForm() {
            this.form = {
                id: null,
                dictTypeId: this.dictTypeId,
                dictLabel: '',
                dictValue: '',
                sort: 0
            }
            this.$refs.form?.resetFields()
        },
        handleSave() {
            this.$refs.form.validate(valid => {
                if (valid) {
                    this.$emit('save', this.form)
                }
            })
        }
    }
}
</script>

<style scoped>
.dialog-footer {
    text-align: right;
}
:deep(.el-dialog) {
    border-radius: 15px;
}

</style>