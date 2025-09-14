import request from "@/utils/request"
import { type loginForm, type userLoginResponseData, type userRecordResponseData } from "./type"
import { useUserStore } from "@/stores/modules/user"

enum API {
    LOGIN_URL = "/user/login",
    RECORD_URL = "/user/queryRecord"
}

export const reqLogin = (data: loginForm) => request.post(API.LOGIN_URL,data) as Promise<userLoginResponseData> 

export const reqRecordGet = (id: number) => request.post(`${API.RECORD_URL}?userId=${id}`) as Promise<userRecordResponseData>