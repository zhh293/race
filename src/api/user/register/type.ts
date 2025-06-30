//登录接口参数
export interface registerForm{
    username:string,
    password:string,
    email: string
}

//登录，注册返回数据类型
export interface userRegisterResponseData {
    code:number,
    msg:string
    data: string
}