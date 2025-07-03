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

            <div style="display: flex; width: 100%; height: 100%; align-items: flex-end;">
                <div style=" width: 80%; padding-bottom: 0; margin-bottom: 10%; height: 50%; margin-right: 10%;">
                    <div style="display: flex; margin-bottom: 2%; width: 100%;" v-show="isShow">
                        <span style="display: flex; width: 100%;">
                            <el-card style="display: flex; margin-left: auto; align-items: self-end;">
                                <p style="display: flex; width: auto;">发送了一条音频</p>
                            </el-card>
                            <el-avatar style="margin-left: 1%"> user </el-avatar>
                        </span>
                    </div>

                    <div style="display: flex; width: 100%; margin-bottom: 2%;" v-show="isShow">
                        <span style="display: flex;">
                            <el-avatar> AI </el-avatar>
                            <el-card>
                                <p>{{ backThing }}</p>
                            </el-card>
                        </span>
                    </div>
                    
                    
                    <el-card shadow="hover" style="margin-bottom: 5%;">
                        <div class="audio-info" v-if="audioBlob" style="padding: 10px;" v-show="!isShow">
                            <p>录音时长: {{ duration }}秒</p>
                            <p>文件大小: {{ fileSize }}KB</p>
                            <audio controls v-if="wavUrl" :src="wavUrl"></audio>
                        </div>
                        <div v-else class="empty-state">
                            <el-icon><Headset /></el-icon>
                            <span>暂无录音内容</span>
                        </div>
                    </el-card>

                    <div style="display: flex; width: 100%; justify-content: right; border-color: black; border-width: 5px;">
                        <el-button :disabled="isRecording" size="large" style="display: flex; margin-left: 1%; margin-bottom: 1%;" type="primary" plain round @click="recordClick1">
                            <el-icon style="color: blue;"><ChatLineRound />点我录制</el-icon>
                        </el-button>
                        <el-button :disabled="!isRecording" size="large" style="display: flex; margin-left: 2%; margin-bottom: 1%;" type="primary" plain round @click="recordClick2">
                            <el-icon style="color: blue;"><ChatLineRound />点我停止</el-icon>
                        </el-button>
                        <el-button :disabled="!audioBlob || isRecording"  size="large" style="display: flex; margin-right: 2%; margin-bottom: 1%;" type="primary" plain round @click="recordClick3">
                            <el-icon style="color: blue;"><ChatLineRound />点我重录</el-icon>
                        </el-button>
                        <el-button :disabled="!audioBlob" style="display: flex; margin-right: 5%;" type="primary" plain round @click="submitClick">
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
import { onMounted, ref, computed, onUnmounted } from 'vue'
import type { Ref } from 'vue'
import { getTimeState } from '@/utils/index'
import { ElMessage } from 'element-plus'
import RecordRTC from 'recordrtc';
import { useUserStore } from '@/stores/modules/user';
import { useRouter } from 'vue-router'
import axios from 'axios';

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

const userStore = useUserStore()

const isRecording = ref(false);
const audioBlob = ref<Blob | null>(null);
const wavUrl: Ref<string | null> = ref(null);
const recorder = ref<RecordRTC | null>(null);
const audioStream = ref<MediaStream | null>(null);
const startTime = ref(0);
const duration = computed(() => {
  if (!startTime.value || !audioBlob.value) return 0;
  return ((Date.now() - startTime.value) / 1000).toFixed(1);
});
const fileSize = computed(() => {
  if (!audioBlob.value) return 0;
  return (audioBlob.value.size / 1024).toFixed(1);
});

// 清理资源
const cleanupResources = () => {
  if (recorder.value) {
    recorder.value.destroy()
    recorder.value = null
  }
  
  if (audioStream.value) {
    audioStream.value.getTracks().forEach(track => track.stop())
    audioStream.value = null
  }
  
  if (wavUrl.value) {
    URL.revokeObjectURL(wavUrl.value)
    wavUrl.value = null
  }
}

const backThing = ref();
const isShow = ref(false);
const recordClick1 = async () => {
    ElMessage.success("开始录音")
    isShow.value = false;
    audioBlob.value = null
    // 这里可以添加录音逻辑
     try {
    const stream = await navigator.mediaDevices.getUserMedia({
      audio: {
        echoCancellation: false,
        noiseSuppression: false,
        autoGainControl: false,
        sampleRate: 16000,
        channelCount: 1
      } 
    })
    
    audioStream.value = stream
    startTime.value = Date.now()

    recorder.value = new RecordRTC(stream, {
      type: 'audio',
      mimeType: 'audio/wav',
      recorderType: RecordRTC.StereoAudioRecorder,
      desiredSampRate: 16000,
      numberOfAudioChannels: 1,
      bufferSize: 4096,
      disableLogs: true,
      audioBitsPerSecond: 256000
    })
    
    recorder.value.startRecording()
    isRecording.value = true
    ElMessage.success("录音已开始")
    
  } catch (error) {
    console.error("录音失败:", error)
    ElMessage.error("无法访问麦克风，请检查权限设置")
    cleanupResources()
  }
}

const recordClick2 = async() => {
    ElMessage.success("停止录音")
    // 这里可以添加停止录音逻辑
    if (!recorder.value || !isRecording.value) {
        ElMessage.warning("请先开始录音");
        return;
    }       
    recorder.value.stopRecording(() => {
        if (recorder.value) {
            audioBlob.value = recorder.value.getBlob();
            wavUrl.value = URL.createObjectURL(audioBlob.value);
        }
        isRecording.value = false;
        ElMessage.success("录音已停止，请上传音频");
    
    // 关闭音频流
    if (audioStream.value) {
      audioStream.value.getTracks().forEach(track => track.stop());
    }
  });
}

const recordClick3 = () => {
  if (wavUrl.value) {
    URL.revokeObjectURL(wavUrl.value)
    wavUrl.value = null
  }
  audioBlob.value = null
  isShow.value = false
  ElMessage.info("已重置录音状态")
}

const submitClick = async() => {
    if (!audioBlob.value) {
        ElMessage.warning("请先录制音频");
        return;
    }

    // 这里可以添加上传音频的逻辑
    isShow.value = true;
    try {
    const formData = new FormData();
    formData.append('audioFile', audioBlob.value, 'voice.wav'); 
    formData.append('sessionId', userStore.sessionId || '');
    formData.append('token', userStore.token || '');

    // 使用axios发送请求
    const response = await axios.post(
      'http://localhost:8080/wordAudio/handleAudioFile',
      {
        headers: {
          'Content-Type': 'multipart/form-data',
          formData,
        },
        timeout: 30000 
      }
    )
    
    if (response.data.code === 200) {
      backThing.value = response.data.result || '处理完成'
      ElMessage.success("音频处理成功")
    } else {
      backThing.value = response.data.msg || '处理失败'
      ElMessage.error(response.data.msg || '音频处理失败')
    }
  } catch (error) {
    console.error("提交失败:", error)
    backThing.value = '处理失败'
    ElMessage.error("提交失败，请重试")
  } finally {

  }
}

// 组件卸载时清理资源
onUnmounted(() => {
  cleanupResources()
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
</style>