<template>
  <TheNavigation />
  <router-view />
  <TheFooter />
</template>

<script setup lang="ts">
import TheNavigation from './components/layout/TheNavigation.vue'
import TheFooter from './components/layout/TheFooter.vue'
</script>