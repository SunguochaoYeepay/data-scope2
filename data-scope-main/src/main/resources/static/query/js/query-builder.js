/**
 * DataScope Query Builder
 * 主入口文件 - 引入所有模块
 */

// 引入其他模块的功能
// 注意：在实际使用时，应该通过<script>标签引入各个模块文件
// 这里只是为了保持与原来的单文件结构兼容

// 模块加载顺序：
// 1. query-builder-core.js - 核心功能和初始化
// 2. query-builder-api.js - API调用相关功能
// 3. query-builder-ui.js - UI相关功能
// 4. query-builder-modals.js - 模态框相关功能

// 这个文件主要用于提供一个简单的接口，方便在HTML中引用
// 实际功能已经分散到各个模块文件中

// 查询构建器对象
const QueryBuilder = {
  // 初始化查询构建器
  init: function () {
    // 当DOM内容加载完成时初始化
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', initQueryBuilder);
    } else {
      initQueryBuilder();
    }
  },

  // 获取当前状态
  getState: function () {
    return state;
  },

  // 设置状态
  setState: function (newState) {
    state = {...state, ...newState};
  },

  // 生成SQL
  generateSQL: function () {
    generateSQL();
    return state.sql;
  },

  // 执行查询
  executeQuery: function () {
    return executeQuery();
  },

  // 保存查询
  saveQuery: function () {
    return saveQuery();
  },

  // 加载查询
  loadQuery: function (queryId) {
    return loadQuery(queryId);
  }
};

// 自动初始化
QueryBuilder.init();

// 导出查询构建器对象
window.QueryBuilder = QueryBuilder;
