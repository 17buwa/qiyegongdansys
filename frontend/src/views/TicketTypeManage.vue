<template>
  <div class="page-container">
    <el-card shadow="never" class="mb-16">
      <div class="filter-bar" style="margin-bottom: 0">
        <span style="font-weight: 600">工单类型管理</span>
        <div class="flex-spacer" />
        <el-button type="success" :icon="Plus" @click="openType()">新增类型</el-button>
      </div>
    </el-card>

    <el-row :gutter="16">
      <el-col v-for="t in types" :key="t.id" :span="8" class="mb-16">
        <el-card shadow="hover">
          <template #header>
            <div class="type-header">
              <div>
                <b>{{ t.name }}</b>
                <el-tag size="small" style="margin-left: 6px">{{ t.code }}</el-tag>
              </div>
              <div>
                <el-button link type="primary" size="small" @click="openType(t)">编辑</el-button>
                <el-button link type="danger" size="small" @click="removeType(t)">删除</el-button>
              </div>
            </div>
          </template>
          <p class="text-muted">{{ t.description || '暂无描述' }}</p>

          <el-divider content-position="left">自定义字段（{{ fieldsOf(t).length }}）</el-divider>
          <div v-if="fieldsOf(t).length" class="field-row">
            <div v-for="f in fieldsOf(t)" :key="f.id" class="field-item">
              <span>{{ f.fieldName }}</span>
              <el-tag size="small" type="info">{{ fieldTypeLabel(f.fieldType) }}</el-tag>
              <el-tag v-if="f.required === 1" size="small" type="danger">必填</el-tag>
              <el-button link type="primary" size="small" @click="openField(t, f)">编</el-button>
              <el-button link type="danger" size="small" @click="removeField(f)">删</el-button>
            </div>
          </div>
          <el-button size="small" style="margin-top: 8px" :icon="Plus" @click="openField(t)">添加字段</el-button>
        </el-card>
      </el-col>
    </el-row>

    <!-- 类型弹窗 -->
    <el-dialog v-model="typeDialog" :title="typeForm.id ? '编辑类型' : '新增类型'" width="460px">
      <el-form label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="typeForm.name" />
        </el-form-item>
        <el-form-item label="编码">
          <el-input v-model="typeForm.code" :disabled="!!typeForm.id" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="typeForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="typeForm.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveType">保存</el-button>
      </template>
    </el-dialog>

    <!-- 字段弹窗 -->
    <el-dialog v-model="fieldDialog" :title="fieldForm.id ? '编辑字段' : '添加字段'" width="480px">
      <el-form label-width="90px">
        <el-form-item label="字段名称">
          <el-input v-model="fieldForm.fieldName" />
        </el-form-item>
        <el-form-item label="字段Key">
          <el-input v-model="fieldForm.fieldKey" :disabled="!!fieldForm.id" placeholder="如 brand" />
        </el-form-item>
        <el-form-item label="字段类型">
          <el-select v-model="fieldForm.fieldType" style="width: 100%">
            <el-option v-for="ft in FIELD_TYPES" :key="ft.value" :label="ft.label" :value="ft.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="fieldForm.fieldType === 'select'" label="选项列表">
          <el-input v-model="fieldForm.optionsJson" type="textarea" :rows="3" placeholder='JSON数组，如 ["A","B"]' />
        </el-form-item>
        <el-form-item label="是否必填">
          <el-switch v-model="fieldForm.required" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="fieldForm.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="fieldDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingField" @click="saveField">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { listTypes, createType, updateType, deleteType, addField, updateField, deleteField } from '@/api/ticketType'
import { FIELD_TYPES } from '@/utils/constants'

const types = ref([])
const saving = ref(false)
const savingField = ref(false)
const typeDialog = ref(false)
const fieldDialog = ref(false)
const fieldTargetType = ref(null)

const typeForm = ref({ id: null, name: '', code: '', description: '', status: 1 })
const fieldForm = ref({ id: null, fieldName: '', fieldKey: '', fieldType: 'text', optionsJson: '', required: 0, sort: 0 })

const load = async () => {
  const res = await listTypes()
  types.value = res.data
}

const fieldsOf = (t) => t.fields || []

const fieldTypeLabel = (type) => FIELD_TYPES.find((f) => f.value === type)?.label || type

const openType = (t) => {
  if (t) {
    typeForm.value = { ...t }
  } else {
    typeForm.value = { id: null, name: '', code: '', description: '', status: 1 }
  }
  typeDialog.value = true
}

const saveType = async () => {
  saving.value = true
  try {
    if (typeForm.value.id) {
      await updateType(typeForm.value.id, { ...typeForm.value })
      ElMessage.success('更新成功')
    } else {
      await createType({ ...typeForm.value })
      ElMessage.success('创建成功')
    }
    typeDialog.value = false
    load()
  } finally {
    saving.value = false
  }
}

const removeType = async (t) => {
  await ElMessageBox.confirm(`确定删除工单类型「${t.name}」吗？`, '提示', { type: 'warning' })
  await deleteType(t.id)
  ElMessage.success('删除成功')
  load()
}

const openField = (type, f) => {
  fieldTargetType.value = type
  if (f) {
    fieldForm.value = { ...f }
  } else {
    fieldForm.value = { id: null, fieldName: '', fieldKey: '', fieldType: 'text', optionsJson: '', required: 0, sort: 0 }
  }
  fieldDialog.value = true
}

const saveField = async () => {
  savingField.value = true
  try {
    if (fieldForm.value.id) {
      await updateField(fieldForm.value.id, { ...fieldForm.value })
      ElMessage.success('更新成功')
    } else {
      await addField(fieldTargetType.value.id, { ...fieldForm.value })
      ElMessage.success('添加成功')
    }
    fieldDialog.value = false
    load()
  } finally {
    savingField.value = false
  }
}

const removeField = async (f) => {
  await ElMessageBox.confirm(`确定删除字段「${f.fieldName}」吗？`, '提示', { type: 'warning' })
  await deleteField(f.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.flex-spacer {
  flex: 1;
}
.type-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.field-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.field-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 8px;
  background: #f7f8fa;
  border-radius: 4px;
}
</style>