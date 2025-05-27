import { defineStore } from 'pinia'
import { reqLogin, reqRegiter } from '@/api/user'
import { type loginForm, type userResponseData } from '@/api/user/type'
import { useRouter } from 'vue-router'
import { ElNotification } from 'element-plus'
import { ref } from 'vue'

// 定义用户状态类型
interface UserState {
  userId: number | null
  token: string | null
  username: string | null
  email: string | null
}

// 创建用户小仓库
export const useUserStore = defineStore('user', {
  // 小仓库数据
  state: (): UserState => ({
    userId: null,
    token: null,
    username: null,
    email: null
  }),

  actions: {
    // 用户登录方法
    async userLogin(data: loginForm) {
      try {
        const result: userResponseData = await reqLogin(data)
        console.log('Login response:', result)
        
        if (result.code === 200) {
          // 存储用户信息到状态
          this.userId = result.data.userId
          this.email = data.email
          
          // 存储到本地存储，确保刷新后数据不丢失
          localStorage.setItem('userId', result.data.userId.toString())

          
          const router = useRouter()
          router.push({ name: 'home' })
          /*
          ElNotification({
            title: '登录成功',
            message: `欢迎回来，${this.username}`,
            type: 'success',
            duration: 3000
          })*/
        } else {
          ElNotification({
            title: '登录失败',
            message: result.msg || '用户不存在或密码错误',
            type: 'error',
            duration: 3000
          })
        }
      } catch (error: any) {
        console.error('Login request failed:', error)
        ElNotification({
          title: '登录异常',
          message: error.response?.data?.message || '登录请求失败，请稍后再试',
          type: 'error',
          duration: 3000
        })
      }
    },

    // 用户注册方法
    async userRegister(data: loginForm) {
      try {
        const result = await reqRegiter(data)
        console.log('Register response:', result)
        
        if (result.code === 200) {
          ElNotification({
            title: '注册成功',
            message: '请登录后使用完整功能',
            type: 'success',
            duration: 3000
          })
          
          const router = useRouter()
          router.push({ name: 'login' })
        } else {
          ElNotification({
            title: '注册失败',
            message: result.msg || '用户已存在',
            type: 'error',
            duration: 3000
          })
        }
      } catch (error: any) {
        console.error('Register request failed:', error)
        ElNotification({
          title: '注册异常',
          message: error.response?.data?.message || '注册请求失败，请稍后再试',
          type: 'error',
          duration: 3000
        })
      }
    },

    /*
    // 从本地存储初始化用户状态
    initUserState() {
      const userId = localStorage.getItem('userId')
      const token = localStorage.getItem('token')
      if (userId && token) {
        this.userId = parseInt(userId)
        this.token = token
      }
    }*/
  },

  getters: {
    /* 判断用户是否已登录
    isLoggedIn(): boolean {
      return this.userId !== null && this.token !== null
    }*/
  }
})