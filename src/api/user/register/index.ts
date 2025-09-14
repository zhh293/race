//用户相关接口
import request from "@/utils/request";
import { type registerForm ,type userRegisterResponseData} from '@/api/user/register/type';

//统一管理接口
enum API{
    REGISTER_URL = "/user/register"
}
//对外暴露请求函数

//注册用户信息接口方法
export const reqRegister = (data: registerForm) => request.post(API.REGISTER_URL, data) as Promise<userRegisterResponseData>;