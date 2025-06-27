import {
	post
} from "./request";

export function loginApi(username, password) {
	return post('/api/student/login', {
		username,
		password
	})
}