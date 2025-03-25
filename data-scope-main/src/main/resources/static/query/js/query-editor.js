/**
 * DataScope Query Editor JavaScript
 *
 * This file contains the functionality for the SQL query editor,
 * including query execution, result handling, and UI interactions.
 */

// Global variables
let editor; // CodeMirror editor instance
let currentDataSourceId = null;
let currentQueryId = null;
let currentQueryName = '';
let currentQueryDescription = '';
let isExecuting = false;
let resultData = null;
let currentPage = 1;
let pageSize = 10;
let totalPages = 1;
let sortColumn = null;
let sortDirection = 'asc';

// DOM elements
const queryNameInput = document.getElementById('queryName');
const executeButton = document.getElementById('executeButton');
const saveButton = document.getElementById('saveButton');
const favoriteButton = document.getElementById('favoriteButton');
const exportButton = document.getElementById('exportButton');
const configButton = document.getElementById('configButton');
const dataSourceSelect = document.getElementById('dataSourceSelect');
const resultsTable = document.getElementById('resultsTable');
const resultsBody = document.getElementById('resultsBody');
const resultsHeader = document.getElementById('resultsHeader');
const paginationContainer = document.getElementById('paginationContainer');
const statusMessage = document.getElementById('statusMessage');
const loadingIndicator = document.getElementById('loadingIndicator');

/**
 * Initialize the query editor
 */
function initQueryEditor() {
  // Initialize CodeMirror
  editor = CodeMirror.fromTextArea(document.getElementById("sqlEditor"), {
    mode: "text/x-sql",
    theme: "monokai",
    lineNumbers: true,
    autoCloseBrackets: true,
    matchBrackets: true,
    indentUnit: 4,
    lineWrapping: true,
    extraKeys: {
      "Ctrl-Enter": executeQuery,
      "Cmd-Enter": executeQuery
    }
  });

  // Set editor height
  editor.setSize(null, 200);

  // Load data sources
  loadDataSources();

  // Add event listeners
  executeButton.addEventListener('click', executeQuery);
  saveButton.addEventListener('click', saveQuery);
  favoriteButton.addEventListener('click', toggleFavorite);
  exportButton.addEventListener('click', exportResults);
  configButton.addEventListener('click', showConfigDialog);
  dataSourceSelect.addEventListener('change', onDataSourceChange);
  queryNameInput.addEventListener('change', updateQueryName);

  // Check for query ID in URL
  const urlParams = new URLSearchParams(window.location.search);
  const queryId = urlParams.get('id');
  if (queryId) {
    loadQuery(queryId);
  }
}

/**
 * Load available data sources
 */
function loadDataSources() {
  fetch('/api/datasources')
    .then(response => {
      if (!response.ok) {
        throw new Error('Failed to load data sources');
      }
      return response.json();
    })
    .then(apiResponse => {
      // 检查API响应是否成功
      if (!apiResponse.success) {
        throw new Error(apiResponse.message || 'Failed to load data sources');
      }

      // 获取实际的数据源列表
      const dataSources = apiResponse.data;

      // Clear existing options
      dataSourceSelect.innerHTML = '';

      // Add default option
      const defaultOption = document.createElement('option');
      defaultOption.value = '';
      defaultOption.textContent = '选择数据源';
      dataSourceSelect.appendChild(defaultOption);

      // 只添加活跃状态的数据源
      const activeSources = dataSources.filter(ds => ds.status === 'ACTIVE');

      // Add data sources
      activeSources.forEach(dataSource => {
        const option = document.createElement('option');
        option.value = dataSource.id;
        option.textContent = dataSource.name;
        dataSourceSelect.appendChild(option);
      });

      // Select first data source if available
      if (activeSources.length > 0 && !currentDataSourceId) {
        dataSourceSelect.value = activeSources[0].id;
        currentDataSourceId = activeSources[0].id;
      } else if (currentDataSourceId) {
        dataSourceSelect.value = currentDataSourceId;
      }
    })
    .catch(error => {
      showNotification('加载数据源失败: ' + error.message, 'error');
    });
}

/**
 * Handle data source change
 */
function onDataSourceChange() {
  currentDataSourceId = dataSourceSelect.value;
}

/**
 * Update query name
 */
function updateQueryName() {
  currentQueryName = queryNameInput.value;
}

/**
 * Execute the current SQL query
 */
function executeQuery() {
  if (isExecuting) {
    return; // Prevent multiple executions
  }

  const sql = editor.getValue().trim();
  if (!sql) {
    showNotification('Please enter a SQL query', 'warning');
    return;
  }

  if (!currentDataSourceId) {
    showNotification('Please select a data source', 'warning');
    return;
  }

  // Show loading indicator
  isExecuting = true;
  loadingIndicator.classList.remove('hidden');
  statusMessage.textContent = 'Executing query...';
  statusMessage.classList.remove('hidden');
  executeButton.disabled = true;

  // Clear previous results
  clearResults();

  // Execute query
  fetch('/api/queries/execute', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': 'current-user' // 在实际应用中，这应该是实际的用户ID
    },
    body: JSON.stringify({
      dataSourceId: currentDataSourceId,
      sql: sql,
      parameters: {},
      page: currentPage - 1, // API使用0基索引
      size: pageSize,
      sortColumn: sortColumn,
      sortDirection: sortDirection
    })
  })
    .then(response => {
      if (!response.ok) {
        return response.json().then(errorData => {
          throw new Error(errorData.message || 'Query execution failed');
        });
      }
      return response.json();
    })
    .then(apiResponse => {
      // 检查API响应是否成功
      if (!apiResponse.success) {
        throw new Error(apiResponse.message || 'Query execution failed');
      }

      // 获取实际的查询结果数据
      const data = apiResponse.data;

      // Store result data
      resultData = data;

      // Display results
      displayResults(data);

      // Update status
      statusMessage.textContent = `查询执行成功。返回 ${data.totalRows} 条记录。`;
      statusMessage.classList.remove('text-red-500', 'hidden');
      statusMessage.classList.add('text-green-500');

      // Update pagination
      totalPages = Math.ceil(data.totalRows / pageSize);
      updatePagination();
    })
    .catch(error => {
      showNotification('Error executing query: ' + error.message, 'error');
      statusMessage.textContent = 'Error: ' + error.message;
      statusMessage.classList.remove('hidden');
      statusMessage.classList.add('text-red-500');
      resultsTable.classList.add('hidden');
    })
    .finally(() => {
      // Hide loading indicator
      isExecuting = false;
      loadingIndicator.classList.add('hidden');
      executeButton.disabled = false;
    });
}

/**
 * Display query results
 */
function displayResults(data) {
  if (!data || !data.columns || !data.rows) {
    resultsTable.classList.add('hidden');
    return;
  }

  // Show results table
  resultsTable.classList.remove('hidden');

  // Create header row
  resultsHeader.innerHTML = '';
  const headerRow = document.createElement('tr');

  data.columns.forEach(column => {
    const th = document.createElement('th');
    th.className = 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer';
    th.scope = 'col';

    // Create header content with sort icon
    const headerContent = document.createElement('div');
    headerContent.className = 'flex items-center';

    // 使用label或name作为显示名称
    const columnName = document.createElement('span');
    columnName.textContent = column.label || column.name;
    headerContent.appendChild(columnName);

    // Add sort icon if this is the sort column
    if (sortColumn === column.name) {
      const sortIcon = document.createElement('i');
      sortIcon.className = `fas fa-sort-${sortDirection === 'asc' ? 'up' : 'down'} ml-1`;
      headerContent.appendChild(sortIcon);
    }

    th.appendChild(headerContent);

    // 只有可排序的列才添加排序事件
    if (column.sortable !== false) {
      th.addEventListener('click', () => {
        if (sortColumn === column.name) {
          // Toggle direction if already sorting by this column
          sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
          // Set new sort column
          sortColumn = column.name;
          sortDirection = 'asc';
        }

        // Re-execute query with new sort
        executeQuery();
      });
    } else {
      th.classList.remove('cursor-pointer');
    }

    headerRow.appendChild(th);
  });

  resultsHeader.appendChild(headerRow);

  // Create data rows
  resultsBody.innerHTML = '';

  // 如果没有数据，显示空状态
  if (data.rows.length === 0) {
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    td.colSpan = data.columns.length;
    td.className = 'px-6 py-4 text-center text-sm text-gray-500';
    td.textContent = '没有查询结果';
    tr.appendChild(td);
    resultsBody.appendChild(tr);
    return;
  }

  data.rows.forEach(row => {
    const tr = document.createElement('tr');
    tr.className = 'hover:bg-gray-50';

    data.columns.forEach(column => {
      const td = document.createElement('td');
      td.className = 'px-6 py-4 whitespace-nowrap text-sm text-gray-500';

      // 使用列名作为字段名
      const fieldName = column.name;
      const value = row[fieldName];

      // Format cell value based on column type
      if (value === null || value === undefined) {
        td.innerHTML = '<span class="text-gray-300">NULL</span>';
      } else if (column.dataType === 'DATE' || column.dataType === 'TIMESTAMP' ||
        column.type === 'DATE' || column.type === 'TIMESTAMP') {
        td.textContent = formatDate(value);
      } else if (column.dataType === 'NUMERIC' || column.dataType === 'DECIMAL' ||
        column.type === 'NUMERIC' || column.type === 'DECIMAL') {
        td.textContent = formatNumber(value);
      } else {
        td.textContent = value;
      }

      tr.appendChild(td);
    });

    resultsBody.appendChild(tr);
  });

  // 更新分页信息
  const paginationInfo = document.getElementById('paginationInfo');
  if (paginationInfo) {
    const start = (currentPage - 1) * pageSize + 1;
    const end = Math.min(start + pageSize - 1, data.totalRows);
    paginationInfo.textContent = `显示 ${start} 到 ${end} 条，共 ${data.totalRows} 条结果`;
  }
}

/**
 * Update pagination controls
 */
function updatePagination() {
  paginationContainer.innerHTML = '';

  if (totalPages <= 1) {
    return;
  }

  // Previous button
  const prevButton = createPaginationButton('Previous', currentPage > 1);
  prevButton.classList.add('rounded-l-md');
  if (currentPage > 1) {
    prevButton.addEventListener('click', () => goToPage(currentPage - 1));
  }
  paginationContainer.appendChild(prevButton);

  // Page buttons
  const maxVisiblePages = 5;
  let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
  let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

  if (endPage - startPage + 1 < maxVisiblePages) {
    startPage = Math.max(1, endPage - maxVisiblePages + 1);
  }

  for (let i = startPage; i <= endPage; i++) {
    const pageButton = document.createElement('a');
    pageButton.href = '#';
    pageButton.className = `relative inline-flex items-center px-4 py-2 border text-sm font-medium ${
      i === currentPage
        ? 'z-10 bg-indigo-50 border-indigo-500 text-indigo-600'
        : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50'
    }`;
    pageButton.textContent = i.toString();

    if (i !== currentPage) {
      pageButton.addEventListener('click', (e) => {
        e.preventDefault();
        goToPage(i);
      });
    }

    paginationContainer.appendChild(pageButton);
  }

  // Next button
  const nextButton = createPaginationButton('Next', currentPage < totalPages);
  nextButton.classList.add('rounded-r-md');
  if (currentPage < totalPages) {
    nextButton.addEventListener('click', () => goToPage(currentPage + 1));
  }
  paginationContainer.appendChild(nextButton);
}

/**
 * Create a pagination button
 */
function createPaginationButton(label, isEnabled) {
  const button = document.createElement('a');
  button.href = '#';
  button.className = `relative inline-flex items-center px-2 py-2 border border-gray-300 ${
    isEnabled ? 'bg-white text-gray-500 hover:bg-gray-50' : 'bg-gray-100 text-gray-300 cursor-not-allowed'
  }`;

  const span = document.createElement('span');
  span.className = 'sr-only';
  span.textContent = label;
  button.appendChild(span);

  const icon = document.createElement('i');
  icon.className = `fas fa-chevron-${label === 'Previous' ? 'left' : 'right'}`;
  button.appendChild(icon);

  if (!isEnabled) {
    button.addEventListener('click', (e) => e.preventDefault());
  }

  return button;
}

/**
 * Go to a specific page
 */
function goToPage(page) {
  if (page < 1 || page > totalPages || page === currentPage) {
    return;
  }

  currentPage = page;
  executeQuery();
}

/**
 * Clear results
 */
function clearResults() {
  resultsHeader.innerHTML = '';
  resultsBody.innerHTML = '';
  paginationContainer.innerHTML = '';
  resultsTable.classList.add('hidden');
}

/**
 * Save the current query
 */
function saveQuery() {
  const sql = editor.getValue().trim();
  if (!sql) {
    showNotification('请输入SQL查询', 'warning');
    return;
  }

  if (!currentQueryName) {
    showNotification('请输入查询名称', 'warning');
    return;
  }

  if (!currentDataSourceId) {
    showNotification('请选择数据源', 'warning');
    return;
  }

  const queryData = {
    id: currentQueryId,
    name: currentQueryName,
    description: currentQueryDescription,
    dataSourceId: currentDataSourceId,
    sql: sql
  };

  const method = currentQueryId ? 'PUT' : 'POST';
  const url = currentQueryId ? `/api/queries/${currentQueryId}` : '/api/queries';

  fetch(url, {
    method: method,
    headers: {
      'Content-Type': 'application/json',
      'X-User-Id': 'current-user' // 在实际应用中，这应该是实际的用户ID
    },
    body: JSON.stringify(queryData)
  })
    .then(response => {
      if (!response.ok) {
        return response.json().then(errorData => {
          throw new Error(errorData.message || 'Failed to save query');
        });
      }
      return response.json();
    })
    .then(apiResponse => {
      // 检查API响应是否成功
      if (!apiResponse.success) {
        throw new Error(apiResponse.message || 'Failed to save query');
      }

      // 获取保存的查询数据
      const data = apiResponse.data;

      currentQueryId = data.id;
      showNotification('查询保存成功', 'success');

      // Update URL with query ID
      const url = new URL(window.location);
      url.searchParams.set('id', currentQueryId);
      window.history.pushState({}, '', url);
    })
    .catch(error => {
      showNotification('保存查询失败: ' + error.message, 'error');
    });
}

/**
 * Load a query by ID
 */
function loadQuery(queryId) {
  fetch(`/api/queries/${queryId}`)
    .then(response => {
      if (!response.ok) {
        throw new Error('Query not found');
      }
      return response.json();
    })
    .then(apiResponse => {
      // 检查API响应是否成功
      if (!apiResponse.success) {
        throw new Error(apiResponse.message || 'Query not found');
      }

      // 获取查询数据
      const data = apiResponse.data;

      currentQueryId = data.id;
      currentQueryName = data.name;
      currentQueryDescription = data.description || '';
      currentDataSourceId = data.dataSourceId;

      // Update UI
      queryNameInput.value = currentQueryName;
      editor.setValue(data.sql);

      // Select data source
      if (dataSourceSelect.querySelector(`option[value="${currentDataSourceId}"]`)) {
        dataSourceSelect.value = currentDataSourceId;
      } else {
        // 数据源可能尚未加载，将在加载后选择
        // 或者数据源可能已被删除或停用
        showNotification('警告：查询关联的数据源可能不可用', 'warning');
      }

      showNotification('查询加载成功', 'success');
    })
    .catch(error => {
      showNotification('加载查询失败: ' + error.message, 'error');
    });
}

/**
 * Toggle favorite status
 */
function toggleFavorite() {
  if (!currentQueryId) {
    showNotification('请先保存查询', 'warning');
    return;
  }

  fetch(`/api/queries/${currentQueryId}/favorite`, {
    method: 'POST',
    headers: {
      'X-User-Id': 'current-user' // 在实际应用中，这应该是实际的用户ID
    }
  })
    .then(response => {
      if (!response.ok) {
        throw new Error('Failed to update favorite status');
      }
      return response.json();
    })
    .then(apiResponse => {
      // 检查API响应是否成功
      if (!apiResponse.success) {
        throw new Error(apiResponse.message || 'Failed to update favorite status');
      }

      // 获取收藏状态
      const data = apiResponse.data;
      const isFavorite = data.favorite;

      // 更新按钮文本
      favoriteButton.innerHTML = `<i class="fas fa-star mr-2"></i>${isFavorite ? '取消收藏' : '收藏'}`;

      showNotification(`查询已${isFavorite ? '添加到' : '从'}收藏夹${isFavorite ? '' : '中移除'}`, 'success');
    })
    .catch(error => {
      showNotification('更新收藏状态失败: ' + error.message, 'error');
    });
}

/**
 * Export results to CSV
 */
function exportResults() {
  if (!resultData || !resultData.columns || !resultData.rows || resultData.rows.length === 0) {
    showNotification('No results to export', 'warning');
    return;
  }

  // Create CSV content
  const headers = resultData.columns.map(col => col.name);
  const csvContent = [
    headers.join(','),
    ...resultData.rows.map(row =>
      headers.map(header => {
        const value = row[header];
        // Handle null values and escape commas
        if (value === null || value === undefined) {
          return '';
        } else if (typeof value === 'string' && value.includes(',')) {
          return `"${value}"`;
        } else {
          return value;
        }
      }).join(',')
    )
  ].join('\n');

  // Create download link
  const blob = new Blob([csvContent], {type: 'text/csv;charset=utf-8;'});
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `query_results_${new Date().toISOString().slice(0, 10)}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

/**
 * Show configuration dialog
 */
function showConfigDialog() {
  // Implementation for configuration dialog
  // This would include options for:
  // - Page size
  // - Date/number formatting
  // - Column visibility
  // - etc.
  alert('Configuration dialog not implemented yet');
}

/**
 * Format a date value
 */
function formatDate(value) {
  if (!value) return '';
  try {
    const date = new Date(value);
    return date.toLocaleString();
  } catch (e) {
    return value;
  }
}

/**
 * Format a number value
 */
function formatNumber(value) {
  if (value === null || value === undefined) return '';
  try {
    return Number(value).toLocaleString();
  } catch (e) {
    return value;
  }
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
  // Create notification element
  const notification = document.createElement('div');
  notification.className = `fixed bottom-4 right-4 px-4 py-2 rounded-lg shadow-lg flex items-center space-x-2 ${
    type === 'error' ? 'bg-red-500 text-white' :
      type === 'success' ? 'bg-green-500 text-white' :
        type === 'warning' ? 'bg-yellow-500 text-white' :
          'bg-blue-500 text-white'
  }`;

  // Icon
  const icon = document.createElement('i');
  icon.className = `fas ${
    type === 'error' ? 'fa-exclamation-circle' :
      type === 'success' ? 'fa-check-circle' :
        type === 'warning' ? 'fa-exclamation-triangle' :
          'fa-info-circle'
  }`;
  notification.appendChild(icon);

  // Message
  const messageEl = document.createElement('span');
  messageEl.textContent = message;
  notification.appendChild(messageEl);

  // Close button
  const closeButton = document.createElement('button');
  closeButton.className = 'ml-2 text-white';
  closeButton.innerHTML = '<i class="fas fa-times"></i>';
  closeButton.addEventListener('click', () => {
    document.body.removeChild(notification);
  });
  notification.appendChild(closeButton);

  // Add to body
  document.body.appendChild(notification);

  // Auto-remove after 5 seconds
  setTimeout(() => {
    if (document.body.contains(notification)) {
      document.body.removeChild(notification);
    }
  }, 5000);
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', initQueryEditor);
