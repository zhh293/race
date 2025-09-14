//AI接口参数
export interface aiChatForm {
    question: string,
    userId: string,
    sessionId: string,
    token: string
}

export interface aiTableForm {
    picture: string
    userId: string
    sessionId: string
    token: string
}

export interface aiResponseData {
    code: number
    msg: string
    data: {
        Respond: string
    }
}

export interface aiResponsePictureData {
    code: number
    msg: string
    data: string
}

