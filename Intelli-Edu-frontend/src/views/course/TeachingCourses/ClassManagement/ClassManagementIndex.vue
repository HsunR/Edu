<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CirclePlus, Search, MoreFilled, CopyDocument } from '@element-plus/icons-vue'
import moment from 'moment'
import { getCourseClasses } from '@/api/course/course'
import {
  createClass,
  updateClass,
  getClassMembers,
  removeMember
} from '@/api/course/class'
import type { ClassVO, ClassCreateRequest, ClassUpdateRequest, ClassMemberVO } from '@/api/course/types'
import { ClassStatus } from '@/types/enums'
import type { FormInstance, FormRules } from 'element-plus'

const route = useRoute()
const courseId = route.params.id as string

const loading = ref(false)
const classes = ref<ClassVO[]>([])
const selectedClass = ref<ClassVO | null>(null)
const members = ref<ClassMemberVO[]>([])
const membersTotal = ref(0)
const membersPage = ref(1)
const membersPageSize = ref(10)

const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const createFormRef = ref<FormInstance>()
const editFormRef = ref<FormInstance>()

const createForm = ref<ClassCreateRequest>({
  courseId,
  className: '',
  maxStudents: 100,
  startDate: '',
  endDate: ''
})

const editForm = ref<ClassUpdateRequest & { classId?: string }>({
  className: '',
  maxStudents: 100,
  startDate: '',
  endDate: '',
  status: undefined
})

const createRules = reactive<FormRules>({
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
})

const statusMap: Record<number, { label: string; type: 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined }> = {
  [ClassStatus.Enrolling]: { label: '招生中', type: 'success' },
  [ClassStatus.InProgress]: { label: '进行中', type: 'primary' },
  [ClassStatus.Ended]: { label: '已结束', type: 'info' }
}

async function loadClasses() {
  loading.value = true
  try {
    classes.value = await getCourseClasses(courseId)
    if (classes.value.length > 0 && !selectedClass.value) {
      selectClass(classes.value[0])
    }
  } catch {
    classes.value = []
  } finally {
    loading.value = false
  }
}

async function loadMembers() {
  if (!selectedClass.value) return
  try {
    const result = await getClassMembers(selectedClass.value.classId, {
      current: membersPage.value,
      pageSize: membersPageSize.value
    })
    members.value = result.records
    membersTotal.value = result.total
  } catch {
    members.value = []
  }
}

function selectClass(cls: ClassVO) {
  selectedClass.value = cls
  membersPage.value = 1
  loadMembers()
}

function openCreateDialog() {
  createForm.value = {
    courseId,
    className: '',
    maxStudents: 100,
    startDate: '',
    endDate: ''
  }
  createDialogVisible.value = true
}

function toOffsetDate(value: string): string {
  if (!value) return ''
  return moment(value).format('YYYY-MM-DDTHH:mm:ss+08:00')
}

function fromOffsetDate(value: string): string {
  if (!value) return ''
  return value.split('T')[0]
}

async function handleCreate() {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const payload = {
        ...createForm.value,
        startDate: toOffsetDate(createForm.value.startDate),
        endDate: toOffsetDate(createForm.value.endDate)
      }
      await createClass(payload)
      ElMessage.success('创建班级成功')
      createDialogVisible.value = false
      await loadClasses()
    } catch (error) {
      const msg = error instanceof Error ? error.message : '创建失败'
      ElMessage.error(msg)
    }
  })
}

function openEditDialog(cls: ClassVO) {
  editForm.value = {
    classId: cls.classId,
    className: cls.className,
    maxStudents: cls.maxStudents,
    startDate: fromOffsetDate(cls.startDate),
    endDate: fromOffsetDate(cls.endDate),
    status: cls.status
  }
  editDialogVisible.value = true
}

async function handleEdit() {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      const { classId, ...data } = editForm.value
      if (!classId) return
      const payload = {
        ...data,
        startDate: toOffsetDate(data.startDate),
        endDate: toOffsetDate(data.endDate)
      }
      await updateClass(classId, payload as ClassUpdateRequest)
      ElMessage.success('更新成功')
      editDialogVisible.value = false
      await loadClasses()
    } catch (error) {
      const msg = error instanceof Error ? error.message : '更新失败'
      ElMessage.error(msg)
    }
  })
}

async function handleRemoveMember(memberId: string) {
  if (!selectedClass.value) return
  try {
    await ElMessageBox.confirm('确认移除该学生吗？', '移除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeMember(selectedClass.value.classId, memberId)
    ElMessage.success('移除成功')
    await loadMembers()
    await loadClasses()
  } catch {
    // cancelled
  }
}

function copyInviteCode(code: string) {
  navigator.clipboard.writeText(code).then(() => {
    ElMessage.success('邀请码已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

function handleMembersPageChange(page: number) {
  membersPage.value = page
  loadMembers()
}

onMounted(loadClasses)
</script>

<template>
  <div v-loading="loading" class="class-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/teaching' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>班级管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="content">
        <div class="left">
          <div class="left-actions">
            <el-button type="primary" :icon="CirclePlus" round @click="openCreateDialog">新建班级</el-button>
          </div>
          <el-scrollbar height="65vh">
            <el-empty v-if="classes.length === 0" description="暂无班级" :image-size="60" />
            <div
              v-for="cls in classes"
              :key="cls.classId"
              class="class-item"
              :class="{ active: selectedClass?.classId === cls.classId }"
              @click="selectClass(cls)"
            >
              <div class="class-info">
                <span class="class-name">{{ cls.className }}</span>
                <el-tag :type="statusMap[cls.status]?.type || 'info'" size="small">
                  {{ statusMap[cls.status]?.label || '未知' }}
                </el-tag>
              </div>
              <div class="class-meta">
                <span>{{ cls.currentStudents }} / {{ cls.maxStudents }} 人</span>
              </div>
              <div class="class-more">
                <el-dropdown trigger="click">
                  <span class="el-dropdown-link" @click.stop>
                    <el-icon><MoreFilled /></el-icon>
                  </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="openEditDialog(cls)">编辑</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </el-scrollbar>
        </div>

        <div class="right">
          <template v-if="selectedClass">
            <div class="right-header">
              <div class="header-info">
                <h3>{{ selectedClass.className }}</h3>
                <div class="invite-code">
                  <span>邀请码：</span>
                  <el-tag type="success" size="large">{{ selectedClass.inviteCode }}</el-tag>
                  <el-button :icon="CopyDocument" link type="primary" @click="copyInviteCode(selectedClass.inviteCode)">
                    复制
                  </el-button>
                </div>
              </div>
              <div class="header-meta">
                <span>学生：{{ selectedClass.currentStudents }} / {{ selectedClass.maxStudents }}</span>
                <span v-if="selectedClass.startDate">开始：{{ selectedClass.startDate?.split('T')[0] }}</span>
                <span v-if="selectedClass.endDate">结束：{{ selectedClass.endDate?.split('T')[0] }}</span>
              </div>
            </div>

            <el-divider />

            <div class="members-section">
              <h4>班级成员</h4>
              <el-table :data="members" stripe style="width: 100%">
                <el-table-column prop="studentName" label="姓名" />
                <el-table-column prop="studentId" label="学号" width="120" />
                <el-table-column prop="joinedAt" label="加入时间" width="180">
                  <template #default="{ row }">
                    {{ row.joinedAt?.split('T')[0] }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row }">
                    <el-button size="small" link type="danger" @click="handleRemoveMember(row.id)">
                      移除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div v-if="membersTotal > membersPageSize" class="members-pagination">
                <el-pagination
                  v-model:current-page="membersPage"
                  :total="membersTotal"
                  :page-size="membersPageSize"
                  layout="prev, pager, next"
                  @current-change="handleMembersPageChange"
                />
              </div>
            </div>
          </template>

          <el-empty v-else description="请选择一个班级查看详情" />
        </div>
      </div>
    </el-card>

    <el-dialog v-model="createDialogVisible" title="新建班级" width="500px">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="createForm.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number v-model="createForm.maxStudents" :min="1" :max="9999" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="createForm.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="createForm.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑班级" width="500px">
      <el-form ref="editFormRef" :model="editForm" :rules="createRules" label-width="100px">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="editForm.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxStudents">
          <el-input-number v-model="editForm.maxStudents" :min="1" :max="9999" />
        </el-form-item>
        <el-form-item label="班级状态" prop="status">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="招生中" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="editForm.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="editForm.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.content {
  display: flex;
  gap: 16px;
}

.left {
  width: 280px;
  flex-shrink: 0;
  border-right: 1px solid #ebeef5;
  padding-right: 16px;

  .left-actions {
    margin-bottom: 12px;
  }
}

.class-item {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  padding: 10px 12px;
  margin-bottom: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;

  &:hover {
    background: #f5f7fa;
  }

  &.active {
    background: #ecf5ff;
    border: 1px solid #409eff;
  }

  .class-info {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
    min-width: 0;
    margin-right: 20px;

    .class-name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .class-meta {
    width: 100%;
    margin-top: 4px;

    span {
      font-size: 12px;
      color: #909399;
    }
  }

  .class-more {
    position: absolute;
    top: 10px;
    right: 8px;
  }
}

.right {
  flex: 1;
  min-width: 0;
}

.right-header {
  .header-info {
    h3 {
      margin: 0 0 8px;
      font-size: 18px;
      color: #303133;
    }

    .invite-code {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .header-meta {
    margin-top: 8px;
    display: flex;
    gap: 16px;

    span {
      font-size: 13px;
      color: #909399;
    }
  }
}

.members-section {
  h4 {
    margin: 0 0 12px;
    font-size: 15px;
    color: #303133;
  }
}

.members-pagination {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}
</style>
