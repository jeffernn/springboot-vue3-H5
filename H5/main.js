import App from './App'
import {
	initRequest
} from './http/index.js'
// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
	...App
})

app.$mount()
// #endif

// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
import uviewPlus from '@/uni_modules/uview-plus'

export function createApp() {
	const app = createSSRApp(App)
	app.use(uviewPlus)
	initRequest(app)
	return {
		app
	}
}
// #endif