import request from "@/utils/request"
import { type aiChatForm , type aiResponseData} from '@/api/interface/type'

enum API {
    AICHAT_URL = "/questionSimple"
}
//对外暴露请求函数

export const reqAiChat = (data: aiChatForm) => request.post(API.AICHAT_URL, data) as Promise<aiResponseData>;