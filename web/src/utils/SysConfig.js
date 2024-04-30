let loginData = {};
export function setLogin(data){
    loginData = data;
}
export function isPermitted(p){
 return  loginData.perms.indexOf(p) != -1
}
export function notPermitted(p){
  return  !isPermitted(p)
}
