<template>
    <div class="placeHolder" style="display: flex; height: 100vh; width: 100%; align-items: center; justify-items: center;">
        <div class="imgPlaceHolder" style="display: flex; width: 500px; height: 50%; background-color: pink; border-radius: 10px; align-items: center; justify-content: center; justify-items: center; margin-left: 15%;">
            <img src="@/assets/login.png" alt="login" style="display: flex; width: 98%; height: 98%; border-radius: 10px; align-items: center; justify-content: center; justify-items: center;"/>
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
                <ValidCodeComponent v-model="captcha" style="display: flex; background-color: paleturquoise; margin-top: 3px; margin-left: 70%;"/>
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
import { reactive, ref, type Component } from 'vue'
import { RowAlign, type FormInstance, type FormRules } from 'element-plus'
import ValidCodeComponent from '@/components/validCode.vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import request from '@/utils/request'
import type { loginForm, userResponseData } from '@/api/user/type'
import { reqLogin } from '@/api/user'


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

const validCode = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请输入验证码'))
  } else if (value !== captcha.value) {
    callback(new Error('验证码错误'))
    console.log(value, captcha.value)
  } else {
    callback()
  }
}
const rules = reactive<FormRules<RuleForm>>({
  name: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: 'Length should be 6 to 16', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' },
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
    }
  })

  const loginData: loginForm = {
    username: ruleForm.name,
    password: ruleForm.password,
    email: ruleForm.email,
  }

  try {
    const response: userResponseData = await reqLogin(loginData);
    if (response.code === 200) {
      // 验证验证码
      router.push('/')
      console.log(response.message)
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
  