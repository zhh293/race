import { defineStore } from 'pinia'
import { reqRegister } from '@/api/user/register'
import { reqLogin } from '@/api/user/login'
import { type registerForm, type userRegisterResponseData } from '@/api/user/register/type'
import { type loginForm, type userLoginResponseData } from '@/api/user/login/type'
import { reqAitable } from '@/api/interface'
import { type aiTableForm, type aiResponsePictureData } from '@/api/interface/type'
import { useRouter } from 'vue-router'
import { ElNotification } from 'element-plus'

// 定义用户状态类型
interface UserState {
  userId: number 
  token: string
  username: string
  email: string
  sessionId: string
  generatedImages: UserImage[]
  currentImage: UserImage | null
}

interface UserImage {
  ossUrl: string
  createdAt: number
}

// localStorage键名常量
const STORAGE_KEYS = {
  USER_ID: 'userId',
  TOKEN: 'token',
  USERNAME: 'username',
  EMAIL: 'email',
  SESSION_ID: 'sessionId',
  GENERATED_IMAGES: 'generatedImages',
  CURRENT_IMAGE: 'currentImage'
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    userId: 0,
    token: '',
    username: '',
    email: '',
    sessionId: '',
    generatedImages: [],
    currentImage: null
  }),

  actions: {
    /**
     * 保存用户基本信息到localStorage
     */
    saveUserToLocalStorage() {
      localStorage.setItem(STORAGE_KEYS.USER_ID, this.userId.toString())
      localStorage.setItem(STORAGE_KEYS.TOKEN, this.token)
      localStorage.setItem(STORAGE_KEYS.USERNAME, this.username)
      localStorage.setItem(STORAGE_KEYS.EMAIL, this.email)
      localStorage.setItem(STORAGE_KEYS.SESSION_ID, this.sessionId)
    },

    /**
     * 从localStorage加载用户基本信息
     */
    loadUserFromLocalStorage() {
      const userId = localStorage.getItem(STORAGE_KEYS.USER_ID)
      const token = localStorage.getItem(STORAGE_KEYS.TOKEN)
      const username = localStorage.getItem(STORAGE_KEYS.USERNAME)
      const email = localStorage.getItem(STORAGE_KEYS.EMAIL)
      const sessionId = localStorage.getItem(STORAGE_KEYS.SESSION_ID)

      if (userId && token) {
        this.userId = parseInt(userId)
        this.token = token
        this.username = username || ''
        this.email = email || ''
        this.sessionId = sessionId || ''
      }
    },

        // 用户注册方法
    async userRegister(data: registerForm) {
      try {
        const result: userRegisterResponseData = await reqRegister(data)
        
        if (result.code === 200) {
          ElNotification({
            title: '注册成功',
            message: '请登录后使用完整功能',
            type: 'success',
            duration: 3000
          })
          return true
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
        
        if (result.code === 200) {
          this.userId = result.data.id
          this.email = result.data.email
          this.username = result.data.username
          this.token = result.data.token
          this.sessionId = result.data.sessionId
          
          // 使用新的方法保存用户信息
          this.saveUserToLocalStorage()
          this.loadImagesFromStorage()

          ElNotification({
            title: '登录成功',
            message: `欢迎回来，${this.username}`,
            type: 'success',
            duration: 3000
          })
          return true
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

    // 初始化用户状态
    initUserState() {
      this.loadUserFromLocalStorage()
      this.loadImagesFromStorage()
    },

    /**
     * 从localStorage加载图片数据
     */
    loadImagesFromStorage() {
      const imagesJson = localStorage.getItem(STORAGE_KEYS.GENERATED_IMAGES)
      if (imagesJson) {
        try {
          this.generatedImages = JSON.parse(imagesJson)
          this.generatedImages.sort((a, b) => (b.createdAt || 0) - (a.createdAt || 0))
        } catch (error) {
          console.error('Failed to parse generated images:', error)
          this.generatedImages = []
        }
      }

      const currentImageJson = localStorage.getItem(STORAGE_KEYS.CURRENT_IMAGE)
      if (currentImageJson) {
        try {
          this.currentImage = JSON.parse(currentImageJson)
        } catch (error) {
          console.error('Failed to parse current image:', error)
          this.currentImage = null
        }
      }
    },

    /**
     * 保存图片数据到localStorage
     */
    saveImagesToStorage() {
      localStorage.setItem(STORAGE_KEYS.GENERATED_IMAGES, JSON.stringify(this.generatedImages))
      console.log('保存生成的图片到localStorage:', this.generatedImages)
      localStorage.setItem(STORAGE_KEYS.CURRENT_IMAGE, JSON.stringify(this.currentImage))
    },


    // 生成图片方法
    async generateImage(prompt: string) {
      try {
        const requestData: aiTableForm = {
          picture: prompt,
          userId: this.userId.toString(),
          sessionId: this.sessionId,
          token: this.token
        }

        const response: aiResponsePictureData = await reqAitable(requestData)
        
        if (response.code === 200 && response.data) {
          const newImage: UserImage = {
            ossUrl: response.data,
            createdAt: Date.now()
          }
          
          this.generatedImages.unshift(newImage)
          this.currentImage = newImage
          this.saveImagesToStorage()
          
          ElNotification({
            title: '图片生成成功',
            message: '已生成新图片',
            type: 'success',
            duration: 3000
          })
          
          return newImage.ossUrl
        } else {
          throw new Error(response.msg || '图片生成失败')
        }
      } catch (error: any) {
        ElNotification({
          title: '生成失败',
          message: error.message,
          type: 'error',
          duration: 3000
        })
        throw error
      }
    },

    // 设置当前显示的图片
    setCurrentImage(imageUrl: string) {
      const image = this.generatedImages.find(img => img.ossUrl === imageUrl)
      if (image) {
        this.currentImage = image
        localStorage.setItem(STORAGE_KEYS.CURRENT_IMAGE, JSON.stringify(image))
      }
    },

    // 清除用户数据
    clearUserData() {
      this.$reset()
      Object.values(STORAGE_KEYS).forEach(key => localStorage.removeItem(key))
    }
  },

  getters: {
    // 判断用户是否已登录
    isLoggedIn(): boolean {
      return !!this.token && !!this.userId && !!this.username && !!this.email
    },

    // 获取当前用户的所有图片
    allImages(): UserImage[] {
      return this.generatedImages
    },
    
    // 获取当前显示的图片
    displayedImage(): string | undefined {
      return this.currentImage?.ossUrl
    },
    
    // 检查是否有生成记录
    hasGeneratedImages(): boolean {
      return this.generatedImages.length > 0
    }
  }
})