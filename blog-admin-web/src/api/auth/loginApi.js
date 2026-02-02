import request from '@/utils/request';

// 登录方法
export function login(username, password, uuid, code) {

  // console.log(passwordEncrypt);
  const data = {
    username,
    password,
    code,
    uuid,
  };
  return request({
    url: '/auth/login',
    headers: {
      isToken: false,
    },
    method: 'post',
    data,
  });
}

// 注册方法
export function register(data) {
  return request({
    url: '/auth/register',
    headers: {
      isToken: false,
    },
    method: 'post',
    data,
  });
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/auth/getInfo',
    method: 'get',
  });
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'delete',
  });
}

export function getCaptcha() {
  return request({
    url: '/auth/getCaptcha',
    headers: {
      isToken: false,
    },
    method: 'get',
    timeout: 20000,
  });
}

export const getRouters = () =>
  request({
    url: '/auth/getRouters',
    method: 'get',
  });
