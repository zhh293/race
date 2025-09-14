<template>
    <div class="common-layout" style="display: flex; height: 100vh; width: 100%;">
        <el-container>
            <el-container>
                <el-aside width="25%">
                    <el-radio-group v-model="isCollapse" style="margin-bottom: 20px;">
                        <el-radio-button :value="false">打开侧边栏</el-radio-button>
                        <el-radio-button :value="true">收缩侧边栏</el-radio-button>
                    </el-radio-group>
                    <el-menu
                        router
                        :default-active="$route.path"
                        class="el-menu-vertical-demo"
                        :collapse="isCollapse"
                        @open="handleOpen"
                        @close="handleClose"
                    >
                        <el-sub-menu>
                            <template #title>
                                <el-icon><ChatRound /></el-icon>
                                <span>问问AI</span>
                            </template>
                            <el-menu-item-group>
                                <el-menu-item index="/tableAi">蓝心小V</el-menu-item>
                                <el-menu-item index="/chatAi">AI原子能力</el-menu-item>
                                <el-menu-item index="/musicAi">AI语音研究实验室</el-menu-item>
                            </el-menu-item-group>
                        </el-sub-menu>

                        <el-menu-item index="/table">
                            <el-icon><Memo /></el-icon>
                            <template #title>我的计划表</template>
                        </el-menu-item>

                        <el-sub-menu>
                            <template #title>
                                <el-icon><UserFilled /></el-icon>
                                <span>个人中心</span>
                            </template>
                            <el-menu-item-group>
                                <el-menu-item index="/my">个人信息</el-menu-item>
                                <el-menu-item index="/used">使用情况</el-menu-item>
                            </el-menu-item-group>
                        </el-sub-menu>
                    </el-menu>
                </el-aside>

                <el-main>
                    <el-header style="font-size: larger;">
                        <div style="display: flex; width: 100%;">
                            <span style="display: flex; justify-content: center; width: 50%;">
                                {{ thisTimeState }}
                            </span>

                            <span style="display: flex; width: 50%; justify-items: right; justify-content: right;">
                                <span @click="handleFullscreen" style="display: flex; margin-right: 8%;">
                                    <el-icon><FullScreen /></el-icon>
                                </span>
                                <el-avatar>{{ userStore.username || 'user' }}</el-avatar>
                            </span>      
                        </div>
                    </el-header>

                    <div>
                        外边框: <el-switch v-model="parentBorder" /> 
                        内边框: <el-switch v-model="childBorder" /> 
                        保持展开:<el-switch v-model="preserveExpanded" />

                        <el-table :data="tableData" :border="parentBorder">
                          <el-table-column type="expand">
                            <template #default="{ row }">
                              <div style="padding: 20px;">
                                <p><strong>Detail Content:</strong> {{ row.content }}</p>
                                <p><strong>Created:</strong> {{ row.createdAt }}</p>
                                <p><strong>Updated:</strong> {{ row.updatedAt }}</p>
                              </div>
                            </template>
                          </el-table-column>
                          <el-table-column prop="username" label="Username" />
                          <el-table-column prop="content" label="Content Summary" :formatter="contentSummary" />
                        </el-table>
                    </div>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>
  
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { getTimeState } from '@/utils/index'
import { useUserStore } from '@/stores/modules/user'
import { reqRecordGet } from '@/api/user/login'
import { type userRecordResponseData } from '@/api/user/login/type'

const isCollapse = ref(false)
const handleOpen = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
    console.log(key, keyPath)
}

/*时间问候*/
const timeState = getTimeState()
const thisTimeState = ref('')
onMounted(() => {
    thisTimeState.value = timeState || ''
    console.log(thisTimeState.value)
})

/*网页全屏*/
const handleFullscreen = () => {
    document.documentElement.requestFullscreen()
}

/*获取用户记录信息 */
const userStore = useUserStore()
const userId = userStore.userId
const tableData = ref<Array<{
    username: string
    content: string
    createdAt: string
    updatedAt: string
}>>([])

onMounted(async () => {
  try {
    const receiveInfo = await reqRecordGet(userId)
    if(receiveInfo.code === 200) {
      tableData.value = receiveInfo.data.flatMap((session: { 文本对话: any[] }) => 
        session.文本对话.map((conversation: { username: any; content: any; createdAt: any; updatedAt: any }) => ({
          username: conversation.username,
          content: conversation.content,
          createdAt: conversation.createdAt,
          updatedAt: conversation.updatedAt
        }))
    )
    } else {
      console.error('Failed to fetch user records:', receiveInfo.msg)
    }
  } catch (error) {
    console.error('Failed to fetch user record:', error)
  }
})
const contentSummary = (row: { content: string }) => {
    return row.content.length > 50 ? row.content.slice(0, 50) + '...' : row.content
}

const parentBorder = ref(false)
const childBorder = ref(false)
const preserveExpanded = ref(false)
</script>

<style>
.el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
}
.el-menu-item-group__title {
    height: 0;
    width: 0;
}
.el-main {
    margin-top: 0;
    padding-top: 0.8%;
    margin-left: 0;
    padding-left: 0;
}
</style>