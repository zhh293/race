//AI接口参数
export interface aiChatForm {
    question: string,
    userId: number
}

export interface aiTableForm {
    picture: string
    userId: string
}

export interface aiResponseData {
    code: number
    msg: string
    data: {
        Respond: string
    }
}

export interface aiResponsePictureData {
    
}