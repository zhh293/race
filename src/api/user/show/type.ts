export interface UserShowResponse {
    code: number
    message: string
    data: {
        username: string
        email: string
        imageUrl: string
        weatherImageUrl: string
    }
}

export interface UserUpdateForm {
    userId: number
    file: File
} 

export interface UserUpdateResponse {
    code: number
    message: string
    data: string
}