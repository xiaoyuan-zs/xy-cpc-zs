import request from '@/utils/request';
import { parseStrEmpty } from '@/utils/common';

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query,
  });
}

// 查询用户详细
export function getUser(userId) {
  return request({
    url: `/system/user/${parseStrEmpty(userId)}`,
    method: 'get',
  });
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data,
  });
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data,
  });
}

// 删除用户
export function deleteUser(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'delete',
  });
}

// 用户密码重置
export function resetUserPassword(userId, password) {
  const data = {
    userId,
    password,
  };
  return request({
    url: `/system/user/${userId}/password/reset`,
    method: 'put',
    data,
  });
}

// 用户状态修改
export function changeUserStatus(userId, status) {
  const data = {
    userId,
    status,
  };
  return request({
    url: `/system/user/${userId}/status`,
    method: 'put',
    data,
  });
}

// 查询用户个人信息
export function getUserProfile() {
  return request({
    url: '/system/user/profile',
    method: 'get',
  });
}

// 修改用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data,
  });
}

// 用户密码重置
export function updateUserPassword(oldPassword, newPassword) {
  const data = {
    oldPassword,
    newPassword,
  };
  return request({
    url: '/system/user/profile/password',
    method: 'put',
    data,
  });
}

// 用户头像上传
export function uploadAvatar(data) {
  return request({
    url: '/system/user/profile/avatar',
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    method: 'post',
    data,
  });
}
