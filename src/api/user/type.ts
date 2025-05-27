//登录接口参数
export interface loginForm{
    username:string,
    password:string
    email: string,
}

//登录，注册返回数据类型
export interface userResponseData {
    code:number,
    msg:string
}