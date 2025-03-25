/**
 * DataScope Query Builder - UI Module
 * 处理所有与UI相关的功能
 */

// 更新表格树
function updateTablesTree() {
  const tablesTree = document.getElementById('tablesTree');
  if (!tablesTree) {
    return;
  }

  // 清除现有树
  tablesTree.innerHTML = '';

  if (!state.tables || state.tables.length === 0) {
    const emptyItem = document.createElement('li');
    emptyItem.className = 'text-sm text-gray-500 p-2';
    emptyItem.textContent = '无表';
    tablesTree.appendChild(emptyItem);
    return;
  }

  // 添加表格到树
  state.tables.forEach(table => {
    const tableItem = document.createElement('li');
    tableItem.className = 'mb-2';

    const tableHeader = document.createElement('div');
    tableHeader.className = 'flex items-center justify-between p-2 bg-gray-100 rounded-md cursor-pointer hover:bg-gray-200';
    tableHeader.innerHTML = `
            <div class="flex items-center">
                <i class="fas fa-table text-gray-600 mr-2"></i>
                <span class="text-sm font-medium">${table.name}</span>
            </div>
            <div>
                <button class="text-xs text-indigo-600 hover:text-indigo-800 mr-2 add-table-btn" data-table="${table.name}">
                    <i class="fas fa-plus"></i> 添加
                </button>
                <i class="fas fa-chevron-down text-gray-500 toggle-btn"></i>
            </div>
        `;

    tableItem.appendChild(tableHeader);

    // 添加列
    const columnsContainer = document.createElement('ul');
    columnsContainer.className = 'pl-6 mt-1 hidden';

    if (state.fields[table.name]) {
      state.fields[table.name].forEach(column => {
        const columnItem = document.createElement('li');
        columnItem.className = 'py-1 px-2 text-sm flex items-center hover:bg-gray-100 rounded';

        let dataTypeIcon = 'fas fa-font';
        let dataTypeClass = 'text-blue-500';

        // 根据数据类型确定图标
        if (column.type.includes('INT') || column.type.includes('DECIMAL')) {
          dataTypeIcon = 'fas fa-hashtag';
          dataTypeClass = 'text-green-500';
        } else if (column.type.includes('DATE') || column.type.includes('TIME')) {
          dataTypeIcon = 'fas fa-calendar';
          dataTypeClass = 'text-purple-500';
        } else if (column.type.includes('BOOL')) {
          dataTypeIcon = 'fas fa-toggle-on';
          dataTypeClass = 'text-yellow-500';
        }

        columnItem.innerHTML = `
                    <i class="${dataTypeIcon} ${dataTypeClass} mr-2"></i>
                    <span>${column.name}</span>
                    <span class="ml-2 text-xs text-gray-500">${column.type}</span>
                `;

        columnsContainer.appendChild(columnItem);
      });
    }

    tableItem.appendChild(columnsContainer);

    // 切换列可见性
    tableHeader.querySelector('.toggle-btn').addEventListener('click', () => {
      columnsContainer.classList.toggle('hidden');
      tableHeader.querySelector('.toggle-btn').classList.toggle('fa-chevron-down');
      tableHeader.querySelector('.toggle-btn').classList.toggle('fa-chevron-up');
    });

    // 添加表格按钮
    tableHeader.querySelector('.add-table-btn').addEventListener('click', (e) => {
      e.stopPropagation();
      const tableName = e.currentTarget.getAttribute('data-table');
      showAddTableModal(tableName);
    });

    tablesTree.appendChild(tableItem);
  });
}

// 更新保存的查询列表
function updateSavedQueriesList() {
  const savedQueriesList = document.getElementById('savedQueriesList');
  if (!savedQueriesList) {
    return;
  }

  // 清除现有列表
  savedQueriesList.innerHTML = '';

  if (!state.savedQueries || state.savedQueries.length === 0) {
    const emptyItem = document.createElement('li');
    emptyItem.className = 'text-sm text-gray-500 p-2';
    emptyItem.textContent = '暂无保存的查询';
    savedQueriesList.appendChild(emptyItem);
    return;
  }

  // 如果搜索输入有值，则过滤查询
  let queries = state.savedQueries;
  const querySearchInput = document.getElementById('querySearchInput');
  if (querySearchInput && querySearchInput.value) {
    const searchTerm = querySearchInput.value.toLowerCase();
    queries = queries.filter(query =>
      query.name.toLowerCase().includes(searchTerm) ||
      (query.description && query.description.toLowerCase().includes(searchTerm))
    );
  }

  // 添加查询到列表
  queries.forEach(query => {
    const queryItem = document.createElement('li');
    queryItem.className = 'p-2 hover:bg-gray-100 rounded cursor-pointer';
    queryItem.innerHTML = `
            <div class="flex items-center justify-between">
                <div>
                    <div class="text-sm font-medium">${query.name}</div>
                    <div class="text-xs text-gray-500">${query.description || '无描述'}</div>
                </div>
                <div>
                    <button class="text-xs text-indigo-600 hover:text-indigo-800 load-query-btn" data-id="${query.id}">
                        <i class="fas fa-arrow-right"></i>
                    </button>
                </div>
            </div>
        `;

    // 加载查询按钮
    queryItem.querySelector('.load-query-btn').addEventListener('click', () => {
      loadQuery(query.id);
    });

    savedQueriesList.appendChild(queryItem);
  });
}

// 更新构建器UI
function updateBuilderUI() {
  const builderStepsContainer = document.getElementById('builderStepsContainer');
  const emptyBuilderState = document.getElementById('emptyBuilderState');

  if (!builderStepsContainer || !emptyBuilderState) {
    return;
  }

  if (state.selectedTables.length === 0) {
    emptyBuilderState.classList.remove('hidden');
    builderStepsContainer.innerHTML = '';
    return;
  }

  emptyBuilderState.classList.add('hidden');
  builderStepsContainer.innerHTML = '';

  // 添加表格部分
  const tablesSection = document.createElement('div');
  tablesSection.className = 'mb-6 p-4 border border-gray-200 rounded-lg';
  tablesSection.innerHTML = `
        <h3 class="text-md font-medium text-gray-900 mb-3">表</h3>
        <div class="space-y-2" id="selectedTablesContainer"></div>
    `;

  builderStepsContainer.appendChild(tablesSection);

  const selectedTablesContainer = tablesSection.querySelector('#selectedTablesContainer');

  // 添加选中的表格
  state.selectedTables.forEach((tableName, index) => {
    const tableItem = document.createElement('div');
    tableItem.className = 'flex items-center justify-between p-2 bg-gray-50 rounded-md';

    let tableInfo = `<span class="font-medium">${tableName}</span>`;

    // 如果不是第一个表格，添加连接信息
    if (index > 0) {
      const join = state.joins.find(j => j.table === tableName);
      if (join) {
        tableInfo = `
                    <span class="text-gray-500 mr-2">${join.type} JOIN</span>
                    <span class="font-medium">${tableName}</span>
                    <span class="text-gray-500 ml-2">ON ${join.condition.leftField} = ${join.condition.rightField}</span>
                `;
      }
    }

    tableItem.innerHTML = `
            <div>${tableInfo}</div>
            <div>
                <button class="text-xs text-red-600 hover:text-red-800 remove-table-btn" data-table="${tableName}">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `;

    // 移除表格按钮
    tableItem.querySelector('.remove-table-btn').addEventListener('click', () => {
      removeTable(tableName);
    });

    selectedTablesContainer.appendChild(tableItem);
  });

  // 如果有条件，添加条件部分
  if (state.conditions.length > 0) {
    const conditionsSection = document.createElement('div');
    conditionsSection.className = 'mb-6 p-4 border border-gray-200 rounded-lg';
    conditionsSection.innerHTML = `
            <h3 class="text-md font-medium text-gray-900 mb-3">条件</h3>
            <div class="space-y-2" id="conditionsContainer"></div>
        `;

    builderStepsContainer.appendChild(conditionsSection);

    const conditionsContainer = conditionsSection.querySelector('#conditionsContainer');

    // 添加条件
    state.conditions.forEach((condition, index) => {
      const conditionItem = document.createElement('div');
      conditionItem.className = 'flex items-center justify-between p-2 bg-gray-50 rounded-md';

      let conditionText = '';

      // 为第一个之后的条件添加逻辑运算符
      if (index > 0) {
        conditionText += `<span class="text-gray-500 mr-2">${condition.logic}</span>`;
      }

      // 添加条件文本
      if (condition.operator === 'IS NULL') {
        conditionText += `<span>${condition.field} IS NULL</span>`;
      } else if (condition.operator === 'IS NOT NULL') {
        conditionText += `<span>${condition.field} IS NOT NULL</span>`;
      } else {
        conditionText += `<span>${condition.field} ${condition.operator} ${condition.value}</span>`;
      }

      conditionItem.innerHTML = `
                <div>${conditionText}</div>
                <div>
                    <button class="text-xs text-red-600 hover:text-red-800 remove-condition-btn" data-index="${index}">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            `;

      // 移除条件按钮
      conditionItem.querySelector('.remove-condition-btn').addEventListener('click', () => {
        removeCondition(index);
      });

      conditionsContainer.appendChild(conditionItem);
    });
  }

  // 如果有分组，添加分组部分
  if (state.groupBy.length > 0) {
    const groupBySection = document.createElement('div');
    groupBySection.className = 'mb-6 p-4 border border-gray-200 rounded-lg';
    groupBySection.innerHTML = `
            <h3 class="text-md font-medium text-gray-900 mb-3">分组</h3>
            <div class="space-y-2" id="groupByContainer"></div>
        `;

    builderStepsContainer.appendChild(groupBySection);

    const groupByContainer = groupBySection.querySelector('#groupByContainer');

    // 添加分组
    state.groupBy.forEach((field, index) => {
      const groupByItem = document.createElement('div');
      groupByItem.className = 'flex items-center justify-between p-2 bg-gray-50 rounded-md';

      groupByItem.innerHTML = `
                <div>${field}</div>
                <div>
                    <button class="text-xs text-red-600 hover:text-red-800 remove-group-by-btn" data-index="${index}">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            `;

      // 移除分组按钮
      groupByItem.querySelector('.remove-group-by-btn').addEventListener('click', () => {
        removeGroupBy(index);
      });

      groupByContainer.appendChild(groupByItem);
    });
  }

  // 如果有排序，添加排序部分
  if (state.orderBy.length > 0) {
    const orderBySection = document.createElement('div');
    orderBySection.className = 'mb-6 p-4 border border-gray-200 rounded-lg';
    orderBySection.innerHTML = `
            <h3 class="text-md font-medium text-gray-900 mb-3">排序</h3>
            <div class="space-y-2" id="orderByContainer"></div>
        `;

    builderStepsContainer.appendChild(orderBySection);

    const orderByContainer = orderBySection.querySelector('#orderByContainer');

    // 添加排序
    state.orderBy.forEach((order, index) => {
      const orderByItem = document.createElement('div');
      orderByItem.className = 'flex items-center justify-between p-2 bg-gray-50 rounded-md';

      orderByItem.innerHTML = `
                <div>${order.field} ${order.direction}</div>
                <div>
                    <button class="text-xs text-red-600 hover:text-red-800 remove-order-by-btn" data-index="${index}">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            `;

      // 移除排序按钮
      orderByItem.querySelector('.remove-order-by-btn').addEventListener('click', () => {
        removeOrderBy(index);
      });

      orderByContainer.appendChild(orderByItem);
    });
  }
}

// 显示结果
function displayResults() {
  const resultsContainer = document.getElementById('resultsContainer');
  const resultsHeader = document.getElementById('resultsHeader');
  const resultsBody = document.getElementById('resultsBody');
  const paginationInfo = document.getElementById('paginationInfo');

  if (!resultsContainer || !resultsHeader || !resultsBody) {
    return;
  }

  if (!state.results) {
    resultsContainer.classList.add('hidden');
    return;
  }

  // 显示结果容器
  resultsContainer.classList.remove('hidden');

  // 清除现有结果
  resultsHeader.innerHTML = '';
  resultsBody.innerHTML = '';

  // 添加表头
  const headerRow = document.createElement('tr');

  if (state.results.columns) {
    state.results.columns.forEach(column => {
      const th = document.createElement('th');
      th.className = 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider';
      th.textContent = column.label || column.name;
      headerRow.appendChild(th);
    });
  }

  resultsHeader.appendChild(headerRow);

  // 添加数据行
  if (state.results.rows) {
    state.results.rows.forEach(row => {
      const dataRow = document.createElement('tr');
      dataRow.className = 'hover:bg-gray-50';

      if (state.results.columns) {
        state.results.columns.forEach(column => {
          const td = document.createElement('td');
          td.className = 'px-6 py-4 whitespace-nowrap text-sm text-gray-500';

          const value = row[column.name];
          td.textContent = value !== null && value !== undefined ? value : '';

          dataRow.appendChild(td);
        });
      }

      resultsBody.appendChild(dataRow);
    });
  }

  // 更新分页信息
  if (paginationInfo) {
    const totalRows = state.results.totalRows || 0;
    const pageSize = state.results.rows ? state.results.rows.length : 0;
    const currentPage = state.currentPage || 1;

    paginationInfo.textContent = `显示 ${(currentPage - 1) * pageSize + 1} 到 ${Math.min(currentPage * pageSize, totalRows)} 条，共 ${totalRows} 条结果`;
  }
}

// 实用函数
function showLoading() {
  const loadingIndicator = document.getElementById('loading-indicator');
  if (loadingIndicator) {
    loadingIndicator.classList.remove('hidden');
  }
}

function hideLoading() {
  const loadingIndicator = document.getElementById('loading-indicator');
  if (loadingIndicator) {
    loadingIndicator.classList.add('hidden');
  }
}

function showError(message) {
  const errorContainer = document.getElementById('error-container');
  const errorMessage = document.getElementById('error-message');

  if (errorContainer && errorMessage) {
    errorMessage.textContent = message;
    errorContainer.classList.remove('hidden');

    // 5秒后自动隐藏
    setTimeout(() => {
      errorContainer.classList.add('hidden');
    }, 5000);
  }
}

function showSuccess(message) {
  const successContainer = document.getElementById('success-container');
  const successMessage = document.getElementById('success-message');

  if (successContainer && successMessage) {
    successMessage.textContent = message;
    successContainer.classList.remove('hidden');

    // 3秒后自动隐藏
    setTimeout(() => {
      successContainer.classList.add('hidden');
    }, 3000);
  }
}

// 处理表格搜索
function handleTableSearch(e) {
  const searchTerm = e.target.value.toLowerCase();

  // 过滤树中的表格
  const tablesTree = document.getElementById('tablesTree');
  if (!tablesTree) return;

  const tableItems = tablesTree.querySelectorAll('li');

  tableItems.forEach(item => {
    const tableName = item.querySelector('.font-medium')?.textContent.toLowerCase();

    if (tableName && tableName.includes(searchTerm)) {
      item.style.display = '';
    } else {
      item.style.display = 'none';
    }
  });
}

// 处理查询搜索
function handleQuerySearch() {
  // 使用过滤结果更新保存的查询列表
  updateSavedQueriesList();
}

// 复制SQL
function handleCopySql() {
  const sqlPreview = document.getElementById('sqlPreview');

  if (!sqlPreview || !state.sql) {
    showError('没有可复制的SQL');
    return;
  }

  // 复制到剪贴板
  navigator.clipboard.writeText(state.sql)
    .then(() => {
      showSuccess('SQL已复制到剪贴板');
    })
    .catch(err => {
      console.error('无法复制SQL:', err);
      showError('复制SQL失败');
    });
}
