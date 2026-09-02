import { generateService } from '@umijs/openapi';

// 用户服务配置
generateService({
  requestLibPath: "import request from '../../../utils/request'", // 你的自定义请求库路径
  schemaPath: 'http://localhost:8890/api/user/v3/api-docs/default',
  serversPath: './src/services/user', // 用户服务生成的代码存放路径
  projectName: 'user', // 保留项目名称
  apiPrefix: "'/user'",
  hook: {
    customFunctionName: (operation) => {
      // 自定义函数名生成逻辑
      return operation.operationId || `${operation.method}${operation.path.split('/').join('_')}`;
    }
  }
});

// 资源服务配置
generateService({
  requestLibPath: "import request from '../../../utils/request'", // 你的自定义请求库路径
  schemaPath: 'http://localhost:8890/api/resource/v3/api-docs/default',
  serversPath: './src/services/resource', // 资源服务生成的代码存放路径
  projectName: 'resource', // 保留项目名称
  apiPrefix: "'/resource'",
  hook: {
    customFunctionName: (operation) => {
      // 自定义函数名生成逻辑
      return operation.operationId || `${operation.method}${operation.path.split('/').join('_')}`;
    }
  }
});

// 其他服务可以按照相同模式添加...
