import request from '@/utils/request';

// 查询操作日志列表
export function list(query) {
  return request({
    url: '/operationLog/list',
    method: 'get',
    params: query,
  });
}

// 删除操作日志
export function deleteOperationLog(operationId) {
  return request({
    url: `/operationLog/${operationId}`,
    method: 'delete',
  });
}

// 清空操作日志
export function deleteAll() {
  return request({
    url: '/operationLog/clean',
    method: 'delete',
  });
}
