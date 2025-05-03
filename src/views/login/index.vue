<template>
    <div class="placeHolder" style="display: flex; height: 100vh; width: 100%; align-items: center; justify-items: center;">
        <div class="imgPlaceHolder" style="display: flex; width: 500px; height: 50%; background-color: pink; border-radius: 10px; align-items: center; justify-content: center; justify-items: center; margin-left: 15%;">
            <img src="" alt="login">
        </div>
        <div class="formPlaceHolder" style="display: flex; width: 500px; margin-right: 15%;">
            <el-form
                ref="ruleFormRef"
                style="max-width: 600px"
                :model="ruleForm"
                :rules="rules"
                label-width="auto"
            >
                <el-form-item label="用户名" prop="name">
                <el-input v-model="ruleForm.name"/>
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                <el-input v-model="ruleForm.email" type="email" autocomplete="off"/>
                </el-form-item>

                <el-form-item label="密码" prop="password">
                <el-input v-model="ruleForm.password" type="password" autocomplete="off" show-password/>
                </el-form-item>

                <el-form-item label="验证码" prop="validCode">
                <el-input v-model="ruleForm.validCode"/> 
                <ValidCode v-model:validCode="captcha" style="display: flex; background-color: paleturquoise; margin-top: 3px; margin-left: 70%;"/>
                </el-form-item>

                <div style="display: flex; margin-top: 8px; justify-content: center;">
                  <el-button color="purple" type="primary"  plain round @click="loginHandler"> <div>登录</div> </el-button>
                  <el-text tag="ins" shadow="hover" @click="registerHandler">还没有账号？点击注册</el-text>
                </div>            

            </el-form>


         

        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { RowAlign, type FormInstance, type FormRules } from 'element-plus'
import ValidCode from '@/components/vaildCode.vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import request from '@/utils/request'


interface RuleForm {
  name: string
  password: string
  email: string
  validCode: string
}

const ruleFormRef = ref<FormInstance>()
const ruleForm = reactive<RuleForm>({
  name: '',
  password: '',
  email: '',
  validCode: '',
})
const captcha = ref('')

const validCode = (rule,value, callback) => {
  if (value === '') {
    callback(new Error('请输入验证码'))
  } else if (value !== captcha.value) {
    callback(new Error('验证码错误'))
  } else {
    callback()
  }
}

const rules = reactive<FormRules<RuleForm>>({
  name: [
    { required: true, message: 'Please input 用户名', trigger: 'blur' },
    { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please input 密码', trigger: 'blur' },
    { min: 6, max: 16, message: 'Length should be 6 to 16', trigger: 'blur' },
  ],
  email: [
    { required: true, message: 'Please input 邮箱', trigger: 'blur' },
    { min: 1, max: 20, message: '请输入正确的邮箱', trigger: 'blur' },
  ],
  validCode: [
    {validator: validCode, trigger: 'blur'}
  ]
})


const router = useRouter()

const registerHandler = () => {
  router.push('/register')
}

const loginHandler = async () => {
  if (!ruleFormRef.value) return

  // 表单验证
  await ruleFormRef.value.validate((valid) => {
    if (!valid) {
      console.log('验证失败')
      return false
    }
  })

  const loginData: loginForm = {
    username: ruleForm.name,
    password: ruleForm.password
  };

  try {
    const response: userResponseData = await reqLogin(loginData);
    if (response.code === 200) {
      // 验证验证码
      if (ruleForm.validCode === captcha.value) {
        localStorage.setItem('token', response.data.token);
        router.push('/home');
      } else {
        alert('验证码错误，请重新输入');
      }
    } else {
      alert('用户名或密码错误');
    }
  } catch (error) {
    console.error('登录失败:', error);
    alert('登录失败，请重试');
  }
}

</script>
  
<style scoped>
.imgPlaceHolder {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}
.formPlaceHolder {
  margin-left: 2px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  display: flex;
  align-content: center;
  justify-content: center;
  height: 50%;
  background-color: rgb(255, 213, 220);
}
.placeHolder {
  background-color: bisque;
}
.el-form {
  align-content: center;
}
</style>
  