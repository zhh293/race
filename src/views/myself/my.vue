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
                <div class="amap-wrapper" style="display: flex; width: 100%; height: 100%;">
                  <div ref="mapContainer" class="map-container"></div>
                  <div v-if="locationLoading" class="location-loading-mask">
                    <el-icon class="is-loading"><Loading /></el-icon>
                    <span style="margin-left: 5%;">正在定位...</span>
                  </div>
                  <div v-if="locationError" class="location-error-mask">
                    <el-alert :title="locationError" type="error" show-icon />
                  </div>
                </div>
              
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
import { reqUserShow, reqUserUpdate } from '@/api/user/show'
import type { UserUpdateForm} from '@/api/user/show/type'
import { onBeforeMount } from 'vue'

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
const userId = ref()
const id = userStore.userId
const weatherImage = ref('')

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
      const form: UserUpdateForm = {
        userId: id,
        file: file    
      }
      const res = await reqUserUpdate(form)
      imgUrl.value = res.data || '/5.png'
    } catch (error) {
      console.error('修改头像失败:', error)
    }
  }
  
  // 这行应该放在onchange回调函数外部
  fileInput.click() 
}

// 地图相关
interface AMapComponents {
  map: AMap.Map | null;
  marker: AMap.Marker | null;
  infoWindow: AMap.InfoWindow | null;
  polyline: AMap.Polyline | null;
}
const mapContainer = ref<HTMLElement | null>(null)
const amapComponents = ref<AMapComponents>({
  map: null,
  marker: null,
  infoWindow: null,
  polyline: null
})

const mapLoaded = ref<boolean>(false)
const locationLoading = ref<boolean>(false)
const userLocation = ref<[number, number] | null>(null)
const locationError = ref<string | null>(null)

// 加载地图脚本
const loadAMapScript = async (): Promise<void> => {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve()
      return
    }
    
    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = 'https://webapi.amap.com/maps?v=2.0&key=5ee696eaddb2f84093a5721c8e505cf8'
    script.async = true
    script.defer = true
    
    script.onload = () => {
      if (typeof AMap !== 'undefined' && AMap.Map) {
        resolve()
      } else {
        reject(new Error('高德地图API加载失败'))
      }
    }
    
    script.onerror = (e) => {
      reject(new Error('加载地图脚本失败: ' + (e as Event).type))
    }
    
    document.head.appendChild(script)
  })
}

// 获取浏览器位置
const getBrowserLocation = (): Promise<[number, number]> => {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('浏览器不支持地理定位'))
      return
    }
    
    locationLoading.value = true
    locationError.value = null
    
    const success = (position: GeolocationPosition) => {
      const { latitude, longitude } = position.coords
      userLocation.value = [longitude, latitude]
      resolve([longitude, latitude])
    }
    
    const error = (err: GeolocationPositionError) => {
      let message = ''
      switch (err.code) {
        case err.PERMISSION_DENIED:
          message = '用户拒绝了位置请求'
          break
        case err.POSITION_UNAVAILABLE:
          message = '位置信息不可用'
          break
        case err.TIMEOUT:
          message = '位置请求超时'
          break
        default:
          message = '未知错误'
      }
      locationError.value = message
      reject(new Error(message))
    }
    
    const options = {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0
    }
    
    navigator.geolocation.getCurrentPosition(success, error, options)
  })
}

// 初始化地图
const initMap = async (): Promise<void> => {
  try {
    // 确保AMap脚本加载
    await loadAMapScript();
    
    // 等待DOM完全渲染
    await nextTick();
    
    // 检查地图容器是否存在
    if (!mapContainer.value) {
      throw new Error('地图容器元素不存在');
    }
    
    // 强制设置容器尺寸
    mapContainer.value.style.width = '100%';
    mapContainer.value.style.height = '100%';
    
    console.log('修复后地图容器尺寸:', 
      mapContainer.value.offsetWidth, 
      mapContainer.value.offsetHeight
    );
    
    // 获取位置中心点
    let center: [number, number];
    try {
      center = await getBrowserLocation();
      ElMessage.success('定位成功');
    } catch (err) {
      console.warn('使用默认中心点:', err);
      center = [116.397428, 39.90923]; // 天安门坐标
      ElMessage.warning('使用默认位置');
    } finally {
      locationLoading.value = false;
    }
    
    // 创建地图实例
    amapComponents.value.map = new AMap.Map(mapContainer.value, {
      zoom: 15,
      center: center,
      resizeEnable: true
    });
    
    // 添加标记
    amapComponents.value.marker = new AMap.Marker({
      position: center,
    });
    amapComponents.value.map.add(amapComponents.value.marker);
    
    // 强制重绘
    setTimeout(() => {
      (amapComponents.value.map as any)?.setFitView();
      // 再次检查尺寸
      console.log('地图初始化后容器尺寸:', 
        mapContainer.value?.offsetWidth, 
        mapContainer.value?.offsetHeight
      );
    }, 300);
    
    mapLoaded.value = true;
  } catch (error: any) {
    console.error('地图初始化失败:', error);
    ElMessage.error(`地图加载失败: ${error.message}`);
    locationError.value = error.message;
  }
};

// 清理资源
const cleanupMap = (): void => {
  try {
    if (amapComponents.value.map) {
      amapComponents.value.map.destroy()
      amapComponents.value = {
        map: null,
        marker: null,
        infoWindow: null,
        polyline: null
      }
    }
  } catch (error) {
    console.error('销毁地图失败:', error)
  }
}

onMounted(async () => {
  // 先加载用户信息
  async () => {
    try {
      const res = await reqUserShow(id.toString())
      if(res.code === 200) {
        userId.value = id
        username.value = res.data.username || '未设置用户名'
        email.value = res.data.email || '未设置邮箱'
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
  }
  
  // 等待DOM更新
  await nextTick();
  
  // 确保容器可见
  if (mapContainer.value) {
    mapContainer.value.style.display = 'block';
    mapContainer.value.style.visibility = 'visible';
  }
  
  // 初始化地图
  await initMap();
  console.log(AMap.Map.prototype)
  
  // 最终检查
  if (amapComponents.value.map) {
    setTimeout(() => {
      (amapComponents.value.map as any)?.setFitView();
    }, 500);
  }
})

onBeforeUnmount(() => {
  cleanupMap()
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