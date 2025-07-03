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
                        {{ timeGreeting }}
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
              <el-descriptions
                  title="个人信息"
                  direction="vertical"
                  border
                  style="margin-top: 20px; margin-left: 0; width: 80%;"
              >
                <el-descriptions-item
                    :rowspan="1"
                    :colspan="1"
                    :width="240"
                    label="Photo"
                    align="center"
                >
                  <img :src="imgUrl" alt="头像"style="width: 40%; height: 40%;"/>
                  <el-button @click="changeAdvater" style="display: flex; width: 40%; height: 2%; font-size: smaller; justify-self: center;">
                    <el-icon><UserFilled /></el-icon>
                    修改头像
                  </el-button>
                </el-descriptions-item>
                <el-descriptions-item label="Username">{{ username }}</el-descriptions-item>
                <el-descriptions-item label="Email">{{ email }}</el-descriptions-item>
                <el-descriptions-item label="weatherImg">
                  <img :src="weatherImage" alt="天气图片" style="display: flex; width: 40%; height: 40%; justify-self: center;"/>
                </el-descriptions-item>
                <el-descriptions-item label="User ID">{{ userId }}</el-descriptions-item>
                <el-descriptions-item label="Remarks">
                  <el-tag size="big" type="primary" @click="toggleTag('student')" v-show="tags.student">学生</el-tag>
                  <el-tag size="big" type="success" @click="toggleTag('worker')" v-show="tags.worker">上班族</el-tag>
                  <el-tag size="big" type="info" @click="toggleTag('retiree')" v-show="tags.retiree">退休族</el-tag>
                  <el-tag type="warning" @click="resetTags" v-show="!allTagsVisible">点我重置</el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <!-- <div class="tag-display">
              <p class="tag-text">FINDING YOU ARE {{ currentTags }}</p>
            </div> -->

            <div style="display: flex; height: 50%; width: 80%; justify-content: center; align-items: center; margin-top: 2%;">
                <LocationMap 
                  :userId="id"
                  @position-update="handlePositionUpdate"
                  @ws-message="handleWsMessage"
                />
            </div>
          </el-main>
        </el-container>
      </el-container>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed, onBeforeUnmount, nextTick } from 'vue'
import { getTimeState } from '@/utils/index'
import { useUserStore } from '@/stores/modules/user'
import { ChatRound, Memo, UserFilled, FullScreen } from '@element-plus/icons-vue'
import { reqUserShow, reqUserUpdate, reqUserUpload } from '@/api/user/show'
import type { UserUpdateForm, UserUploadForm} from '@/api/user/show/type'
import { onBeforeMount } from 'vue'
import { useRealtimeLocationWS } from '@/utils/ws'
import LocationMap from '@/components/LocationMap.vue'


// 侧边栏状态
const isCollapse = ref(false)
const handleOpen = (key: string, keyPath: string[]) => {
  console.log('Menu opened:', key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log('Menu closed:', key, keyPath)
}

// 时间问候语
const timeGreeting = computed(() => getTimeState() || '')

// 全屏功能
const handleFullscreen = () => {
  document.documentElement.requestFullscreen().catch(err => {
    console.error('全屏模式失败:', err)
  })
}

// 用户标签状态
const tags = ref({
  student: true,
  worker: true,
  retiree: true
})

// 切换标签状态
const toggleTag = (tag: keyof typeof tags.value) => {
  tags.value[tag] = !tags.value[tag]
}

// 重置所有标签
const resetTags = () => {
  tags.value = {
    student: true,
    worker: true,
    retiree: true
  }
}

// 计算属性
const allTagsVisible = computed(() => 
  tags.value.student && tags.value.worker && tags.value.retiree
)

const username = ref('')
const email = ref('')
const userStore = useUserStore()
const id = userStore.userId
const weatherImage = ref('')
const userId = ref()

const imgUrl = ref("/5.png")
const changeAdvater = async () => {
  // 创建隐藏的文件输入元素
  const fileInput = document.createElement('input')
  fileInput.type = 'file'
  fileInput.accept = 'image/*'
  
  fileInput.onchange = async (e) => {
    const file = (e.target as HTMLInputElement).files?.[0]
    if (!file) return
    
    try {
      const form: UserUploadForm = {
        userId: id,
        file: file    
      }
      const res = await reqUserUpload(form)
      imgUrl.value = res.data || '/5.png'
    } catch (error) {
      console.error('修改头像失败:', error)
    }

    try {
      const form: UserUpdateForm = {
        imageUrl: imgUrl.value
      }
      const res = await reqUserUpdate(form)
      if (res.code === 200) {
        ElMessage.success('头像修改成功')
      } else {
        ElMessage.error('头像修改失败')
      }
    } catch (error) {
      console.error('修改头像失败:', error)
    }
  }
  
  // 这行应该放在onchange回调函数外部
  fileInput.click() 
}

const handlePositionUpdate = (pos: { lng: number; lat: number }) => {
  console.log('位置更新:', pos)
}

const handleWsMessage = (msg: any) => {
  console.log('收到WS消息:', msg)
  if (msg.status === 'success') {
    ElMessage.success('位置上报成功')
  }
}

onMounted(async () => {
      try {
      const res = await reqUserShow(id.toString())
      if(res.code === 200) {
        username.value = res.data.username || '未设置用户名'
        email.value = res.data.email || '未设置邮箱'
        userId.value = id
        weatherImage.value = res.data.weatherImageUrl || '/defaultWeather.png'
        imgUrl.value = res.data.imageUrl || '/5.png'
      } else {
        console.error('获取用户信息失败:', res.message)
      }
    }catch
      (error) {
      console.error('修改头像失败:', error)
    }finally {
      console.log('用户信息加载完成')
    }
})
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
.tag-display {
  display: flex;
  width: 80%;
  height: 40%;
  margin-top: 2%;
  align-content: center;
  justify-content: center;
}
.tag-text {
  width: 100%;
  text-align: center;
  font-family: bigWord;
  font-size: 6vh;
}
</style>