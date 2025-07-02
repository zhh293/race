<template>
  <div class="amap-wrapper">
    <div ref="mapContainer" class="map-container"></div>
    <div v-if="loading" class="map-mask">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>{{ loadingText }}</span>
    </div>
    <div v-if="error" class="map-mask">
      <el-alert :title="error" type="error" show-icon />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { useRealtimeLocationWS } from '@/utils/ws'

interface Position {
  lng: number
  lat: number
}

const props = withDefaults(defineProps<{
  userId: number
  wsHost?: string
  zoom?: number
  loadingText?: string
  showMarker?: boolean
}>(), {
  zoom: 15,
  loadingText: '正在加载地图...',
  showMarker: true,
  wsHost: import.meta.env.VITE_WS_HOST || 'localhost:8080'
})

const emit = defineEmits<{
  (e: 'position-update', position: Position): void
  (e: 'ws-message', message: any): void
}>()

const mapContainer = ref<HTMLElement | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
let map: AMap.Map | null = null
let marker: AMap.Marker | null = null
let watchId: number | null = null

// 初始化WebSocket
const locationWS = useRealtimeLocationWS(
  props.userId, 
  props.wsHost,
  (msg) => emit('ws-message', msg)
)

// 加载地图脚本
const loadAMapScript = (): Promise<void> => {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve()
      return
    }
    
    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${import.meta.env.VITE_AMAP_KEY || 'YOUR_AMAP_KEY'}`
    script.async = true
    
    script.onload = () => resolve()
    script.onerror = () => reject(new Error('加载地图脚本失败'))
    
    document.head.appendChild(script)
  })
}

// 初始化地图
const initMap = async (): Promise<void> => {
  try {
    await loadAMapScript()
    
    if (!mapContainer.value) {
      throw new Error('地图容器不存在')
    }

    map = new AMap.Map(mapContainer.value, {
      zoom: props.zoom,
      center: [116.397428, 39.90923], // 默认天安门坐标
      resizeEnable: true
    })

    if (props.showMarker) {
      marker = new AMap.Marker({
        position: [116.397428, 39.90923],
      })
      map.add(marker)
    }

    startWatchingLocation()
    loading.value = false
  } catch (err) {
    error.value = err instanceof Error ? err.message : '地图初始化失败'
    ElMessage.error(error.value)
  }
}

// 开始监听位置
const startWatchingLocation = () => {
  if (!navigator.geolocation) {
    error.value = '浏览器不支持地理定位'
    return
  }
  
  watchId = navigator.geolocation.watchPosition(
    (position) => updatePosition(position),
    (err) => {
      error.value = `定位错误: ${err.message}`
      ElMessage.error(error.value)
    },
    { maximumAge: 0, timeout: 10000 }
  )
}

// 更新位置
const updatePosition = (position: GeolocationPosition) => {
  if (!map) return
  
  const newPos: [number, number] = [
    position.coords.longitude,
    position.coords.latitude
  ]
  
  if (marker) marker.setPosition(newPos)
  map.setCenter(newPos)
  
  emit('position-update', { lng: newPos[0], lat: newPos[1] })
  locationWS.sendLocation(newPos)
}

// 清理资源
const cleanup = () => {
  if (watchId) navigator.geolocation.clearWatch(watchId)
  if (map) map.destroy()
}

onMounted(() => initMap())
onBeforeUnmount(() => cleanup())

// 暴露方法给父组件
defineExpose({
  getMap: () => map,
  getMarker: () => marker
})
</script>

<style scoped>
.amap-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 300px;
}

.map-container {
  width: 100%;
  height: 100%;
}

.map-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  z-index: 999;
  flex-direction: column;
  gap: 10px;
}
</style>