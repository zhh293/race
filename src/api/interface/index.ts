import request from "@/utils/request"
import { type aiChatForm , type aiTableForm, type aiResponseData} from '@/api/interface/type'

enum API {
    AICHAT_URL = "/user/questionSimple",
    AIPICTURE_URL = "/user/picture"
}
//对外暴露请求函数

export const reqAiChat = (data: aiChatForm) => request.post(API.AICHAT_URL, data) as Promise<aiResponseData>;
export const reqAitable = (data: aiTableForm) => request.post(API.AIPICTURE_URL, data) as Promise<aiResponseData>