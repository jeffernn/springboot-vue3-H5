import request from "@/utils/request";

class UserAPI {
  /**
   * 获取当前登录用户信息
   *
   * @returns 登录用户昵称、头像信息，包括角色和权限
   */
  static getInfo() {
    return request<any, UserInfo>({
      url: "/api/user/info/get",
      method: "get",
    });
  }

  static getUser(id: number) {
    return request<any, any>({
      url: "/api/user/get",
      method: "post",
      data: { id },
    });
  }

  static getPage(username: string, pageSize: number, pageNum: number) {
    return request<any, any>({
      url: "/api/user/page",
      method: "post",
      data: {
        username,
        pageNum,
        pageSize,
      },
    });
  }

  static saveUser(user: any) {
    return request<any, any>({
      url: "/api/user/save",
      method: "post",
      data: user,
    });
  }

  static deleteUser(user: any) {
    return request<any, any>({
      url: "/api/user/delete",
      method: "post",
      data: user,
    });
  }
}

export default UserAPI;

/** 登录用户信息 */
export interface UserInfo {
  /** 用户ID */
  userId?: number;

  /** 用户名 */
  username?: string;

  /** 昵称 */
  nickname?: string;

  /** 头像URL */
  avatar?: string;

  /** 角色 */
  roles: string[];

  /** 权限 */
  perms: string[];
}
