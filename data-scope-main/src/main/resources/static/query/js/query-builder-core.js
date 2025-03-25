/**
 * DataScope Query Builder - Core Module
 * 核心功能和初始化
 */

// 全局状态
let state = {
  dataSource: null,
  tables: [],
  fields: {},
  selectedTables: [],
  joins: [],
  conditions: [],
  selectedFields: [],
  groupBy: [],
  orderBy: [],
  limit: 100,
  sql: '',
  queryId: null,
  queryName: '',
  queryDescription: '',
  savedQueries: [],
  results: null,
  currentPage: 1,
  pageSize: 10,
  totalPages: 1
};

// 初始化查询构建器
function initQueryBuilder() {
  // 从URL获取查询ID（如果存在）
  const urlParams = new URLSearchParams(window.location.search);
  const queryId = urlParams.get('id');

  if (queryId) {
    state.queryId = queryId;
    loadQuery(queryId);
  }

  // 加载数据源
  loadDataSources();

  // 加载保存的查询
  loadSavedQueries();

  // 设置事件监听器
  setupEventListeners();
}

// 设置事件监听器
function setupEventListeners() {
  // 数据源选择
  const dataSourceSelect = document.getElementById('dataSourceSelect');
  if (dataSourceSelect) {
    dataSourceSelect.addEventListener('change', handleDataSourceChange);
  }

  // 表格搜索
  const tableSearchInput = document.getElementById('tableSearchInput');
  if (tableSearchInput) {
    tableSearchInput.addEventListener('input', handleTableSearch);
  }

  // 查询搜索
  const querySearchInput = document.getElementById('querySearchInput');
  if (querySearchInput) {
    querySearchInput.addEventListener('input', handleQuerySearch);
  }

  // 添加步骤按钮
  const addStepButton = document.getElementById('addStepButton');
  if (addStepButton) {
    addStepButton.addEventListener('click', handleAddStep);
  }

  // 开始构建按钮
  const startBuildingButton = document.getElementById('startBuildingButton');
  if (startBuildingButton) {
    startBuildingButton.addEventListener('click', handleStartBuilding);
  }

  // 保存按钮
  const saveButton = document.getElementById('saveButton');
  if (saveButton) {
    saveButton.addEventListener('click', handleSaveQuery);
  }

  // 预览SQL按钮
  const previewSqlButton = document.getElementById('previewSqlButton');
  if (previewSqlButton) {
    previewSqlButton.addEventListener('click', handlePreviewSql);
  }

  // 执行按钮
  const executeButton = document.getElementById('executeButton');
  if (executeButton) {
    executeButton.addEventListener('click', handleExecuteQuery);
  }

  // 复制SQL按钮
  const copySqlButton = document.getElementById('copySqlButton');
  if (copySqlButton) {
    copySqlButton.addEventListener('click', handleCopySql);
  }

  // 模态框按钮
  setupModalEventListeners();
}

// 设置模态框事件监听器
function setupModalEventListeners() {
  // 添加表格模态框
  const confirmAddTableButton = document.getElementById('confirmAddTableButton');
  if (confirmAddTableButton) {
    confirmAddTableButton.addEventListener('click', handleConfirmAddTable);
  }

  const cancelAddTableButton = document.getElementById('cancelAddTableButton');
  if (cancelAddTableButton) {
    cancelAddTableButton.addEventListener('click', () => {
      document.getElementById('addTableModal').classList.add('hidden');
    });
  }

  const joinTypeSelect = document.getElementById('joinTypeSelect');
  if (joinTypeSelect) {
    joinTypeSelect.addEventListener('change', handleJoinTypeChange);
  }

  // 添加条件模态框
  const confirmAddConditionButton = document.getElementById('confirmAddConditionButton');
  if (confirmAddConditionButton) {
    confirmAddConditionButton.addEventListener('click', handleConfirmAddCondition);
  }

  const cancelAddConditionButton = document.getElementById('cancelAddConditionButton');
  if (cancelAddConditionButton) {
    cancelAddConditionButton.addEventListener('click', () => {
      document.getElementById('addConditionModal').classList.add('hidden');
    });
  }

  const conditionOperatorSelect = document.getElementById('conditionOperatorSelect');
  if (conditionOperatorSelect) {
    conditionOperatorSelect.addEventListener('change', handleConditionOperatorChange);
  }

  // 添加分组模态框
  const confirmAddGroupByButton = document.getElementById('confirmAddGroupByButton');
  if (confirmAddGroupByButton) {
    confirmAddGroupByButton.addEventListener('click', handleConfirmAddGroupBy);
  }

  const cancelAddGroupByButton = document.getElementById('cancelAddGroupByButton');
  if (cancelAddGroupByButton) {
    cancelAddGroupByButton.addEventListener('click', () => {
      document.getElementById('addGroupByModal').classList.add('hidden');
    });
  }

  // 添加排序模态框
  const confirmAddOrderByButton = document.getElementById('confirmAddOrderByButton');
  if (confirmAddOrderByButton) {
    confirmAddOrderByButton.addEventListener('click', handleConfirmAddOrderBy);
  }

  const cancelAddOrderByButton = document.getElementById('cancelAddOrderByButton');
  if (cancelAddOrderByButton) {
    cancelAddOrderByButton.addEventListener('click', () => {
      document.getElementById('addOrderByModal').classList.add('hidden');
    });
  }
}

// 处理添加步骤
function handleAddStep() {
  // 如果尚未选择表格，则显示添加表格模态框
  if (state.selectedTables.length === 0) {
    showAddTableModal();
  } else {
    // 显示下一步选项
    showAddStepOptions();
  }
}

// 处理开始构建
function handleStartBuilding() {
  // 显示添加表格模态框
  showAddTableModal();
}

// 处理保存查询
function handleSaveQuery() {
  saveQuery();
}

// 处理预览SQL
function handlePreviewSql() {
  const sqlPreview = document.getElementById('sqlPreview');
  const sqlPreviewContainer = document.getElementById('sqlPreviewContainer');

  if (!sqlPreview || !sqlPreviewContainer) {
    return;
  }

  if (!state.sql) {
    showError('请先构建查询');
    return;
  }

  // 显示SQL预览
  sqlPreview.textContent = state.sql;
  sqlPreviewContainer.classList.remove('hidden');
}

// 处理执行查询
function handleExecuteQuery() {
  executeQuery();
}

// 生成SQL
function generateSQL() {
  if (state.selectedTables.length === 0) {
    state.sql = '';
    return;
  }

  let sql = 'SELECT ';

  // 添加字段
  if (state.selectedFields.length > 0) {
    sql += state.selectedFields.join(', ');
  } else {
    // 如果没有选择字段，则选择所有字段
    const allFields = [];
    state.selectedTables.forEach(tableName => {
      if (state.fields[tableName]) {
        state.fields[tableName].forEach(column => {
          allFields.push(`${tableName}.${column.name}`);
        });
      }
    });

    sql += allFields.join(', ');
  }

  // 添加表格
  sql += `\nFROM ${state.selectedTables[0]}`;

  // 添加连接
  state.joins.forEach(join => {
    sql += `\n${join.type} JOIN ${join.table} ON ${join.condition.leftField} = ${join.condition.rightField}`;
  });

  // 添加条件
  if (state.conditions.length > 0) {
    sql += '\nWHERE ';

    state.conditions.forEach((condition, index) => {
      if (index > 0) {
        sql += ` ${condition.logic} `;
      }

      if (condition.operator === 'IS NULL') {
        sql += `${condition.field} IS NULL`;
      } else if (condition.operator === 'IS NOT NULL') {
        sql += `${condition.field} IS NOT NULL`;
      } else {
        // 对字符串值添加引号
        let value = condition.value;
        if (isNaN(value) && value !== 'true' && value !== 'false') {
          value = `'${value}'`;
        }

        sql += `${condition.field} ${condition.operator} ${value}`;
      }
    });
  }

  // 添加分组
  if (state.groupBy.length > 0) {
    sql += '\nGROUP BY ' + state.groupBy.join(', ');
  }

  // 添加排序
  if (state.orderBy.length > 0) {
    sql += '\nORDER BY ';

    const orderClauses = state.orderBy.map(order => `${order.field} ${order.direction}`);
    sql += orderClauses.join(', ');
  }

  // 添加限制
  if (state.limit) {
    sql += `\nLIMIT ${state.limit}`;
  }

  // 更新状态
  state.sql = sql;

  // 更新SQL预览
  const sqlPreview = document.getElementById('sqlPreview');
  if (sqlPreview) {
    sqlPreview.textContent = sql;
  }
}

// 当DOM内容加载完成时初始化
document.addEventListener('DOMContentLoaded', function () {
  initQueryBuilder();
});
