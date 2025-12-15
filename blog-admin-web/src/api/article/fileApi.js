import axios from '@/utils/request'

function upload(data) {
  return axios({
    url: '/article/file/upload',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: '/article/file',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function download(id) {
  return axios({
    url: '/article/file/download',
    method: 'GET',
    params: { "id": id },
    responseType: 'blob' //axios支持文件下载的关键
  })
}

function list(data) {
  return axios({
    url: '/article/file/list',
    method: 'POST',
    data: {
      ...data
    }
  })
}


export default {upload, del, download, list}
