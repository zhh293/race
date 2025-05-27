//AI接口参数
export interface aiChatForm {
    question: string,
    userId: number
}

export interface aiResponseData {
    code: number,
    Respond: string
}