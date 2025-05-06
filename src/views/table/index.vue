<template>
    <div class="common-layout" style="display: flex; height: 100vh; width: 100%;">
        
      <el-container>

        <el-container>
          <el-aside width="15%">
            <el-radio-group v-model="isCollapse" style="margin-bottom: 20px;">
                <el-radio-button :value="false">打开侧边栏</el-radio-button>
                <el-radio-button :value="true">收缩侧边栏</el-radio-button>
            </el-radio-group>
            <el-menu
                default-active="2"
                class="el-menu-vertical-demo"
                :collapse="isCollapse"
                @open="handleOpen"
                @close="handleClose"
            >
                <el-sub-menu index="1">
                    <template #title>
                        <el-icon><ChatRound /></el-icon>
                        <span>问问AI</span>
                    </template>
                    <el-menu-item-group>
                        <el-menu-item index="1-1">蓝心小V</el-menu-item>
                        <el-menu-item index="1-2">AI原子能力</el-menu-item>
                        <el-menu-item index="1-3">AI语音研究实验室</el-menu-item>
                    </el-menu-item-group>
                </el-sub-menu>

                <el-menu-item index="2">
                    <el-icon><Memo /></el-icon>
                    <template #title>我的计划表</template>
                </el-menu-item>

                <el-sub-menu index="3">
                    <template #title>
                        <el-icon><UserFilled /></el-icon>
                        <span>个人中心</span>
                    </template>
                    <el-menu-item-group>
                        <el-menu-item index="3-1">个人信息</el-menu-item>
                        <el-menu-item index="3-2">使用情况</el-menu-item>
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
                        <span style="display: flex; margin-right: 8%;">
                            <el-icon><FullScreen /></el-icon>
                        </span>
                        <el-avatar src=""> user </el-avatar>
                    </span>      
                </div>
            </el-header>

            
            <div ref="chartRef" style="display: flex; width: 100%; height: 90%; justify-content: left; margin-left: 0; margin-bottom: 0; margin-top: 3%;"></div>

          </el-main>
        </el-container>

      </el-container>
    </div>
</template>

<script lang="ts" setup>
import { nextTick, onMounted, ref } from 'vue'
import { getTimeState } from '@/utils/index'
import * as echarts from 'echarts'

  const isCollapse = ref(true)
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


const chartRef = ref(null);

onMounted(() => {
    nextTick(() => {
        if (chartRef.value) {
            try {
                const myChart = echarts.init(chartRef.value);
                const option = {
                    toolbox: {
                        show: true,
                        feature: {
                            dataView: {
                                show: true,
                                readOnly: true,
                                optionToContent: function (opt: any) {
                                    const axisData = opt.xAxis[0].data;
                                    const series = opt.series;
                                    let table = '<table style="width:100%;text-align:center; margin: 0 auto; border-collapse: collapse; border: 1px solid #000;"><tbody><tr style="border: 1px solid #000; align-items: center; align-content: center;">' + '<td style="border: 1px solid #000;">项目</td>';
                                    series.forEach(function (item: any) {
                                        table += '<td style="border: 1px solid #000;">' + item.name + '</td>';
                                    });
                                    table += '</tr>';

                                    for (let i = 0, l = axisData.length; i < l; i++) {
                                        table += '<tr style="border: 1px solid #000;">'
                                            + '<td style="border: 1px solid #000;">' + axisData[i] + '</td>';
                                        for (let j = 0; j < series.length; j++) {
                                            table += '<td style="border: 1px solid #000;">' + series[j].data[i] + '</td>';
                                        }
                                        table += '</tr>';
                                    }
                                    table += '</tbody></table>';
                                    return table;
                                }
                            },
                            saveAsImage: {
                                show: true
                            }
                        }
                    },
                    xAxis: {
                        type: 'category',
                        data: ['运动', '练习口语', '高数', '概统', '吃饭', '游戏', '网课']
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            name: '时间',
                            type: 'bar',
                            data: [1, 0.5, 3, 2, 1, 0.5, 1.5]
                        }
                    ]
                };

                myChart.setOption(option);
            } catch (error) {
                console.error('初始化 ECharts 时出错:', error);
            }
        }
    });
});
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