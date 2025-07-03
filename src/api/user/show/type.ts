export interface UserShowResponse {
    code: number
    message: string
    data: {
        username: string
        email: string
        avatarImageUrl: string
        password: string
        weatherImageUrl: string
    }
}

export interface UserUpdateForm {
    username: string
    password: string
    email: string
    avatarImageUrl: string
}

export interface UserUpdateResponse {
    code: number
    msg: string
}