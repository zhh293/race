import { defineStore } from 'pinia'
import { reqRegiter } from '@/api/user/register'
import { reqLogin } from '@/api/user/login'
import { type registerForm, type userRegisterResponseData } from '@/api/user/register/type'
import { type loginForm, type userLoginResponseData } from '@/api/user/login/type'
import { useRouter } from 'vue-router'
import { ElNotification } from 'element-plus'


// 定义用户状态类型
interface UserState {
  userId: number 
  token: string
  username: string
  email: string
}

// 创建用户小仓库
export const useUserStore = defineStore('user', {
  // 小仓库数据
  state: (): UserState => ({
    userId: 0,
    token: '',
    username: '',
    email: ''
  }),

  actions: {
    // 用户注册方法
    async userRegister(data: registerForm) {
      try {
        const result:userRegisterResponseData = await reqRegiter(data)
        console.log('Register response:', result)   //code,msg,注册成功请登录
        
        if (result.code === 200) {
          ElNotification({
            title: '注册成功',
            message: '请登录后使用完整功能',
            type: 'success',
            duration: 3000
          })
          
          const router = useRouter()
          router.push('/login')

        } else {
          ElNotification({
            title: '注册失败',
            message: result.msg,
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

    // 用户登录方法
    async userLogin(data: loginForm) {
      try {
        const result: userLoginResponseData = await reqLogin(data)
        console.log('Login response:', result)
        
        if (result.code === 200) {
          // 存储用户信息到状态
          this.userId = result.data.id
          this.email = result.data.email
          this.username = result.data.username
          
          // 存储到本地存储，确保刷新后数据不丢失
          localStorage.setItem('userId', result.data.id.toString())

          const router = useRouter()
          router.push('/')

          ElNotification({
            title: '登录成功',
            message: `欢迎回来，${this.username}`,
            type: 'success',
            duration: 3000
          })
          
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


    // 从本地存储初始化用户状态
    initUserState() {
      const userId = localStorage.getItem('userId')
      const token = localStorage.getItem('token')
      if (userId && token) {
        this.userId = parseInt(userId)
        this.token = token
      }
    }
  },

  //getters计算属性
  getters: {
    //判断用户是否已登录
    isLoggedIn(): boolean {
      return this.userId !== null && this.token !== null
    }
  }
})