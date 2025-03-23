# DataScope UI Design

## Design System

### Colors
```css
/* Primary Colors */
--primary-50: #e3f2fd;
--primary-100: #bbdefb;
--primary-200: #90caf9;
--primary-300: #64b5f6;
--primary-400: #42a5f5;
--primary-500: #2196f3;
--primary-600: #1e88e5;
--primary-700: #1976d2;
--primary-800: #1565c0;
--primary-900: #0d47a1;

/* Neutral Colors */
--neutral-50: #fafafa;
--neutral-100: #f5f5f5;
--neutral-200: #eeeeee;
--neutral-300: #e0e0e0;
--neutral-400: #bdbdbd;
--neutral-500: #9e9e9e;
--neutral-600: #757575;
--neutral-700: #616161;
--neutral-800: #424242;
--neutral-900: #212121;

/* Semantic Colors */
--success: #4caf50;
--warning: #ff9800;
--error: #f44336;
--info: #2196f3;
```

### Typography
```css
/* Font Family */
--font-family: 'Inter', system-ui, sans-serif;

/* Font Sizes */
--text-xs: 0.75rem;
--text-sm: 0.875rem;
--text-base: 1rem;
--text-lg: 1.125rem;
--text-xl: 1.25rem;
--text-2xl: 1.5rem;
--text-3xl: 1.875rem;
--text-4xl: 2.25rem;

/* Font Weights */
--font-normal: 400;
--font-medium: 500;
--font-semibold: 600;
--font-bold: 700;
```

### Spacing
```css
--space-1: 0.25rem;
--space-2: 0.5rem;
--space-3: 0.75rem;
--space-4: 1rem;
--space-5: 1.25rem;
--space-6: 1.5rem;
--space-8: 2rem;
--space-10: 2.5rem;
--space-12: 3rem;
--space-16: 4rem;
```

## Components

### Navigation
```html
<!-- Top Navigation Bar -->
<nav class="bg-white shadow">
  <div class="max-w-7xl mx-auto px-4">
    <div class="flex justify-between h-16">
      <div class="flex">
        <div class="flex-shrink-0 flex items-center">
          <img class="h-8 w-auto" src="/logo.svg" alt="DataScope">
        </div>
        <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
          <a href="#" class="border-primary-500 text-neutral-900 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
            Data Sources
          </a>
          <a href="#" class="border-transparent text-neutral-500 hover:border-neutral-300 hover:text-neutral-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
            Queries
          </a>
          <!-- More navigation items -->
        </div>
      </div>
    </div>
  </div>
</nav>
```

### Data Source List
```html
<!-- Data Source Grid -->
<div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
  <div class="px-4 py-6 sm:px-0">
    <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      <!-- Data Source Card -->
      <div class="bg-white overflow-hidden shadow rounded-lg">
        <div class="px-4 py-5 sm:p-6">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <svg class="h-6 w-6 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <!-- Database icon -->
              </svg>
            </div>
            <div class="ml-5 w-0 flex-1">
              <dl>
                <dt class="text-sm font-medium text-neutral-500 truncate">
                  Production MySQL
                </dt>
                <dd class="flex items-baseline">
                  <div class="text-2xl font-semibold text-neutral-900">
                    Connected
                  </div>
                </dd>
              </dl>
            </div>
          </div>
        </div>
        <div class="bg-neutral-50 px-4 py-4 sm:px-6">
          <div class="text-sm">
            <a href="#" class="font-medium text-primary-600 hover:text-primary-500">
              View details
            </a>
          </div>
        </div>
      </div>
      <!-- More data source cards -->
    </div>
  </div>
</div>
```

### Query Builder
```html
<!-- Query Builder Interface -->
<div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
  <div class="bg-white shadow sm:rounded-lg">
    <div class="px-4 py-5 sm:p-6">
      <!-- Query Tabs -->
      <div class="border-b border-neutral-200">
        <nav class="-mb-px flex space-x-8">
          <a href="#" class="border-primary-500 text-primary-600 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm">
            SQL Editor
          </a>
          <a href="#" class="border-transparent text-neutral-500 hover:text-neutral-700 hover:border-neutral-300 whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm">
            Natural Language
          </a>
        </nav>
      </div>

      <!-- SQL Editor -->
      <div class="mt-5">
        <textarea
          rows="8"
          class="shadow-sm focus:ring-primary-500 focus:border-primary-500 block w-full sm:text-sm border-neutral-300 rounded-md"
          placeholder="Enter your SQL query here..."
        ></textarea>
      </div>

      <!-- Action Buttons -->
      <div class="mt-5 flex justify-end space-x-3">
        <button type="button" class="inline-flex items-center px-4 py-2 border border-neutral-300 shadow-sm text-sm font-medium rounded-md text-neutral-700 bg-white hover:bg-neutral-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500">
          Save
        </button>
        <button type="button" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500">
          Execute
        </button>
      </div>
    </div>
  </div>
</div>
```

### Results Display
```html
<!-- Query Results Table -->
<div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
  <div class="flex flex-col">
    <div class="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
      <div class="py-2 align-middle inline-block min-w-full sm:px-6 lg:px-8">
        <div class="shadow overflow-hidden border-b border-neutral-200 sm:rounded-lg">
          <table class="min-w-full divide-y divide-neutral-200">
            <thead class="bg-neutral-50">
              <tr>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-neutral-500 uppercase tracking-wider">
                  Column 1
                </th>
                <!-- More column headers -->
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-neutral-200">
              <tr>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-neutral-900">
                  Data 1
                </td>
                <!-- More cells -->
              </tr>
              <!-- More rows -->
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>
```

### Configuration Forms
```html
<!-- Display Configuration Form -->
<div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
  <div class="bg-white shadow sm:rounded-lg">
    <div class="px-4 py-5 sm:p-6">
      <h3 class="text-lg leading-6 font-medium text-neutral-900">
        Display Configuration
      </h3>
      
      <!-- Column Configuration -->
      <div class="mt-6">
        <label class="block text-sm font-medium text-neutral-700">
          Visible Columns
        </label>
        <div class="mt-2 space-y-2">
          <div class="relative flex items-start">
            <div class="flex items-center h-5">
              <input type="checkbox" class="focus:ring-primary-500 h-4 w-4 text-primary-600 border-neutral-300 rounded">
            </div>
            <div class="ml-3 text-sm">
              <label class="font-medium text-neutral-700">Column Name</label>
              <p class="text-neutral-500">Column description</p>
            </div>
          </div>
          <!-- More column options -->
        </div>
      </div>

      <!-- Display Type Selection -->
      <div class="mt-6">
        <label class="block text-sm font-medium text-neutral-700">
          Display Type
        </label>
        <select class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-neutral-300 focus:outline-none focus:ring-primary-500 focus:border-primary-500 sm:text-sm rounded-md">
          <option>Table</option>
          <option>Chart</option>
          <option>Form</option>
        </select>
      </div>
    </div>
  </div>
</div>
```

## Responsive Design

### Breakpoints
```css
/* Tailwind CSS breakpoints */
sm: 640px
md: 768px
lg: 1024px
xl: 1280px
2xl: 1536px
```

### Mobile Considerations
- Collapsible navigation menu
- Stacked layouts for small screens
- Touch-friendly interface elements
- Responsive tables with horizontal scroll
- Simplified forms for mobile input

### Accessibility
- ARIA labels for interactive elements
- Keyboard navigation support
- High contrast color options
- Screen reader compatibility
- Focus indicators for interactive elements

## Loading States
```html
<!-- Loading Spinner -->
<div class="flex items-center justify-center">
  <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500"></div>
</div>

<!-- Skeleton Loading -->
<div class="animate-pulse">
  <div class="h-4 bg-neutral-200 rounded w-3/4"></div>
  <div class="space-y-3 mt-4">
    <div class="h-4 bg-neutral-200 rounded"></div>
    <div class="h-4 bg-neutral-200 rounded w-5/6"></div>
  </div>
</div>
```

## Error States
```html
<!-- Error Message -->
<div class="rounded-md bg-error-50 p-4">
  <div class="flex">
    <div class="flex-shrink-0">
      <svg class="h-5 w-5 text-error-400" viewBox="0 0 20 20" fill="currentColor">
        <!-- Error icon -->
      </svg>
    </div>
    <div class="ml-3">
      <h3 class="text-sm font-medium text-error-800">
        Error Title
      </h3>
      <div class="mt-2 text-sm text-error-700">
        <p>Detailed error message</p>
      </div>
    </div>
  </div>
</div>