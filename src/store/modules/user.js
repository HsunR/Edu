import router from "@/router";
import api from '@/temp/index';
const { userController,authController } = api;

import { getToken, setToken, removeToken } from "@/utils/auth";
import {ElMessage} from "element-plus";

const useUserStore = defineStore("user", {
  state: () => ({
    token: getToken(),
    userId: "",
    username: "",
    roles: [],
    permissions: [],
    userInfo: {},
    loginUserInfo: {}
  }),
  actions: {
    // 登录
      async login(userInfo) {
          try {
              const res = await authController.login(userInfo)
              if (res.data.data) {
                  ElMessage.success("登录成功");
              } else{
                  ElMessage.error("登录失败：",res.data.message);
              }
              setToken(res.data.data.accessToken);
              this.token = res.data.data?.accessToken;
              this.userId = res.data.data?.userId;
              this.username = res.data.data?.username;
              localStorage.setItem("userId", res.data.data.userId);

              this.getInfo(res.data.data.userId);
              this.getLoginUser()
          } catch (e){
              console.error(e)
          }
      },
      // 获取用户信息
      async getInfo() {
          try {
              const userId = localStorage.getItem("userId");

              const res = await userController.getUserInfo(userId)
              const user = res.data.data;
              this.userId = user.userId;
              this.userInfo = user

              return res
          } catch (error) {
              console.error(error)
          }
      },
      async getLoginUser() {
          try {
              const res = await userController.getUserMe()
              const user = res.data.data;
              // if (res.roles && res.roles.length > 0) {
              //   // 验证返回的roles是否是一个非空数组
              //   this.roles = res.roles;
              //   this.permissions = res.permissions;
              // } else {
              //   this.roles = ["ROLE_DEFAULT"];
              // }
              this.loginUserInfo = user

              return res
          } catch (error) {
              console.error(error)
          }
      },
    // 退出系统
    logOut() {
      return new Promise((resolve, reject) => {
          authController.logout()
          .then(() => {
            this.token = "";
            this.userId = "";
            this.roles = [];
            this.permissions = [];
            localStorage.removeItem("userId");
            // localStorage.removeItem("headPortrait");
            removeToken();
            resolve();
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
  },
});

export default useUserStore;
