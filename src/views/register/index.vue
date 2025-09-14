<template>
    <div class="register-container">
        <div class="register-illustration">
            <img src="@/assets/register.png" alt="register illustration" class="illustration-img"/>
        </div>
        <div class="register-form-container">
            <div class="form-header">
                <h2>创建账号</h2>
                <p>请填写您的注册信息</p>
            </div>
            <el-form
                ref="ruleFormRef"
                class="register-form"
                :model="ruleForm"
                :rules="rules"
                label-width="auto"
                @keyup.enter="registerHandler"
            >
                <el-form-item label="用户名" prop="name">
                    <el-input 
                        v-model="ruleForm.name" 
                        placeholder="3-16位字符"
                        :prefix-icon="User"
                    />
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                    <el-input 
                        v-model="ruleForm.email" 
                        type="email" 
                        placeholder="请输入有效邮箱"
                        :prefix-icon="Message"
                    />
                </el-form-item>

                <el-form-item label="密码" prop="password">
                    <el-input 
                        v-model="ruleForm.password" 
                        type="password" 
                        show-password
                        placeholder="6-16位密码"
                        :prefix-icon="Lock"
                    />
                </el-form-item>

                <el-form-item label="确认密码" prop="passwordConfirm">
                    <el-input 
                        v-model="ruleForm.passwordConfirm" 
                        type="password" 
                        show-password
                        placeholder="请再次输入密码"
                        :prefix-icon="Lock"
                    />
                </el-form-item>

                <div class="action-buttons">
                    <el-button 
                        class="register-btn"
                        :loading="isRegistering"
                        @click="registerHandler"
                    >
                        注 册
                    </el-button>
                    <div class="form-footer">
                        <el-text tag="span" class="footer-text">已有账号？</el-text>
                        <el-text tag="ins" class="login-link" @click="loginHandler">立即登录</el-text>
                    </div>
                </div>            
            </el-form>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/modules/user'
import { useRouter } from 'vue-router'
import type { registerForm } from '@/api/user/register/type'

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
const isRegistering = ref(false)
const router = useRouter()
const userStore = useUserStore()

const validatePassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== ruleForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = reactive<FormRules<RuleForm>>({
  name: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 16, message: '长度3-16个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '长度6-16个字符', trigger: 'blur' },
  ],
  passwordConfirm: [
    { validator: validatePassword, trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
})

const loginHandler = () => {
  router.push('/login')
}

const registerHandler = async () => {
  try {
    await ruleFormRef.value?.validate()
    isRegistering.value = true

    const registerForm :registerForm = {
      username: ruleForm.name,
      password: ruleForm.password,
      email: ruleForm.email
    }
    const flag =  await userStore.userRegister(registerForm)
    if(flag) router.push('/login')
    
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    isRegistering.value = false
  }
}
</script>

<style scoped lang="scss">
.register-container {
  display: flex;
  height: 100vh;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
  gap: 60px;
}

.register-illustration {
  width: 500px;
  height: 500px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
  }
}

.illustration-img {
  width: 90%;
  height: 90%;
  object-fit: contain;
  border-radius: 8px;
}

.register-form-container {
  width: 420px;
  padding: 40px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
  }
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
  
  h2 {
    color: #303133;
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  
  p {
    color: #909399;
    font-size: 14px;
  }
}

.register-form {
  width: 100%;
  
  :deep(.el-form-item__label) {
    color: #606266;
    font-weight: 500;
  }
  
  :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: 0 0 0 1px #dcdfe6;
    transition: all 0.3s;
    
    &:hover {
      box-shadow: 0 0 0 1px #c0c4cc;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 1px #7c3aed !important;
    }
  }
}

.action-buttons {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.register-btn {
  width: 100%;
  height: 44px;
  background: linear-gradient(135deg, #7c3aed 0%, #6d28d9 100%);
  border: none;
  color: white;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s;
  
  &:hover {
    background: linear-gradient(135deg, #6d28d9 0%, #5b21b6 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.form-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.footer-text {
  color: #909399;
  font-size: 14px;
}

.login-link {
  color: #7c3aed;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.3s;
  
  &:hover {
    color: #6d28d9;
    text-decoration: underline;
  }
}

@media (max-width: 992px) {
  .register-container {
    flex-direction: column;
    gap: 40px;
    padding: 40px 20px;
  }
  
  .register-illustration {
    width: 100%;
    max-width: 400px;
    height: auto;
    aspect-ratio: 1/1;
  }
  
  .register-form-container {
    width: 100%;
    max-width: 400px;
  }
}

@media (max-width: 576px) {
  .register-form-container {
    padding: 30px 20px;
  }
}
</style>