import request from "../utils/request";

class StudentAPI {
  /**
   * 获取当前登录用户信息
   *
   * @returns 登录用户昵称、头像信息，包括角色和权限
   */
  static getInfo() {
    return request<any, any>({
      url: "/api/student/info/get",
      method: "get",
    });
  }

  static getStudent(id: number) {
    return request<any, any>({
      url: "/api/student/get",
      method: "post",
      data: { id },
    });
  }

  static getPage(username: string, pageSize: number, pageNum: number) {
    return request<any, any>({
      url: "/api/student/page",
      method: "post",
      data: {
        username,
        pageNum,
        pageSize,
      },
    });
  }

  static save(student: any) {
    return request<any, any>({
      url: "/api/student/save",
      method: "post",
      data: student,
    });
  }

  static delete(student: any) {
    return request<any, any>({
      url: "/api/student/delete",
      method: "post",
      data: student,
    });
  }
}

export default StudentAPI;
