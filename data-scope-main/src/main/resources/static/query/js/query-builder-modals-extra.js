/**
 * DataScope Query Builder - 额外模态框模块
 * 处理分组和排序等额外模态框相关的功能
 */

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

// 设置额外模态框的事件监听器
function setupExtraModalEventListeners() {
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

// 当DOM内容加载完成时初始化
document.addEventListener('DOMContentLoaded', function () {
  setupExtraModalEventListeners();
});
