import { number } from "echarts"

export interface loginForm {
    username:string,
    password:string
    email: string,
}

export interface userLoginResponseData {
    code:number
    msg:string
    data:{
        id:number
        username:string
        email:string
        token:string
        sessionId:string
    }
}

export interface userRecordResponseData {
    code: number
    msg: string
    data: {
        flatMap(arg0: (session: { 文本对话: any[] }) => { username: any; content: any; createdAt: any; updatedAt: any }[]): { username: string; content: string; createdAt: string; updatedAt: string }[] | { username: string; content: string; createdAt: string; updatedAt: string }[]
        sessionId: string
        文本对话 : Array<{
            username: string
            content: string
            createdAt: string
            updatedAt: string
        }>
    }
}