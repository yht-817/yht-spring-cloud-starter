# 四种授权模式
- Authorization Code（授权码模式）：正宗的OAuth2的授权模式，客户端先将用户导向认证服务器，登录后获取授权码，然后进行授权，最后根据授权码获取访问令牌
  - 使用场景：第三方Web服务器端应用与第三方原生App
- Implicit（简化模式）：和授权码模式相比，取消了获取授权码的过程，直接获取访问令牌；
  - 使用场景：第三方单页面应用
- Resource Owner Password Credentials（密码模式）：客户端直接向用户获取用户名和密码，之后向认证服务器获取访问令牌；
  - 使用场景：第一方单页应用与第一方原生App
- Client Credentials（客户端模式）：客户端直接通过客户端认证（比如client_id和client_secret）从认证服务器获取访问令牌。
  - 使用场景：没有用户参与的，完全信任的服务器端服务
- authorized_grant_types(设置权限类型):authorization_code（授权码模式）,password（密码模式）,implicit（简化模式）,client_credentials（客户端模式）,refresh_token（刷新token）
- INSERT INTO `yht-user`.`oauth_client_details`(`client_id`, `resource_ids`, `client_secret`, `scope`, `authorized_grant_types`, `web_server_redirect_uri`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`, `create_time`) VALUES ('8888', NULL, '$2a$10$Io1E3YWlkBRJ6q0P8ZFRu.cIP6B0CFhUoGSAlM.TeCVlS3GK7Y8Ii', 'all', 'authorization_code,password,implicit,client_credentials,refresh_token', 'http://www.baidu.com', NULL, 7200, 7200, NULL, NULL, '2021-09-09 14:24:19');
#### 授权码模式的使用：authorization_code
- 在浏览器访问该地址进行登录授权：http://localhost:8001/oauth/authorize?response_type=code&client_id=8888&redirect_uri=http://www.baidu.com&scope=all
- 输入账号密码
- 登录后授权操作
- 同意授权后跳转到:https://www.baidu.com/?code=dHD6Nq
- 根据code获取访问令牌:http://localhost:8001/oauth/token
#### 密码模式使用：password
- http://localhost:8001/oauth/token
- 请求参数需要：[{"key":"grant_type","value":"password","description":"","type":"text","enabled":true},{"key":"code","value":"us8eci","description":"","type":"text","enabled":true},{"key":"client_id","value":"8888","description":"","type":"text","enabled":true},{"key":"redirect_uri","value":"http://www.baidu.com","description":"","type":"text","enabled":true},{"key":"scope","value":"all","description":"","type":"text","enabled":true},{"key":"client_secret","value":"123456","description":"","type":"text","enabled":true},{"key":"username","value":"张三","description":"","type":"text","enabled":true},{"key":"password","value":"123456","description":"","type":"text","enabled":true}]
#### 简化模式：token
- http://localhost:8001/oauth/authorize?response_type=token&client_id=8888&redirect_uri=http://www.baidu.com&scope=all
#### 客户端模式：client_credentials
- http://localhost:8001/oauth/token
- 请求参数需要：[{"key":"grant_type","value":"client_credentials","description":"","type":"text","enabled":true},{"key":"scope","value":"all","description":"","type":"text","enabled":true},{"key":"client_id","value":"8888","description":"","type":"text","enabled":true},{"key":"client_secret","value":"123456","description":"","type":"text","enabled":true}]
#### 刷新token
- http://localhost:8001/oauth/token
- 请求参数需要：[{"key":"grant_type","value":"refresh_token","description":"","type":"text","enabled":true},{"key":"refresh_token","value":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MzExNzQ3NzUsInVzZXJfaWQiOjEwLCJ1c2VyX25hbWUiOiLlvKDkuIkiLCJqdGkiOiJjYjUyNzJmYy1jN2I4LTQwYTktOGVhOC00YTVhNDM0MTA4ZDgiLCJjbGllbnRfaWQiOiI4ODg4Iiwic2NvcGUiOlsiYWxsIl19.J7hTM5lbpTI7mRp5zZWMIJJ9hkjPsjHoEFSVdnpffKqmDF15IicIo39wsjKQ36UwJdmjjyo99Y9rJ9QGz_fwhUtKvvr13PCEhCOT7E8_wW8hKO7ndQ-_26BX_EIhsR8B4Vzrm_eYX7k5yWZ-AejRRDj0wW7lVtK_B4RlfwEBvFU","description":"","type":"text","enabled":true},{"key":"client_id","value":"8888","description":"","type":"text","enabled":true},{"key":"client_secret","value":"123456","description":"","type":"text","enabled":true}]
- refresh_token这个参数必须是密码模式返回的refresh_token这个字段去请求刷新的token数据