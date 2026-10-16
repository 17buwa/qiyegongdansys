<template>
  <div class="page-container">
    <el-card shadow="never" style="max-width: 760px; margin: 0 auto">
      <template #header>
        <div style="display: flex; align-items: center">
          <el-button link :icon="ArrowLeft" @click="router.back()">返回</el-button>
          <span style="margin-left: 8px">创建工单</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="工单类型" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择工单类型" style="width: 100%" @change="onTypeChange">
            <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入工单标题" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="5"
            placeholder="请描述问题详情"
          />
        </el-form-item>

        <el-form-item label="优先级">
          <el-radio-group v-model="form.priority">
            <el-radio-button v-for="(v, k) in PRIORITY_CONFIG" :key="k" :value="k">
              {{ v.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <!-- 动态自定义字段 -->
        <template v-if="currentFields.length">
          <el-divider content-position="left">附加信息</el-divider>
          <el-form-item
            v-for="f in currentFields"
            :key="f.id"
            :label="f.fieldName"
            :required="f.required === 1"
          >
            <el-input
              v-if="f.fieldType === 'text'"
              v-model="fieldValues[f.fieldKey]"
              :placeholder="`请输入${f.fieldName}`"
            />
            <el-input
              v-else-if="f.fieldType === 'textarea'"
              v-model="fieldValues[f.fieldKey]"
              type="textarea"
              :rows="3"
              :placeholder="`请输入${f.fieldName}`"
            />
            <el-input-number
              v-else-if="f.fieldType === 'number'"
              v-model="fieldValues[f.fieldKey]"
              :placeholder="`请输入${f.fieldName}`"
              style="width: 200px"
            />
            <el-select
              v-else-if="f.fieldType === 'select'"
              v-model="fieldValues[f.fieldKey]"
              :placeholder="`请选择${f.fieldName}`"
              style="width: 100%"
            >
              <el-option v-for="opt in parseOptions(f.optionsJson)" :key="opt" :label="opt" :value="opt" />
            </el-select>
            <el-date-picker
              v-else-if="f.fieldType === 'date'"
              v-model="fieldValues[f.fieldKey]"
              type="date"
              value-format="YYYY-MM-DD"
              :placeholder="`请选择${f.fieldName}`"
              style="width: 100%"
            />
          </el-form-item>
        </template>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submit">提交</el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { listTypes } from '@/api/ticketType'
import { createTicket } from '@/api/ticket'
import { PRIORITY_CONFIG } from '@/utils/constants'

const router = useRouter()
const formRef = ref()
const types = ref([])
const submitting = ref(false)
const fieldValues = reactive({})

const form = reactive({
  typeId: null,
  title: '',
  description: '',
  priority: 'MEDIUM'
})

const rules = {
  typeId: [{ required: true, message: '请选择工单类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

const currentFields = computed(() => {
  const t = types.value.find((i) => i.id === form.typeId)
  return t?.fields || []
})

const parseOptions = (json) => {
  if (!json) return []
  try {
    const arr = JSON.parse(json)
    return Array.isArray(arr) ? arr : []
  } catch (e) {
    return []
  }
}

const onTypeChange = () => {
  Object.keys(fieldValues).forEach((k) => delete fieldValues[k])
}

const submit = async () => {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    // 校验必填自定义字段
    for (const f of currentFields.value) {
      if (f.required === 1 && (fieldValues[f.fieldKey] === undefined || fieldValues[f.fieldKey] === null || fieldValues[f.fieldKey] === '')) {
        ElMessage.warning(`请填写${f.fieldName}`)
        return
      }
    }
    submitting.value = true
    try {
      const data = {
        title: form.title,
        description: form.description,
        priority: form.priority,
        typeId: form.typeId,
        fieldValues: { ...fieldValues }
      }
      const res = await createTicket(data)
      ElMessage.success(res.message || '创建成功')
      router.push(`/tickets/${res.data.id}`)
    } finally {
      submitting.value = false
    }
  })
}

onMounted(async () => {
  const res = await listTypes()
  types.value = res.data
})
</script>