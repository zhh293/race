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

          <!-- 全新设计的轮播图 -->
            <div class="carousel-container">
                <el-carousel
                    height="550px"
                    direction="horizontal"
                    type="card"
                    :autoplay="true"
                    :interval="5000"
                    indicator-position="outside"
                    arrow="always"
                    @change="onCarouselChange"
                >
                    <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
                        <div class="custom-carousel-card" :class="{'active': currentIndex === index}">
                            <div class="card-content">
                                <div class="card-icon">
                                    <el-icon :size="64" class="icon-element">
                                        <component :is="item.icon" />
                                    </el-icon>
                                </div>
                                <h3 class="card-title">{{ item.title }}</h3>
                                <p class="card-desc">{{ item.desc }}</p>
                            </div>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>



          </el-main>
        </el-container>

      </el-container>
    </div>
</template>
  
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { getTimeState } from '@/utils/index'
import { ElMessage } from 'element-plus'
import { FullScreen, ChatRound, Mic, Memo, UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/modules/user'

const userStore = useUserStore()

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

// 轮播图当前索引
const currentIndex = ref(0)

// 轮播图变更处理
const onCarouselChange = (index: number) => {
    currentIndex.value = index
}

// 优化后的轮播图数据结构（添加图标和背景色）
const carouselItems = [
    {
        title: '蓝心小V - 智能计划助手',
        desc: '问问AI如何制定科学计划，高效管理时间，提升工作学习效率。',
        icon: ChatRound,
        bgColor: '#f0f7ff',
        textColor: '#3a7bd5'
    },
    {
        title: 'AI原子能力 - 情感陪伴',
        desc: '与AI聊天倾诉，获取情感支持和专业建议，让心情更舒畅。',
        icon: ChatRound,
        bgColor: '#f8f9fa',
        textColor: '#5c6b77'
    },
    {
        title: 'AI音乐实验室 - 创作无限',
        desc: '探索音乐世界，AI不仅能推荐歌曲，还能帮你创作专属旋律。',
        icon: Mic,
        bgColor: '#fff6f0',
        textColor: '#ff7a45'
    },
    {
        title: '我的计划表 - 目标管理',
        desc: '查看AI制定的计划，自定义编辑和跟踪进度，实现目标可视化。',
        icon: Memo,
        bgColor: '#f0fff4',
        textColor: '#52c41a'
    },
    {
        title: '个人中心 - 数据洞察',
        desc: '查看计划统计和生活习惯分析，获取个性化建议，优化自我管理。',
        icon: UserFilled,
        bgColor: '#fcf6ff',
        textColor: '#722ed1'
    }
]
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

/* 优化后的轮播图样式 */
.el-carousel__item {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  padding: 40px;
  box-sizing: border-box;
}

.carousel-content {
  text-align: center;
  max-width: 800px;
  width: 100%;
}

.carousel-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 15px;
  line-height: 1.3;
}

.carousel-desc {
  font-size: 18px;
  color: #666;
  line-height: 1.6;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}

.blue-title {
  color: #3a7bd5;
}

.blue-desc {
  color: #4a88d9;
}

.gray-title {
  color: #5c6b77;
}

.gray-desc {
  color: #6d7a85;
}
</style>