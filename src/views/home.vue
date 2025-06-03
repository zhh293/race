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
                        <el-avatar> user </el-avatar>
                    </span>      
                </div>
            </el-header>

            <el-carousel
                height="600px"
                direction="vertical"
                type="card"
                :autoplay="true"
            >
                <el-carousel-item v-for="key in item" :key="item">
                <h3 text="2xl" justify="center">{{ key }}</h3>
                </el-carousel-item>
            </el-carousel>



          </el-main>
        </el-container>

      </el-container>
    </div>
</template>
  
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { getTimeState } from '@/utils/index'
import { ElMessage } from 'element-plus'

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

const item = [
    ['在蓝心小V里你可以问问AI该怎么制定计划'],
    ["如果你不开心，你也可以在AI原子能力里面和AI聊天"],
    ["如果你想听听音乐，也可以和AI探讨音乐世界的美丽，他也许会帮你制作哦"],
    ["在我的计划里面你可以清楚的查看到每一次AI为你制定的计划，你也可以自定义"],
    ["个人中心里面我们会为您统计您的计划安排和生活，有更加针对化的建议哦"]
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
.el-carousel__item h3 {
  color: #475669;
  opacity: 0.80;
  line-height: 300px;
  margin: 0;
  text-align: center;
  margin-left: 0;
  padding-left: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}
</style>