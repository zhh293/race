import { ref, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'

export interface WebSocketMessage {
  userId: number
  latitude: number
  longitude: number
  recordTime: string
}

export interface WebSocketResponse {
  trackId?: string
  status?: string
  recordTime?: string
  remark?: string
}

export class RealtimeLocationWS {
  private ws: WebSocket | null = null
  private sid: string
  private userId: number
  private wsUrl: string
  private retryCount = 0
  private maxRetryCount = 3
  private retryTimer: number | null = null
  private messageHandler: ((data: WebSocketResponse) => void) | null = null

  public connected = ref(false)

  constructor(userId: number, host: string, onMessage?: (data: WebSocketResponse) => void) {
    this.userId = userId
    this.sid = this.generateSessionId()
    this.wsUrl = `ws://${host}/realtime/${this.sid}`
    this.messageHandler = onMessage || null
    this.connect()
  }

  private generateSessionId(): string {
    return 'sid-' + Math.random().toString(36).substr(2, 9) + Date.now().toString(36)
  }

  public connect(): void {
    if (this.connected.value) return

    try {
      this.ws = new WebSocket(this.wsUrl)

      this.ws.onopen = () => {
        this.connected.value = true
        this.retryCount = 0
        console.log('WebSocket连接已建立')
      }

      this.ws.onmessage = (event) => {
        try {
          const response: WebSocketResponse = JSON.parse(event.data)
          console.log('收到服务端响应:', response)
          this.messageHandler?.(response)
        } catch (error) {
          console.error('解析WebSocket消息失败:', error)
        }
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket错误:', error)
        this.reconnect()
      }

      this.ws.onclose = () => {
        this.connected.value = false
        console.log('WebSocket连接已关闭')
        this.reconnect()
      }
    } catch (error) {
      console.error('WebSocket初始化失败:', error)
      this.reconnect()
    }
  }

  private reconnect(): void {
    if (this.retryCount >= this.maxRetryCount) {
      console.error(`已达到最大重试次数(${this.maxRetryCount})，停止重连`)
      ElMessage.warning('位置服务连接失败，请刷新页面重试')
      return
    }

    this.retryCount++
    const delay = Math.min(1000 * 2 ** this.retryCount, 30000) // 指数退避，最大30秒

    console.log(`将在${delay}ms后尝试第${this.retryCount}次重连...`)
    this.retryTimer = window.setTimeout(() => {
      this.connect()
    }, delay)
  }

  public sendLocation(position: [number, number]): boolean {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      console.warn('WebSocket未连接，无法发送位置数据')
      return false
    }

    const [longitude, latitude] = position
    const message: WebSocketMessage = {
      userId: this.userId,
      latitude,
      longitude,
      recordTime: new Date().toISOString().split('.')[0] // ISO8601格式，去掉毫秒部分
    }

    try {
      this.ws.send(JSON.stringify(message))
      console.log('已发送位置数据:', message)
      return true
    } catch (error) {
      console.error('发送位置数据失败:', error)
      return false
    }
  }

  public close(): void {
    if (this.retryTimer) {
      clearTimeout(this.retryTimer)
      this.retryTimer = null
    }

    if (this.ws) {
      this.ws.close()
      this.ws = null
    }

    this.connected.value = false
  }
}

// 导出一个创建WebSocket实例的函数
export function useRealtimeLocationWS(userId: number, host: string, onMessage?: (data: WebSocketResponse) => void) {
  const wsInstance = new RealtimeLocationWS(userId, host, onMessage)

  // 自动在组件卸载时关闭连接
  onBeforeUnmount(() => {
    wsInstance.close()
  })

  return wsInstance
}