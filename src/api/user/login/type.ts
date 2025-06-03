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
    }
}