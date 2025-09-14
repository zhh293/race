<template>
    <div class="common-layout" style="display: flex; height: 100vh; width: 100%;">
      <el-container>
        <el-container>
          <el-aside width="20%">
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
                        <el-avatar src=""> {{ userStore.username || 'user' }} </el-avatar>
                    </span>      
                </div>
            </el-header>
            
            <div class="image-container">
              <div v-if="userStore.displayedImage" class="image-wrapper">
                <img 
                  :src="userStore.displayedImage" 
                  alt="Generated Image" 
                  class="generated-image"
                />
                <div class="image-actions" v-if="userStore.displayedImage">
                  <el-button 
                    type="primary" 
                    @click="handleDownload"
                  >
                    下载图片
                  </el-button>
                </div>
              </div>

              <div v-else class="empty-state">
                <el-empty description="暂无生成的图片" />
                <p style="display: flex; align-content: center; justify-content: center;">
                  请前往AI生成页面创建您的第一张图片
                </p>
              </div>
            </div>
          </el-main>
        </el-container>
      </el-container>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import { getTimeState } from '@/utils/index'
import { useUserStore } from '@/stores/modules/user'
import { ElMessage } from 'element-plus'
import { ChatRound, Memo, UserFilled, FullScreen } from '@element-plus/icons-vue'

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
    ElMessage.warning(`全屏模式失败: ${err.message}`)
  })
}

// 使用用户存储
const userStore = useUserStore()

// 初始化用户状态
onMounted(() => {
  userStore.initUserState()
  
  // 如果没有当前图片但有历史图片，显示最新的一张
  if (!userStore.currentImage && userStore.hasGeneratedImages) {
    userStore.setCurrentImage(userStore.generatedImages[0].ossUrl)
  }
})

// 下载图片处理
const handleDownload = () => {
  if (!userStore.displayedImage) return
  
  try {
    const link = document.createElement('a')
    link.href = userStore.displayedImage
    link.download = `ai-image-${new Date().getTime()}.jpg`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    ElMessage.success('图片下载成功')
  } catch (error) {
    ElMessage.error('图片下载失败')
    console.error('下载图片失败:', error)
  }
}
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
.image-container {
  margin-top: 20px;
}
.generated-image {
  max-width: 100%;
  max-height: 70vh;
  display: block;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.image-actions {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.empty-state {
  text-align: center;
  margin-top: 50px;
}
</style>