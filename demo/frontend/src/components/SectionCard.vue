<template>
  <section class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 md:p-8 hover:shadow-md transition-shadow duration-300">
    <h2 class="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
      <div class="w-1.5 h-6 bg-gradient-to-b from-bento-orange to-bento-yellow rounded-full"></div>
      {{ title }}
    </h2>
    <slot />
  </section>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
})
</script>

