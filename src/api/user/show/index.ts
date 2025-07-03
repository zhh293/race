import { type UserShowResponse } from "./type";
import request from "@/utils/request";
import { type UserUploadForm , type UserUploadResponse} from "./type";
import { type UserUpdateForm, type UserUpdateResponse } from "./type";

enum API {
    USER_SHOW_URL = "/user/show",
    USER_UPLOAD_URL = "/common/upload",
    USER_UPDATE_URL = "/user/update"
}

// 获取用户信息接口方法
export const reqUserShow = (id: string) => request.post(`${API.USER_SHOW_URL}/${id}`) as Promise<UserShowResponse>;
export const reqUserUpload = (data: UserUploadForm) => request.post(API.USER_UPLOAD_URL, data) as Promise<UserUploadResponse>;
export const reqUserUpdate = (data: UserUpdateForm) => request.post(API.USER_UPDATE_URL, data) as Promise<UserUpdateResponse>;