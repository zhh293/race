import request from "@/utils/request"
import { type loginForm, type userLoginResponseData } from "./type"


const LOGIN_URL = "/user/login"


export const reqLogin = (data: loginForm) => request.post(LOGIN_URL,data) as Promise<userLoginResponseData> 