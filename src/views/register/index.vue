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

                <el-form-item label="确认密码" prop="passwordConfirm">
                <el-input v-model="ruleForm.passwordConfirm" type="password" autocomplete="off" show-password/>
                </el-form-item>

                <div style="display: flex; margin-top: 8px; justify-content: center;">
                  <el-button color="purple" type="primary"  plain round @click="registerHandler"> <div>注册</div> </el-button>
                  <el-text tag="ins" shadow="hover" @click="loginHandler">已有账号？点击登录</el-text>
                </div>            

            </el-form>


         

        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { RowAlign, type FormInstance, type FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'
import request from '@/utils/request'

interface RuleForm {
  name: string
  email: string
  password: string
  passwordConfirm: string
}

const ruleFormRef = ref<FormInstance>()
const ruleForm = reactive<RuleForm>({
  name: '',
  email: '',
  password: '',
  passwordConfirm: '',
})

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'));
  } else if (value !== ruleForm.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
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
  passwordConfirm: [
    { validator: validatePassword, trigger: 'blur' },
  ],
  email: [
    { required: true, message: 'Please input 邮箱', trigger: 'blur' },
    { min: 1, max: 20, message: '请输入正确的邮箱', trigger: 'blur' },
  ],

})

const router = useRouter()
const loginHandler = () => {
  router.push('/login')
}
const registerHandler = async () => {
  if (!ruleFormRef.value) return

  // 表单验证
  await ruleFormRef.value.validate((valid) => {
    if (!valid) {
      console.log('验证失败')
      return false
    }
  })

  try {
    const response = await request.post('/user/register', ruleForm);
    if (response.code === 200) {
      localStorage.setItem('token', response.data.token)
      alert('注册成功，请登录')
      router.push('/login')
    } else {
      alert(response.msg)
    }
  } catch (error) {
    console.error('注册失败:', error);
    alert('注册失败，请重试');
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
  