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
                        <span  @click="handleFullscreen"  style="display: flex; margin-right: 8%;">
                            <el-icon><FullScreen /></el-icon>
                        </span>
                        <el-avatar>{{ userStore.username || 'user' }}</el-avatar>
                    </span>      
                </div>
            </el-header>

            <div style="display: flex; width: 100%; height: 100%; align-items: flex-end;">
                <div style=" width: 80%; padding-bottom: 0; margin-bottom: 10%; height: 50%; margin-right: 10%;">

                    <div style="display: flex; margin-bottom: 2%; width: 100%;" v-show="isShow">
                        <span style="display: flex; width: 100%;">
                            <el-card style="display: flex; margin-left: auto; align-items: self-end;" v-model="userThing">
                                <p style="display: flex; width: auto;">{{ userThing.picture }}</p>
                            </el-card>
                            <el-avatar style="margin-left: 1%"> user </el-avatar>
                        </span>
                    </div>

                    <div style="display: flex; width: 100%; margin-bottom: 2%;" v-show="isShow">
                      <span style="display: flex;">
                        <el-avatar> AI </el-avatar>
                        <el-card>
                          <template v-if="backThing === '思考中...'">
                            <p>思考中...</p>
                          </template>
                          <template v-else-if="backThing">
                            <img :src="backThing" alt="生成的图片" style="max-width: 100%; max-height: 400px; object-fit: contain;">
                          </template>
                        </el-card>
                      </span>
                    </div>                  
                    
                    <el-input clearable v-model="text" type="textarea" 
                            :userThing="userThing.picture"
                            style="display: flex; width: 100%; height: 30%; margin-bottom: 5%; border-width: 2px;" 
                            placeholder="来制定你的计划吧" 
                            :autosize="{ minRows: 3, maxRows: 6 }"
                            @keyup.enter="handleEnter">
                    </el-input>

                    <div style="display: flex; width: 100%; justify-content: right;">
                        <el-button style="display: flex; margin-right: 5%;" type="primary" plain round @click="submitClick">
                            <el-icon style="color: blue;"><Upload /></el-icon>
                        </el-button>
                    </div>

                </div>
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
import { useUserStore } from '@/stores/modules/user'
import { type aiTableForm } from '@/api/interface/type' 
import { useRouter } from 'vue-router'

/*侧边栏*/
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

/*对话框写入部分*/
const text = ref()
const isShow = ref(false)

const handleEnter = (event: KeyboardEvent) => {
  if (!event.shiftKey) {
    event.preventDefault()
    submitClick()
  }
}

/*对话框交互部分*/
// 获取用户状态
const userStore = useUserStore()
const userId = userStore.userId.toString()

const backThing = ref()
const userThing = ref<aiTableForm>({
  picture: text.value,
  userId: userId ,
  sessionId: userStore.sessionId,
  token: userStore.token
})

const submitClick = async () => {
  const inputValue = text.value.trim()
  if (!inputValue) {
    ElMessage.warning('请输入图片描述')
    return
  }


  try {
    isShow.value = true
    userThing.value.picture = inputValue
    backThing.value = '思考中...'

    // 使用store中的方法生成图片
    const imageData = await userStore.generateImage(inputValue)
    backThing.value = imageData
    
    ElMessage.success('图片生成成功')
    console.log('生成的图片:', imageData)   // 打印生成的图片数据
    text.value = ''
  } catch (error: any) {
    console.error('图片生成失败:', error)
    ElMessage.error(error.message || '图片生成失败')
    backThing.value = ''
  } finally {
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
</style>