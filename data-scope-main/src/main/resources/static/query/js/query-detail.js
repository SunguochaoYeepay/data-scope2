/**
 * DataScope Query Detail JavaScript
 *
 * This file contains the functionality for the query detail page,
 * including query information display and execution history.
 */

// API Base URL
const API_BASE_URL = '/api';

// Query API Endpoints
const QUERY_API = {
  GET: (id) => `${API_BASE_URL}/queries/${id}`,
  HISTORY: (id) => `${API_BASE_URL}/queries/${id}/history`,
  FAVORITE: (id) => `${API_BASE_URL}/queries/${id}/favorite`,
  SHARE: (id) => `${API_BASE_URL}/queries/${id}/share`,
  CLONE: (id) => `${API_BASE_URL}/queries/${id}/clone`,
  DELETE: (id) => `${API_BASE_URL}/queries/${id}`
};

// DOM Elements
const queryTitle = document.getElementById('query-title');
const queryDescription = document.getElementById('query-description');
const queryDataSource = document.getElementById('query-datasource');
const querySql = document.getElementById('query-sql');
const queryCreatedBy = document.getElementById('query-created-by');
const queryCreatedTime = document.getElementById('query-created-time');
const queryModifiedBy = document.getElementById('query-modified-by');
const queryModifiedTime = document.getElementById('query-modified-time');
const queryTags = document.getElementById('query-tags');
const favoriteButton = document.getElementById('favorite-button');
const shareButton = document.getElementById('share-button');
const cloneButton = document.getElementById('clone-button');
const editButton = document.getElementById('edit-button');
const deleteButton = document.getElementById('delete-button');
const executeButton = document.getElementById('execute-button');
const historyTable = document.getElementById('history-table');
const historyTableBody = document.getElementById('history-table-body');
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
  dataSourceName: '',
  favorite: false,
  tags: [],
  createdBy: '',
  createdTime: null,
  modifiedBy: '',
  modifiedTime: null
};

/**
 * Initialize the query detail page
 */
function initQueryDetail() {
  // Get query ID from URL
  const urlParams = new URLSearchParams(window.location.search);
  const queryId = urlParams.get('id');

  if (!queryId) {
    showError('查询ID不能为空');
    return;
  }

  // Load query details
  loadQuery(queryId);

  // Load query execution history
  loadQueryHistory(queryId);

  // Set up event listeners
  if (favoriteButton) {
    favoriteButton.addEventListener('click', handleToggleFavorite);
  }

  if (shareButton) {
    shareButton.addEventListener('click', handleShareQuery);
  }

  if (cloneButton) {
    cloneButton.addEventListener('click', handleCloneQuery);
  }

  if (editButton) {
    editButton.addEventListener('click', () => {
      window.location.href = `/query/editor.html?id=${currentQuery.id}`;
    });
  }

  if (deleteButton) {
    deleteButton.addEventListener('click', handleDeleteQuery);
  }

  if (executeButton) {
    executeButton.addEventListener('click', () => {
      window.location.href = `/query/editor.html?id=${currentQuery.id}&execute=true`;
    });
  }
}

/**
 * Load query details from API
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
      description: query.description || '',
      sql: query.sql,
      dataSourceId: query.dataSourceId,
      dataSourceName: query.dataSourceName || '',
      favorite: query.favorite || false,
      tags: query.tags || [],
      createdBy: query.createdBy || '',
      createdTime: query.createdTime ? new Date(query.createdTime) : null,
      modifiedBy: query.modifiedBy || '',
      modifiedTime: query.modifiedTime ? new Date(query.modifiedTime) : null
    };

    // Update UI
    updateQueryDetails();

    hideLoading();
  } catch (error) {
    console.error('Error loading query:', error);
    showError('加载查询失败: ' + error.message);
    hideLoading();
  }
}

/**
 * Update query details in the UI
 */
function updateQueryDetails() {
  // Update query title
  if (queryTitle) {
    queryTitle.textContent = currentQuery.name;
  }

  // Update query description
  if (queryDescription) {
    queryDescription.textContent = currentQuery.description || '无描述';
  }

  // Update query data source
  if (queryDataSource) {
    queryDataSource.textContent = currentQuery.dataSourceName || '未知数据源';
  }

  // Update query SQL
  if (querySql) {
    querySql.textContent = currentQuery.sql;

    // Initialize syntax highlighting if CodeMirror is available
    if (window.CodeMirror) {
      const sqlEditor = CodeMirror.fromTextArea(querySql, {
        mode: 'text/x-sql',
        theme: 'monokai',
        lineNumbers: true,
        readOnly: true
      });
    }
  }

  // Update query created by
  if (queryCreatedBy) {
    queryCreatedBy.textContent = currentQuery.createdBy || '未知用户';
  }

  // Update query created time
  if (queryCreatedTime && currentQuery.createdTime) {
    queryCreatedTime.textContent = formatDateTime(currentQuery.createdTime);
  }

  // Update query modified by
  if (queryModifiedBy) {
    queryModifiedBy.textContent = currentQuery.modifiedBy || '未知用户';
  }

  // Update query modified time
  if (queryModifiedTime && currentQuery.modifiedTime) {
    queryModifiedTime.textContent = formatDateTime(currentQuery.modifiedTime);
  }

  // Update query tags
  if (queryTags) {
    queryTags.innerHTML = '';

    if (currentQuery.tags && currentQuery.tags.length > 0) {
      currentQuery.tags.forEach(tag => {
        const tagElement = document.createElement('span');
        tagElement.className = 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 mr-2';
        tagElement.textContent = tag;
        queryTags.appendChild(tagElement);
      });
    } else {
      const noTagsElement = document.createElement('span');
      noTagsElement.className = 'text-gray-500 text-sm';
      noTagsElement.textContent = '无标签';
      queryTags.appendChild(noTagsElement);
    }
  }

  // Update favorite button
  updateFavoriteButton(currentQuery.favorite);
}

/**
 * Load query execution history from API
 */
async function loadQueryHistory(queryId) {
  try {
    const response = await fetch(QUERY_API.HISTORY(queryId), {
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to load query history');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to load query history');
    }

    // Get data from API response
    const history = result.data;

    // Update UI
    updateQueryHistory(history);
  } catch (error) {
    console.error('Error loading query history:', error);
    showError('加载查询历史失败: ' + error.message);
  }
}

/**
 * Update query history in the UI
 */
function updateQueryHistory(history) {
  if (!historyTableBody) {
    return;
  }

  // Clear existing history
  historyTableBody.innerHTML = '';

  if (!history || history.length === 0) {
    const emptyRow = document.createElement('tr');
    const emptyCell = document.createElement('td');
    emptyCell.className = 'px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center';
    emptyCell.colSpan = 5;
    emptyCell.textContent = '无执行历史';
    emptyRow.appendChild(emptyCell);
    historyTableBody.appendChild(emptyRow);
    return;
  }

  // Add history rows
  history.forEach(execution => {
    const row = document.createElement('tr');
    row.className = 'hover:bg-gray-50';

    // Execution ID
    const idCell = document.createElement('td');
    idCell.className = 'px-6 py-4 whitespace-nowrap text-sm text-gray-500';
    idCell.textContent = execution.id.substring(0, 8) + '...';
    row.appendChild(idCell);

    // Execution time
    const timeCell = document.createElement('td');
    timeCell.className = 'px-6 py-4 whitespace-nowrap text-sm text-gray-500';
    timeCell.textContent = formatDateTime(new Date(execution.startTime));
    row.appendChild(timeCell);

    // Duration
    const durationCell = document.createElement('td');
    durationCell.className = 'px-6 py-4 whitespace-nowrap text-sm text-gray-500';
    durationCell.textContent = formatDuration(execution.executionTime);
    row.appendChild(durationCell);

    // Rows
    const rowsCell = document.createElement('td');
    rowsCell.className = 'px-6 py-4 whitespace-nowrap text-sm text-gray-500';
    rowsCell.textContent = execution.rowCount.toLocaleString();
    row.appendChild(rowsCell);

    // Status
    const statusCell = document.createElement('td');
    statusCell.className = 'px-6 py-4 whitespace-nowrap text-sm';

    const statusBadge = document.createElement('span');
    statusBadge.className = 'px-2 inline-flex text-xs leading-5 font-semibold rounded-full';

    switch (execution.status) {
      case 'COMPLETED':
        statusBadge.classList.add('bg-green-100', 'text-green-800');
        statusBadge.textContent = '成功';
        break;
      case 'FAILED':
        statusBadge.classList.add('bg-red-100', 'text-red-800');
        statusBadge.textContent = '失败';
        break;
      case 'CANCELLED':
        statusBadge.classList.add('bg-yellow-100', 'text-yellow-800');
        statusBadge.textContent = '已取消';
        break;
      case 'RUNNING':
        statusBadge.classList.add('bg-blue-100', 'text-blue-800');
        statusBadge.textContent = '运行中';
        break;
      default:
        statusBadge.classList.add('bg-gray-100', 'text-gray-800');
        statusBadge.textContent = execution.status;
    }

    statusCell.appendChild(statusBadge);
    row.appendChild(statusCell);

    // Add row to table
    historyTableBody.appendChild(row);
  });
}

/**
 * Handle toggle favorite
 */
async function handleToggleFavorite() {
  if (!currentQuery.id) {
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
 * Handle share query
 */
function handleShareQuery() {
  if (!currentQuery.id) {
    return;
  }

  // Create a shareable link
  const shareUrl = `${window.location.origin}/query/detail.html?id=${currentQuery.id}`;

  // Copy to clipboard
  navigator.clipboard.writeText(shareUrl)
    .then(() => {
      showSuccess('链接已复制到剪贴板');
    })
    .catch(error => {
      console.error('Error copying to clipboard:', error);
      showError('复制链接失败');
    });
}

/**
 * Handle clone query
 */
async function handleCloneQuery() {
  if (!currentQuery.id) {
    return;
  }

  try {
    showLoading();

    const response = await fetch(QUERY_API.CLONE(currentQuery.id), {
      method: 'POST',
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to clone query');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to clone query');
    }

    // Get data from API response
    const clonedQuery = result.data;

    // Show success message
    showSuccess('查询已克隆');

    // Redirect to the cloned query
    window.location.href = `/query/editor.html?id=${clonedQuery.id}`;
  } catch (error) {
    console.error('Error cloning query:', error);
    showError('克隆查询失败: ' + error.message);
    hideLoading();
  }
}

/**
 * Handle delete query
 */
async function handleDeleteQuery() {
  if (!currentQuery.id) {
    return;
  }

  // Confirm deletion
  if (!confirm('确定要删除此查询吗？此操作无法撤销。')) {
    return;
  }

  try {
    showLoading();

    const response = await fetch(QUERY_API.DELETE(currentQuery.id), {
      method: 'DELETE',
      headers: {
        'X-User-Id': 'current-user' // In a real app, this would be the actual user ID
      }
    });

    if (!response.ok) {
      throw new Error('Failed to delete query');
    }

    const result = await response.json();

    // Check if API response is successful
    if (!result.success) {
      throw new Error(result.message || 'Failed to delete query');
    }

    // Show success message
    showSuccess('查询已删除');

    // Redirect to query list
    setTimeout(() => {
      window.location.href = '/query/list.html';
    }, 1000);
  } catch (error) {
    console.error('Error deleting query:', error);
    showError('删除查询失败: ' + error.message);
    hideLoading();
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
 * Format date time
 */
function formatDateTime(date) {
  if (!date) {
    return '';
  }

  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).format(date);
}

/**
 * Format duration in milliseconds to human-readable format
 */
function formatDuration(milliseconds) {
  if (!milliseconds) {
    return '0 ms';
  }

  if (milliseconds < 1000) {
    return `${milliseconds} ms`;
  }

  const seconds = Math.floor(milliseconds / 1000);
  const ms = milliseconds % 1000;

  if (seconds < 60) {
    return `${seconds}.${ms} 秒`;
  }

  const minutes = Math.floor(seconds / 60);
  const secs = seconds % 60;

  return `${minutes}:${secs.toString().padStart(2, '0')} 分钟`;
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
document.addEventListener('DOMContentLoaded', initQueryDetail);
