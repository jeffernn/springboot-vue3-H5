import {
	http,
	toast
} from '@/uni_modules/uview-plus'

export function get(url : string, param : any) : Promise<any> {
	return http.get(url, {
		params: param
	})
}

export function post(url : string, data : any) : Promise<any> {
	return http.post(url, data)
}

export function upload(url : string, filePath : string) : Promise<any> {
	return http.upload(url, {
		filePath
	})
}

export function download(url : string) : Promise<any> {
	return http.download(url)
}

const requestInterceptors = (vm : any) => {
	/**
	 * 请求拦截
	 * @param {Object} http
	 */
	http.interceptors.request.use((config) => { // 可使用async await 做异步操作
		config.data = config.data || {}
		const token = uni.getStorageSync('accessToken')
		const tokenType = uni.getStorageSync('tokenType')
		config.header = config.header || {}
		config.header.Authorization = tokenType + ' ' + token
		return config
	}, (config) => // 可使用async await 做异步操作
		Promise.reject(config))
}

const responseInterceptors = (vm : any) => {
	/**
	 * 响应拦截
	 * @param {Object} http 
	 */
	http.interceptors.response.use((response) => {
		/* 对响应成功做点什么 可使用async await 做异步操作*/
		const data = response.data
		// console.log('http', data)
		// 自定义参数
		const custom = response.config?.custom
		if (data.code !== 0) { // 服务端返回的状态码不等于200，则reject()
			// 如果没有显式定义custom的toast参数为false的话，默认对报错进行toast弹出提示
			if (custom.toast !== false) {
				toast(data.msg)
			}
			// 如果需要catch返回，则进行reject
			if (custom?.catch) {
				return Promise.reject(data)
			} else {
				// 否则返回一个pending中的promise
				return new Promise(() => { })
			}
		}
		return data.data || {}
	}, (response) => {
		/*  对响应错误做点什么 （statusCode !== 200）*/
		return Promise.reject(response)
	})
}

export {
	requestInterceptors,
	responseInterceptors
}