import router from "@/router";
// import api from '@/services/user/user/index.js';
import api from '@/temp/index';
const { userController,authController } = api;


import { getToken, setToken, removeToken } from "@/utils/auth";

const useUserStore = defineStore("user", {
  state: () => ({
    token: getToken(),
    userId: "",
    name: "",
    headPortrait: "",
    email: "",
    mobile: "",
    roles: [],
    permissions: [],
  }),
  actions: {
    // 登录
    login(userInfo) {
      return new Promise((resolve, reject) => {
          authController.login(userInfo)
          .then((res) => {
            console.log("登录");
            console.log(res);
            console.log(res.data.data.userId);
            if (res.data.data) {
              ElMessage.success("登录成功");
            } else {
              ElMessage.error("登录失败");
            }
            setToken(res.data.data.accessToken);
            this.token = res.data.data.accessToken;
            this.userId = res.data.data.userId;
            localStorage.setItem("userId", res.data.data.userId);
            localStorage.setItem("headPortrait", res.data.data.headPortrait);
            resolve();
            getInfo(res.data.data.userId);
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
    // 获取用户信息
    getInfo() {
      return new Promise((resolve, reject) => {
        const userId = Number(localStorage.getItem("userId"));
          userController.getUserInfo(userId)
          .then((res) => {
            console.log("获取用户信息");
            console.log(res);
            const user = res.data.data;
            // if (res.roles && res.roles.length > 0) {
            //   // 验证返回的roles是否是一个非空数组
            //   this.roles = res.roles;
            //   this.permissions = res.permissions;
            // } else {
            //   this.roles = ["ROLE_DEFAULT"];
            // }
            this.userId = user.userId;
            this.name = user.name;
            this.email = user.email;
            this.mobile = user.mobile;
            this.headPortrait = user.headPortrait;
            localStorage.setItem("headPortrait", user.headPortrait);
            resolve(res);
          })
          .catch((error) => {
            reject(error);
          });
      });
    },
    // 退出系统
    logOut() {
      return new Promise((resolve, reject) => {
          authController.logout()
          .then(() => {
            this.token = "";
            this.userId = "";
            this.name = "";
            this.headPortrait = "";
            this.email = "";
            this.mobile = "";
            this.roles = [];
            this.permissions = [];
            localStorage.removeItem("userId");
            localStorage.removeItem("headPortrait");
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
