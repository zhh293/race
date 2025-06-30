import { type UserShowResponse } from "./type";
import request from "@/utils/request";
import { type UserUpdateForm , type UserUpdateResponse} from "./type";

enum API {
    USER_SHOW_URL = "/user/show",
    USER_UPDATE_URL = "/common/upload"
}

// 获取用户信息接口方法
export const reqUserShow = (id: string) => request.post(`${API.USER_SHOW_URL}/${id}`) as Promise<UserShowResponse>;
export const reqUserUpdate = (data: UserUpdateForm) => request.post(API.USER_UPDATE_URL, data) as Promise<UserUpdateResponse>;