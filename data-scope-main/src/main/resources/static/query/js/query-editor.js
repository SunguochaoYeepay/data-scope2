/**
 * DataScope Query Editor JavaScript
 *
 * This file contains the functionality for the query editor page,
 * including SQL execution, result display, and query management.
 */

// API Base URL
const API_BASE_URL = '/api';

// Query API Endpoints
const QUERY_API = {
  EXECUTE: `${API_BASE_URL}/queries/execute`,
  VALIDATE: `${API_BASE_URL}/queries/validate`,
  METADATA: `${API_BASE_URL}/queries/metadata`,
  ESTIMATE: `${API_BASE_URL}/queries/estimate`,
  CANCEL: (id) => `${API_BASE_URL}/queries/${id}/cancel`,
  SAVE: `${API_BASE_URL}/queries`,
  GET: (id) => `${API_BASE_URL}/queries/${id}`,
  FAVORITE: (id) => `${API_BASE_URL}/queries/${id}/favorite`
};

// Data Source API Endpoints
const DATASOURCE_API = {
  LIST: `${API_BASE_URL}/datasources`,
  GET: (id) => `${API_BASE_URL}/datasources/${id}`
};

// DOM Elements
const queryForm = document.getElementById('query-form');
const sqlEditor = document.getElementById('sql-editor');
const dataSourceSelect = document.getElementById('data-source');
const executeButton = document.getElementById('execute-button');
const cancelButton = document.getElementById('cancel-button');
const saveButton = document.getElementById('save-button');
const favoriteButton = document.getElementById('favorite-button');
const queryNameInput = document.getElementById('query-name');
const queryDescriptionInput = document.getElementById('query-description');
const resultsContainer = document.getElementById('results-container');
const resultsTable = document.getElementById('results-table');
const resultsTableHead = document.querySelector('#results-table thead');
const resultsTableBody = document.querySelector('#results-table tbody');
const paginationContainer = document.getElementById('pagination');
const paginationInfo = document.getElementById('pagination-info');
const loadingIndicator = document.getElementById('loading-indicator');
const errorContainer = document.getElementById('error-container');
const errorMessage = document.getElementById('error-message');
const successContainer = document.getElementById('success-container');
const successMessage = document.getElementById('success-message');

// State
let currentQuery = {
  id: null,
  name: '',
  description: '',
  sql: '',
  dataSourceId: '',
  favorite: false,
  parameters: {}
};
let currentExecution = {
  id: null,
  status: null
};
let queryResults = null;
let currentPage = 1;
let pageSize = 100;
let sortFields = [];
let isExecuting = false;

/**
 * Initialize the query editor
 */
function initQueryEditor() {
  // Load data sources
  loadDataSources();

  // Set up event listeners
  if (queryForm) {
    queryForm.addEventListener('submit', handleExecuteQuery);
  }

  if (executeButton) {
    executeButton.addEventListener('click', handleExecuteQuery);
  }

  if (cancelButton) {
    cancelButton.addEventListener('click', handleCancelQuery);
  }

  if (saveButton) {
    saveButton.addEventListener('click', handleSaveQuery);
  }

  if (favoriteButton) {
    favoriteButton.addEventListener('click', handleToggleFavorite);
  }

  // Check if we're editing an existing query
  const urlParams = new URLSearchParams(window.location.search);
  const queryId = urlParams.get('id');

  if (queryId) {
    loadQuery(queryId);
  }

  // Set up keyboard shortcuts
  document.addEventListener('keydown', (e) => {
    // Ctrl+Enter or Cmd+Enter to execute query
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
      e.preventDefault();
      handleExecuteQuery(e);
    }
  });
}

/**
 * Load data sources from API
 */
async function loadDataSources() {
  try {
    const response = await fetch(DATASOURCE_API.LIST, {
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to load data sources');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to load data sources');
    }

    // Get data from API response
    const dataSources = result.data;

    // Clear existing options
    dataSourceSelect.innerHTML = '<option value="">选择数据源</option>';

    // Add data sources to dropdown
    dataSources.forEach(dataSource => {
      if (dataSource.status === 'ACTIVE') {
        const option = document.createElement('option');
        option.value = dataSource.id;
        option.textContent = dataSource.name;
        dataSourceSelect.appendChild(option);
      }
    });
  } catch (error) {
    console.error('Error loading data sources:', error);
    showError('加载数据源失败: ' + error.message);
  }
}

/**
 * Load query from API
 */
async function loadQuery(queryId) {
  try {
    showLoading();

    const response = await fetch(QUERY_API.GET(queryId), {
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to load query');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to load query');
    }

    // Get data from API response
    const query = result.data;

    // Update state
    currentQuery = {
      id: query.id,
      name: query.name,
      description: query.description,
      sql: query.sql,
      dataSourceId: query.dataSourceId,
      favorite: query.favorite,
      parameters: query.parameters || {}
    };

    // Update UI
    queryNameInput.value = currentQuery.name;
    queryDescriptionInput.value = currentQuery.description;
    sqlEditor.value = currentQuery.sql;
    dataSourceSelect.value = currentQuery.dataSourceId;

    // Update favorite button
    updateFavoriteButton(currentQuery.favorite);

    hideLoading();
  } catch (error) {
    console.error('Error loading query:', error);
    showError('加载查询失败: ' + error.message);
    hideLoading();
  }
}

/**
 * Handle execute query
 */
async function handleExecuteQuery(e) {
  e.preventDefault();

  if (isExecuting) {
    return;
  }

  const sql = sqlEditor.value.trim();
  const dataSourceId = dataSourceSelect.value;

  if (!sql) {
    showError('请输入SQL查询');
    return;
  }

  if (!dataSourceId) {
    showError('请选择数据源');
    return;
  }

  try {
    isExecuting = true;
    showLoading();
    updateExecuteButtonState(true);

    // Clear previous results
    clearResults();

    // Prepare request
    const request = {
      sql: sql,
      dataSourceId: dataSourceId,
      parameters: currentQuery.parameters || {},
      page: currentPage - 1, // API uses 0-based indexing
      size: pageSize,
      sortFields: sortFields
    };

    // Execute query
    const response = await fetch(QUERY_API.EXECUTE, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      },
      body: JSON.stringify(request)
    });

    if (!response.ok) {
      throw new Error('查询执行失败');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || '查询执行失败');
    }

    // Get data from API response
    const data = result.data;

    // Update current execution
    currentExecution = {
      id: data.executionId,
      status: data.status
    };

    // Update query results
    queryResults = data;

    // Display results
    displayResults(data);

    // Show success message
    showSuccess('查询执行成功');

    isExecuting = false;
    hideLoading();
    updateExecuteButtonState(false);
  } catch (error) {
    console.error('Error executing query:', error);
    showError('查询执行失败: ' + error.message);
    isExecuting = false;
    hideLoading();
    updateExecuteButtonState(false);
  }
}

/**
 * Handle cancel query
 */
async function handleCancelQuery() {
  if (!currentExecution.id || !isExecuting) {
    return;
  }

  try {
    const response = await fetch(QUERY_API.CANCEL(currentExecution.id), {
      method: 'POST',
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to cancel query');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to cancel query');
    }

    // Show success message
    showSuccess('查询已取消');

    isExecuting = false;
    updateExecuteButtonState(false);
  } catch (error) {
    console.error('Error cancelling query:', error);
    showError('取消查询失败: ' + error.message);
  }
}

/**
 * Handle save query
 */
async function handleSaveQuery() {
  const name = queryNameInput.value.trim();
  const description = queryDescriptionInput.value.trim();
  const sql = sqlEditor.value.trim();
  const dataSourceId = dataSourceSelect.value;

  if (!name) {
    showError('请输入查询名称');
    return;
  }

  if (!sql) {
    showError('请输入SQL查询');
    return;
  }

  if (!dataSourceId) {
    showError('请选择数据源');
    return;
  }

  try {
    showLoading();

    // Prepare request
    const request = {
      id: currentQuery.id, // Will be null for new queries
      name: name,
      description: description,
      sql: sql,
      dataSourceId: dataSourceId,
      parameters: currentQuery.parameters || {}
    };

    // Save query
    const response = await fetch(QUERY_API.SAVE, {
      method: currentQuery.id ? 'PUT' : 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      },
      body: JSON.stringify(request)
    });

    if (!response.ok) {
      throw new Error('Failed to save query');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to save query');
    }

    // Get data from API response
    const savedQuery = result.data;

    // Update state
    currentQuery = {
      id: savedQuery.id,
      name: savedQuery.name,
      description: savedQuery.description,
      sql: savedQuery.sql,
      dataSourceId: savedQuery.dataSourceId,
      favorite: savedQuery.favorite,
      parameters: savedQuery.parameters || {}
    };

    // Update URL if this is a new query
    if (!request.id) {
      window.history.replaceState({}, '', `/query/editor.html?id=${savedQuery.id}`);
    }

    // Show success message
    showSuccess('查询已保存');

    hideLoading();
  } catch (error) {
    console.error('Error saving query:', error);
    showError('保存查询失败: ' + error.message);
    hideLoading();
  }
}

/**
 * Handle toggle favorite
 */
async function handleToggleFavorite() {
  if (!currentQuery.id) {
    showError('请先保存查询');
    return;
  }

  try {
    const response = await fetch(QUERY_API.FAVORITE(currentQuery.id), {
      method: 'POST',
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to update favorite status');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to update favorite status');
    }

    // Get data from API response
    const data = result.data;

    // Update state
    currentQuery.favorite = data.favorite;

    // Update UI
    updateFavoriteButton(currentQuery.favorite);

    // Show success message
    showSuccess(`查询已${currentQuery.favorite ? '添加到' : '从'}收藏夹${currentQuery.favorite ? '' : '中移除'}`);
  } catch (error) {
    console.error('Error updating favorite status:', error);
    showError('更新收藏状态失败: ' + error.message);
  }
}

/**
 * Display query results
 */
function displayResults(data) {
  if (!data) {
    return;
  }

  // Show results container
  resultsContainer.classList.remove('hidden');

  // Get columns and rows
  const columns = data.columns || [];
  const rows = data.rows || [];

  // Clear existing table
  resultsTableHead.innerHTML = '';
  resultsTableBody.innerHTML = '';

  // Create table header
  const headerRow = document.createElement('tr');

  columns.forEach(column => {
    const th = document.createElement('th');
    th.className = 'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider';
    th.textContent = column.name;

    // Add sort functionality if column is sortable
    if (column.sortable) {
      th.classList.add('cursor-pointer', 'hover:bg-gray-100');

      // Check if column is already sorted
      const sortField = sortFields.find(field => field.field === column.name);

      if (sortField) {
        th.classList.add(sortField.direction === 'asc' ? 'sorted-asc' : 'sorted-desc');
        th.innerHTML = `${column.name} <i class="fas fa-sort-${sortField.direction === 'asc' ? 'up' : 'down'} ml-1"></i>`;
      } else {
        th.innerHTML = `${column.name} <i class="fas fa-sort ml-1 text-gray-300"></i>`;
      }

      th.addEventListener('click', () => handleSort(column.name));
    }

    headerRow.appendChild(th);
  });

  resultsTableHead.appendChild(headerRow);

  // Create table body
  if (rows.length === 0) {
    const emptyRow = document.createElement('tr');
    const emptyCell = document.createElement('td');
    emptyCell.className = 'px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center';
    emptyCell.colSpan = columns.length;
    emptyCell.textContent = '没有数据';
    emptyRow.appendChild(emptyCell);
    resultsTableBody.appendChild(emptyRow);
  } else {
    rows.forEach(row => {
      const tr = document.createElement('tr');
      tr.className = 'hover:bg-gray-50';

      columns.forEach(column => {
        const td = document.createElement('td');
        td.className = 'px-6 py-4 whitespace-nowrap text-sm text-gray-500';

        const value = row[column.name];
        td.textContent = value !== null && value !== undefined ? value : '';

        tr.appendChild(td);
      });

      resultsTableBody.appendChild(tr);
    });
  }

  // Update pagination
  updatePagination(data);
}

/**
 * Update pagination
 */
function updatePagination(data) {
  if (!data) {
    return;
  }

  // Get pagination info
  const totalRows = data.totalRows || 0;
  const totalPages = Math.ceil(totalRows / pageSize);
  const hasMore = data.hasMore || false;

  // Update pagination info text
    const start = (currentPage - 1) * pageSize + 1;
  const end = Math.min(start + pageSize - 1, totalRows);

  if (paginationInfo) {
    paginationInfo.textContent = `显示 ${start} 到 ${end} 条，共 ${totalRows} 条结果`;
  }

  // Clear existing pagination controls
  if (paginationContainer) {
    paginationContainer.innerHTML = '';

    // Don't show pagination if there's only one page
    if (totalPages <= 1) {
      return;
    }

    // Previous button
    const prevButton = document.createElement('a');
    prevButton.href = '#';
    prevButton.className = `relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium ${currentPage > 1 ? 'text-gray-500 hover:bg-gray-50' : 'text-gray-300 cursor-not-allowed'}`;
    prevButton.innerHTML = '<span class="sr-only">上一页</span><i class="fas fa-chevron-left"></i>';

    if (currentPage > 1) {
      prevButton.addEventListener('click', (e) => {
        e.preventDefault();
        goToPage(currentPage - 1);
      });
    }

    paginationContainer.appendChild(prevButton);

    // Page numbers
    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      const pageButton = document.createElement('a');
      pageButton.href = '#';
      pageButton.className = `relative inline-flex items-center px-4 py-2 border ${i === currentPage ? 'bg-indigo-50 border-indigo-500 text-indigo-600 z-10' : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50'}`;
      pageButton.textContent = i;

      if (i !== currentPage) {
        pageButton.addEventListener('click', (e) => {
          e.preventDefault();
          goToPage(i);
        });
      }

      paginationContainer.appendChild(pageButton);
    }

    // Next button
    const nextButton = document.createElement('a');
    nextButton.href = '#';
    nextButton.className = `relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium ${currentPage < totalPages ? 'text-gray-500 hover:bg-gray-50' : 'text-gray-300 cursor-not-allowed'}`;
    nextButton.innerHTML = '<span class="sr-only">下一页</span><i class="fas fa-chevron-right"></i>';

    if (currentPage < totalPages) {
      nextButton.addEventListener('click', (e) => {
        e.preventDefault();
        goToPage(currentPage + 1);
      });
    }

    paginationContainer.appendChild(nextButton);
  }
}

/**
 * Go to a specific page
 */
function goToPage(page) {
  if (page < 1 || page === currentPage) {
    return;
  }

  currentPage = page;
  handleExecuteQuery(new Event('click'));
}

/**
 * Handle sort
 */
function handleSort(columnName) {
  // Check if column is already sorted
  const existingSortIndex = sortFields.findIndex(field => field.field === columnName);

  if (existingSortIndex !== -1) {
    // Toggle direction
    if (sortFields[existingSortIndex].direction === 'asc') {
      sortFields[existingSortIndex].direction = 'desc';
    } else {
      // Remove sort if already desc
      sortFields.splice(existingSortIndex, 1);
    }
  } else {
    // Add new sort
    sortFields.push({
      field: columnName,
      direction: 'asc'
    });
  }

  // Reset to first page
  currentPage = 1;

  // Re-execute query with new sort
  handleExecuteQuery(new Event('click'));
}

/**
 * Clear results
 */
function clearResults() {
  resultsContainer.classList.add('hidden');
  resultsTableHead.innerHTML = '';
  resultsTableBody.innerHTML = '';

  if (paginationContainer) {
    paginationContainer.innerHTML = '';
  }

  if (paginationInfo) {
    paginationInfo.textContent = '';
  }
}

/**
 * Update favorite button
 */
function updateFavoriteButton(isFavorite) {
  if (favoriteButton) {
    if (isFavorite) {
      favoriteButton.classList.remove('bg-gray-200', 'hover:bg-gray-300');
      favoriteButton.classList.add('bg-yellow-500', 'hover:bg-yellow-600');
      favoriteButton.querySelector('i').classList.remove('text-gray-600');
      favoriteButton.querySelector('i').classList.add('text-white');
    } else {
      favoriteButton.classList.remove('bg-yellow-500', 'hover:bg-yellow-600');
      favoriteButton.classList.add('bg-gray-200', 'hover:bg-gray-300');
      favoriteButton.querySelector('i').classList.remove('text-white');
      favoriteButton.querySelector('i').classList.add('text-gray-600');
    }
  }
}

/**
 * Update execute button state
 */
function updateExecuteButtonState(isExecuting) {
  if (executeButton) {
    if (isExecuting) {
      executeButton.disabled = true;
      executeButton.classList.add('opacity-50', 'cursor-not-allowed');
      executeButton.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i> 执行中...';
        } else {
      executeButton.disabled = false;
      executeButton.classList.remove('opacity-50', 'cursor-not-allowed');
      executeButton.innerHTML = '<i class="fas fa-play mr-2"></i> 执行';
        }
  }
}

/**
 * Show loading indicator
 */
function showLoading() {
  if (loadingIndicator) {
    loadingIndicator.classList.remove('hidden');
  }
}

/**
 * Hide loading indicator
 */
function hideLoading() {
  if (loadingIndicator) {
    loadingIndicator.classList.add('hidden');
  }
}

/**
 * Show error message
 */
function showError(message) {
  if (errorContainer && errorMessage) {
    errorMessage.textContent = message;
    errorContainer.classList.remove('hidden');

    // Hide after 5 seconds
    setTimeout(() => {
      errorContainer.classList.add('hidden');
    }, 5000);
    }
}

/**
 * Show success message
 */
function showSuccess(message) {
  if (successContainer && successMessage) {
    successMessage.textContent = message;
    successContainer.classList.remove('hidden');

    // Hide after 5 seconds
    setTimeout(() => {
      successContainer.classList.add('hidden');
    }, 5000);
  }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', initQueryEditor);
