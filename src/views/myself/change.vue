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

            <div style="width: 80%;">
                <h3>个人信息修改</h3>
                <el-form>
                    <el-form-item label="头像">
                        <img :src="avatarImageUrl" style="margin-bottom: 2%; width: 30%; height: 100%;"></img>
                        <el-button  type="primary" size="small" @click="changeAdvater">修改头像</el-button>
                    </el-form-item>
                    <el-form-item label="用户ID">
                        <el-input v-model="userStore.userId" placeholder="用户ID" disabled></el-input>
                    </el-form-item>
                    <el-form-item label="用户名">
                        <el-input v-model="userName" placeholder="请输入修改用户名"></el-input>
                    </el-form-item>
                    <el-form-item label="邮箱">
                        <el-input v-model="Email" placeholder="请输入修改邮箱"></el-input>
                    </el-form-item>
                    <el-form-item label="密码">
                        <el-input type="password" show-password v-model="password" placeholder="请修改密码"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="update">保存修改</el-button>
                    </el-form-item>
                </el-form>
            </div>


            
           </el-main>
        </el-container>
      </el-container>
    </div>
</template>


<script lang="ts" setup>
import { ref, computed, onMounted} from 'vue'
import { getTimeState } from '@/utils'
import { useUserStore } from '@/stores/modules/user'
import { reqUserShow, reqUserUpdate, reqUserUpload } from '@/api/user/show'
import type{ UserUpdateForm, UserUploadForm } from '@/api/user/show/type'

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

const userStore = useUserStore()
const avatarImageUrl = ref('/5.png')
const userName = ref(userStore.username || '')
const Email = ref(userStore.email || '')
const password = ref('')

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
        userId: userStore.userId,
        file: file    
      }
      const res = await reqUserUpload(form)
      console.log('头像上传结果:', res)
      if(res.code === 200) {
        console.log('头像上传成功')
        avatarImageUrl.value = res.data
      }
    } catch (error) {
      console.error('修改头像失败:', error)
    }
  }
  
  // 这行应该放在onchange回调函数外部
  fileInput.click() 
}

const update = async () => {
    try {
            const form: UserUpdateForm = {
                username: userName.value,
                email: Email.value,
                password: password.value,
                avatarImageUrl: avatarImageUrl.value
            }
            const res = await reqUserUpdate(form)
            if (res.code === 200) {
                ElMessage.success('信息修改成功')

                try{
                    const response = await reqUserShow(userStore.userId.toString())
                    if(response.code === 200) {
                        userStore.username = response.data.username
                        userStore.email = response.data.email
                        avatarImageUrl.value = response.data.avatarImageUrl
                    }
                }catch (error) {
                    console.error('获取用户信息失败:', error)
                }

            } else {
                ElMessage.error('修改失败')
            }
        } catch (error) {
            console.error('修改信息失败:', error)
        }
}


onMounted(async () => {
  try {
    const response = await reqUserShow(userStore.userId.toString())
    if(response.code === 200) {
      
        userName.value = response.data.username,
        Email.value = response.data.email,
        password.value = response.data.password,
        avatarImageUrl.value = response.data.avatarImageUrl
      
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
})
</script>