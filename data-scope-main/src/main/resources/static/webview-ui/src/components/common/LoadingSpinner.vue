<script setup lang="ts">
// 定义组件属性
const props = withDefaults(defineProps<{
  size?: 'sm' | 'md' | 'lg' | 'xl' // 尺寸
  color?: 'primary' | 'secondary' | 'white' // 颜色
  fullScreen?: boolean // 是否全屏显示
  text?: string // 加载文本
}>(), {
  size: 'md',
  color: 'primary',
  fullScreen: false,
  text: ''
})

// 计算尺寸类
const sizeClass = {
  sm: 'w-4 h-4',
  md: 'w-8 h-8',
  lg: 'w-12 h-12',
  xl: 'w-16 h-16'
}

// 计算颜色类
const colorClass = {
  primary: 'text-blue-600',
  secondary: 'text-gray-600',
  white: 'text-white'
}
</script>

<template>
  <div 
    :class="[
      'flex items-center justify-center',
      { 'fixed inset-0 bg-gray-900 bg-opacity-50 z-50': fullScreen }
    ]"
  >
    <div class="flex flex-col items-center">
      <svg
        :class="[sizeClass[size], colorClass[color], 'animate-spin']"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
      >
        <circle
          class="opacity-25"
          cx="12"
          cy="12"
          r="10"
          stroke="currentColor"
          stroke-width="4"
        ></circle>
        <path
          class="opacity-75"
          fill="currentColor"
          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
        ></path>
      </svg>
      <span 
        v-if="text" 
        class="mt-2 text-sm" 
        :class="colorClass[color]"
      >
        {{ text }}
      </span>
    </div>
  </div>
</template>