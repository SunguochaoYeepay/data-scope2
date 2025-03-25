/**
 * DataScope Query Builder - Modals Module
 * 处理所有与模态框相关的功能
 */

// 显示添加表格模态框
function showAddTableModal(preSelectedTable = null) {
  const tableSelect = document.getElementById('tableSelect');
  const joinTypeSelect = document.getElementById('joinTypeSelect');
  const joinConditionContainer = document.getElementById('joinConditionContainer');
  const addTableModal = document.getElementById('addTableModal');

  // 重置模态框
  if (tableSelect) {
    tableSelect.value = preSelectedTable || '';
  }

  if (joinTypeSelect) {
    // 如果这是第一个表格，禁用连接选项
    if (state.selectedTables.length === 0) {
      joinTypeSelect.value = 'NONE';
      joinTypeSelect.disabled = true;
      joinConditionContainer.classList.add('hidden');
    } else {
      joinTypeSelect.disabled = false;
      joinTypeSelect.value = 'INNER';
      joinConditionContainer.classList.remove('hidden');

      // 更新字段选择器
      updateLeftFieldSelect();
      updateRightFieldSelect();
    }
  }

  // 显示模态框
  addTableModal.classList.remove('hidden');
}

// 显示添加条件模态框
function showAddConditionModal() {
  const conditionFieldSelect = document.getElementById('conditionFieldSelect');
  const conditionOperatorSelect = document.getElementById('conditionOperatorSelect');
  const conditionValueInput = document.getElementById('conditionValueInput');
  const conditionLogicSelect = document.getElementById('conditionLogicSelect');
  const conditionValueContainer = document.getElementById('conditionValueContainer');
  const addConditionModal = document.getElementById('addConditionModal');

  // 重置模态框
  if (conditionFieldSelect) {
    // 清除现有选项
    conditionFieldSelect.innerHTML = '<option value="">选择字段...</option>';

    // 添加已选表格的字段
    state.selectedTables.forEach(tableName => {
      if (state.fields[tableName]) {
        const optgroup = document.createElement('optgroup');
        optgroup.label = tableName;

        state.fields[tableName].forEach(column => {
          const option = document.createElement('option');
          option.value = `${tableName}.${column.name}`;
          option.textContent = `${tableName}.${column.name}`;
          optgroup.appendChild(option);
        });

        conditionFieldSelect.appendChild(optgroup);
      }
    });
  }

  if (conditionOperatorSelect) {
    conditionOperatorSelect.value = '=';
  }

  if (conditionValueInput) {
    conditionValueInput.value = '';
  }

  if (conditionLogicSelect) {
    conditionLogicSelect.value = 'AND';
  }

  if (conditionValueContainer) {
    conditionValueContainer.classList.remove('hidden');
  }

  // 显示模态框
  addConditionModal.classList.remove('hidden');
}

// 显示添加分组模态框
function showAddGroupByModal() {
  const groupByFieldSelect = document.getElementById('groupByFieldSelect');
  const addGroupByModal = document.getElementById('addGroupByModal');

  // 重置模态框
  if (groupByFieldSelect) {
    // 清除现有选项
    groupByFieldSelect.innerHTML = '<option value="">选择字段...</option>';

    // 添加已选表格的字段
    state.selectedTables.forEach(tableName => {
      if (state.fields[tableName]) {
        const optgroup = document.createElement('optgroup');
        optgroup.label = tableName;

        state.fields[tableName].forEach(column => {
          const option = document.createElement('option');
          option.value = `${tableName}.${column.name}`;
          option.textContent = `${tableName}.${column.name}`;
          optgroup.appendChild(option);
        });

        groupByFieldSelect.appendChild(optgroup);
      }
    });
  }

  // 显示模态框
  addGroupByModal.classList.remove('hidden');
}

// 显示添加排序模态框
function showAddOrderByModal() {
  const orderByFieldSelect = document.getElementById('orderByFieldSelect');
  const orderByDirectionSelect = document.getElementById('orderByDirectionSelect');
  const addOrderByModal = document.getElementById('addOrderByModal');

  // 重置模态框
  if (orderByFieldSelect) {
    // 清除现有选项
    orderByFieldSelect.innerHTML = '<option value="">选择字段...</option>';

    // 添加已选表格的字段
    state.selectedTables.forEach(tableName => {
      if (state.fields[tableName]) {
        const optgroup = document.createElement('optgroup');
        optgroup.label = tableName;

        state.fields[tableName].forEach(column => {
          const option = document.createElement('option');
          option.value = `${tableName}.${column.name}`;
          option.textContent = `${tableName}.${column.name}`;
          optgroup.appendChild(option);
        });

        orderByFieldSelect.appendChild(optgroup);
      }
    });
  }

  if (orderByDirectionSelect) {
    orderByDirectionSelect.value = 'ASC';
  }

  // 显示模态框
  addOrderByModal.classList.remove('hidden');
}

// 显示添加步骤选项
function showAddStepOptions() {
  // 创建一个带有选项的下拉菜单
  const optionsContainer = document.createElement('div');
  optionsContainer.className = 'absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 z-10';
  optionsContainer.innerHTML = `
        <div class="py-1" role="menu" aria-orientation="vertical">
            <button class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 w-full text-left" role="menuitem" id="addTableOption">
                <i class="fas fa-table mr-2"></i> 添加表
            </button>
            <button class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 w-full text-left" role="menuitem" id="addConditionOption">
                <i class="fas fa-filter mr-2"></i> 添加条件
            </button>
            <button class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 w-full text-left" role="menuitem" id="addGroupByOption">
                <i class="fas fa-layer-group mr-2"></i> 添加分组
            </button>
            <button class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 w-full text-left" role="menuitem" id="addOrderByOption">
                <i class="fas fa-sort mr-2"></i> 添加排序
            </button>
        </div>
    `;

  // 添加到DOM
  document.body.appendChild(optionsContainer);

  // 定位下拉菜单
  const addStepButton = document.getElementById('addStepButton');
  const buttonRect = addStepButton.getBoundingClientRect();
  optionsContainer.style.position = 'fixed';
  optionsContainer.style.top = `${buttonRect.bottom}px`;
  optionsContainer.style.left = `${buttonRect.left}px`;

  // 添加事件监听器
  document.getElementById('addTableOption').addEventListener('click', () => {
    document.body.removeChild(optionsContainer);
    showAddTableModal();
  });

  document.getElementById('addConditionOption').addEventListener('click', () => {
    document.body.removeChild(optionsContainer);
    showAddConditionModal();
  });

  document.getElementById('addGroupByOption').addEventListener('click', () => {
    document.body.removeChild(optionsContainer);
    showAddGroupByModal();
  });

  document.getElementById('addOrderByOption').addEventListener('click', () => {
    document.body.removeChild(optionsContainer);
    showAddOrderByModal();
  });

  // 点击外部时关闭下拉菜单
  document.addEventListener('click', function closeDropdown(e) {
    if (!optionsContainer.contains(e.target) && e.target !== addStepButton) {
      document.body.removeChild(optionsContainer);
      document.removeEventListener('click', closeDropdown);
    }
  });
}

// 更新左侧字段选择器
function updateLeftFieldSelect() {
  const leftFieldSelect = document.getElementById('leftFieldSelect');
  if (!leftFieldSelect) {
    return;
  }

  // 清除现有选项
  leftFieldSelect.innerHTML = '<option value="">选择字段...</option>';

  // 添加已选表格的字段
  state.selectedTables.forEach(tableName => {
    if (state.fields[tableName]) {
      const optgroup = document.createElement('optgroup');
      optgroup.label = tableName;

      state.fields[tableName].forEach(column => {
        const option = document.createElement('option');
        option.value = `${tableName}.${column.name}`;
        option.textContent = `${tableName}.${column.name}`;
        optgroup.appendChild(option);
      });

      leftFieldSelect.appendChild(optgroup);
    }
  });
}

// 更新右侧字段选择器
function updateRightFieldSelect() {
  const rightFieldSelect = document.getElementById('rightFieldSelect');
  const tableSelect = document.getElementById('tableSelect');

  if (!rightFieldSelect || !tableSelect) {
    return;
  }

  // 清除现有选项
  rightFieldSelect.innerHTML = '<option value="">选择字段...</option>';

  // 获取选中的表格
  const selectedTable = tableSelect.value;

  if (!selectedTable || !state.fields[selectedTable]) {
    return;
  }

  // 添加选中表格的字段
  const optgroup = document.createElement('optgroup');
  optgroup.label = selectedTable;

  state.fields[selectedTable].forEach(column => {
    const option = document.createElement('option');
    option.value = `${selectedTable}.${column.name}`;
    option.textContent = `${selectedTable}.${column.name}`;
    optgroup.appendChild(option);
  });

  rightFieldSelect.appendChild(optgroup);
}

// 更新表格选择器
function updateTableSelect() {
  const tableSelect = document.getElementById('tableSelect');
  if (!tableSelect) {
    return;
  }

  // 清除现有选项
  tableSelect.innerHTML = '<option value="">选择一个表...</option>';

  // 添加表格
  state.tables.forEach(table => {
    const option = document.createElement('option');
    option.value = table.name;
    option.textContent = table.name;
    tableSelect.appendChild(option);
  });
}

// 处理连接类型变更
function handleJoinTypeChange() {
  const joinTypeSelect = document.getElementById('joinTypeSelect');
  const joinConditionContainer = document.getElementById('joinConditionContainer');

  const joinType = joinTypeSelect.value;

  if (joinType === 'NONE') {
    joinConditionContainer.classList.add('hidden');
  } else {
    joinConditionContainer.classList.remove('hidden');

    // 更新字段选择器
    updateLeftFieldSelect();
    updateRightFieldSelect();
  }
}

// 处理条件运算符变更
function handleConditionOperatorChange() {
  const conditionOperatorSelect = document.getElementById('conditionOperatorSelect');
  const conditionValueContainer = document.getElementById('conditionValueContainer');

  const operator = conditionOperatorSelect.value;

  // 对于IS NULL和IS NOT NULL隐藏值输入
  if (operator === 'IS NULL' || operator === 'IS NOT NULL') {
    conditionValueContainer.classList.add('hidden');
  } else {
    conditionValueContainer.classList.remove('hidden');
  }
}

// 处理确认添加表格
function handleConfirmAddTable() {
  const tableSelect = document.getElementById('tableSelect');
  const joinTypeSelect = document.getElementById('joinTypeSelect');
  const leftFieldSelect = document.getElementById('leftFieldSelect');
  const rightFieldSelect = document.getElementById('rightFieldSelect');
  const addTableModal = document.getElementById('addTableModal');

  const selectedTable = tableSelect.value;

  if (!selectedTable) {
    showError('请选择一个表');
    return;
  }

  // 如果这不是第一个表格，验证连接
  if (state.selectedTables.length > 0) {
    const joinType = joinTypeSelect.value;

    if (joinType !== 'NONE') {
      const leftField = leftFieldSelect.value;
      const rightField = rightFieldSelect.value;

      if (!leftField || !rightField) {
        showError('请选择连接条件');
        return;
      }

      // 添加连接到状态
      state.joins.push({
        type: joinType,
        table: selectedTable,
        condition: {
          leftField,
          rightField
        }
      });
    }
  }

  // 如果表格尚未添加，则添加到状态
  if (!state.selectedTables.includes(selectedTable)) {
    state.selectedTables.push(selectedTable);
  }

  // 隐藏模态框
  addTableModal.classList.add('hidden');

  // 更新构建器UI
  updateBuilderUI();

  // 生成SQL
  generateSQL();
}

// 处理确认添加条件
function handleConfirmAddCondition() {
  const conditionFieldSelect = document.getElementById('conditionFieldSelect');
  const conditionOperatorSelect = document.getElementById('conditionOperatorSelect');
  const conditionValueInput = document.getElementById('conditionValueInput');
  const conditionLogicSelect = document.getElementById('conditionLogicSelect');
  const addConditionModal = document.getElementById('addConditionModal');

  const field = conditionFieldSelect.value;
  const operator = conditionOperatorSelect.value;
  const value = conditionValueInput.value;
  const logic = conditionLogicSelect.value;

  if (!field) {
    showError('请选择字段');
    return;
  }

  if ((operator !== 'IS NULL' && operator !== 'IS NOT NULL') && !value) {
    showError('请输入值');
    return;
  }

  // 添加条件到状态
  state.conditions.push({
    field,
    operator,
    value,
    logic
  });

  // 隐藏模态框
  addConditionModal.classList.add('hidden');

  // 更新构建器UI
  updateBuilderUI();

  // 生成SQL
  generateSQL();
}

// 处理确认添加分组
function handleConfirmAddGroupBy() {
  const groupByFieldSelect = document.getElementById('groupByFieldSelect');
  const addGroupByModal = document.getElementById('addGroupByModal');

  const field = groupByFieldSelect.value;

  if (!field) {
    showError('请选择字段');
    return;
  }

  // 添加分组到状态
  state.groupBy.push(field);

  // 隐藏模态框
  addGroupByModal.classList.add('hidden');

  // 更新构建器UI
  updateBuilderUI();

  // 生成SQL
  generateSQL();
}

// 处理确认添加排序
function handleConfirmAddOrderBy() {
  const orderByFieldSelect = document.getElementById('orderByFieldSelect');
  const orderByDirectionSelect = document.getElementById('orderByDirectionSelect');
  const addOrderByModal = document.getElementById('addOrderByModal');

  const field = orderByFieldSelect.value;
  const direction = orderByDirectionSelect.value;

  if (!field) {
    showError('请选择字段');
    return;
  }

  // 添加排序到状态
  state.orderBy.push({
    field,
    direction
  });

  // 隐藏模态框
  addOrderByModal.classList.add('hidden');

  // 更新构建器UI
  updateBuilderUI();

  // 生成SQL
  generateSQL();
}

// 移除表格
function removeTable(tableName) {
  // 获取表格索引
  const index = state.selectedTables.indexOf(tableName);

  if (index === -1) {
    return;
  }

  // 如果不是第一个表格，移除相关连接
  if (index > 0) {
    state.joins = state.joins.filter(join => join.table !== tableName);
  }
  // 如果是第一个表格，移除所有表格和连接
  else {
    state.selectedTables = [];
    state.joins = [];
    state.conditions = [];
    state.groupBy = [];
    state.orderBy = [];

    // 更新构建器UI
    updateBuilderUI();

    // 生成SQL
    generateSQL();

    return;
  }

  // 移除表格
  state.selectedTables.splice(index, 1);

  // 移除使用该表格的条件
  state.conditions = state.conditions.filter(condition => !condition.field.startsWith(tableName + '.'));

  // 移除使用该表格的分组
  state.groupBy = state.groupBy.filter(field => !field.startsWith(tableName + '.'));

  // 移除使用该表格的排序
  state.orderBy = state.orderBy.filter(order => !order.field.startsWith(tableName + '.'));

  // 更新构建器UI
  updateBuilderUI();

  // 生成SQL
  generateSQL();
}

// 移除条件
function removeCondition(index) {
  if (index < 0 || index >= state.conditions.length) {
    return;
  }

  // 移除条件
  state.conditions.splice(index, 1);

  // 更新构建器UI
  updateBuilderUI();

  // 生成SQL
  generateSQL();
}

// 移除分组
function removeGroupBy(index) {
  if (index < 0 || index >= state.groupBy.length) {
    return;
  }

  // 移除分组
  state.groupBy.splice(index, 1);

  // 更新构建器UI
  updateBuilderUI();

  // 生成SQL
  generateSQL();
}

// 移除排序
function removeOrderBy(index) {
  if (index < 0 || index >= state.orderBy.length) {
    return;
  }

  // 移除排序
  state.orderBy.splice(index, 1);

  // 更新构建器UI
  updateBuilderUI();

  // 生成SQL
  generateSQL();
}
